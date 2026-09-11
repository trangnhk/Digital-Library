import os
from pathlib import Path

import mysql.connector
import pandas as pd
from dotenv import load_dotenv

from datetime import datetime


# ============================================================
# CONFIGURATION
# ============================================================

# Load .env
load_dotenv()


# ------------------------------------------------------------
# Parquet configuration
# ------------------------------------------------------------

PARQUET_PATH = Path(
    os.getenv(
        "CLEAN_BOOK_PARQUET_PATH",
        "data/clean_books.parquet",
    )
)


# ------------------------------------------------------------
# MySQL configuration
# ------------------------------------------------------------

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", "3306"))
DB_NAME = os.getenv("DB_NAME")
DB_USERNAME = os.getenv("DB_USERNAME")
DB_PASSWORD = os.getenv("DB_PASSWORD")


# ------------------------------------------------------------
# Migration configuration
# ------------------------------------------------------------

BATCH_SIZE = 1000


# ============================================================
# SQL
# ============================================================

INSERT_SQL = """
INSERT INTO documents (
    id,
    title,
    author,
    publisher,
    description,
    publish_year,
    created_date,
    premium,
    price,
    approved,
    thumbnail
)
VALUES (
    %s,
    %s,
    %s,
    %s,
    %s,
    %s,
    %s,
    %s,
    %s,
    %s,
    %s
)
"""


# ============================================================
# VALIDATION
# ============================================================

REQUIRED_COLUMNS = [
    "book_id",
    "title",
    "author",
    "publisher",
    "description",
    "date_published",
    "cover_link"
]


def validate_configuration():
    """
    Validate environment variables and migration configuration.
    """

    if not DB_NAME:
        raise ValueError(
            "DB_NAME is not configured."
        )

    if not DB_USERNAME:
        raise ValueError(
            "DB_USERNAME is not configured."
        )

    if not DB_PASSWORD:
        raise ValueError(
            "DB_PASSWORD is not configured."
        )

    if not PARQUET_PATH.exists():
        raise FileNotFoundError(
            f"Parquet file not found: {PARQUET_PATH}"
        )


def validate_dataframe(df: pd.DataFrame):
    """
    Validate the structure and basic integrity
    of clean_book.parquet.
    """

    print("\n" + "=" * 60)
    print("VALIDATING PARQUET DATA")
    print("=" * 60)

    # --------------------------------------------------------
    # Check required columns
    # --------------------------------------------------------

    missing_columns = [
        column
        for column in REQUIRED_COLUMNS
        if column not in df.columns
    ]

    if missing_columns:
        raise ValueError(
            f"Missing required columns: {missing_columns}"
        )

    print("✓ Required columns exist")


    # --------------------------------------------------------
    # Check empty dataset
    # --------------------------------------------------------

    if df.empty:
        raise ValueError(
            "Parquet file contains no records."
        )

    print(f"✓ Rows: {len(df):,}")


    # --------------------------------------------------------
    # Check book_id
    # --------------------------------------------------------

    if df["book_id"].isna().any():
        null_count = int(
            df["book_id"].isna().sum()
        )

        raise ValueError(
            f"book_id contains {null_count} NULL values."
        )

    print("✓ No NULL book_id")


    # --------------------------------------------------------
    # Check duplicate book_id
    # --------------------------------------------------------

    duplicate_count = int(
        df["book_id"].duplicated().sum()
    )

    if duplicate_count > 0:
        raise ValueError(
            f"book_id contains {duplicate_count} duplicates."
        )

    print("✓ No duplicate book_id")


    # --------------------------------------------------------
    # Check title
    # --------------------------------------------------------

    if df["title"].isna().any():
        null_count = int(
            df["title"].isna().sum()
        )

        print(
            f"⚠ title contains {null_count} NULL values"
        )
    else:
        print("✓ No NULL title")


    # --------------------------------------------------------
    # Display ID range
    # --------------------------------------------------------

    print(
        f"First book_id : {df['book_id'].min()}"
    )

    print(
        f"Last book_id  : {df['book_id'].max()}"
    )


# ============================================================
# DATABASE
# ============================================================

def create_connection():
    """
    Create MySQL database connection.
    """

    print("\n" + "=" * 60)
    print("CONNECTING TO MYSQL")
    print("=" * 60)

    connection = mysql.connector.connect(
        host=DB_HOST,
        port=DB_PORT,
        database=DB_NAME,
        user=DB_USERNAME,
        password=DB_PASSWORD,
    )

    print("✓ MySQL connection established")

    return connection


# ============================================================
# TRANSFORMATION
# ============================================================

def dataframe_to_records(batch_df: pd.DataFrame):
    """
    Convert a DataFrame batch into tuples
    matching INSERT_SQL.

    Mapping:

        book_id        → document.id
        title          → document.title
        author         → document.author
        publisher      → document.publisher
        description    → document.description
        publisher_year → document.publisher_year
    """
    batch_df = batch_df.astype(object).where(pd.notnull(batch_df), None)
    current_time = datetime.now()

    records = []
    

    for _, row in batch_df.iterrows():

        record = (
            row["book_id"],
            row["title"],
            row["author"],
            row["publisher"],
            row["description"],
            row["date_published"],
            current_time,
            0,
            0.0,
            1,
            row["cover_link"]
        )

        records.append(record)

    return records


# ============================================================
# MIGRATION
# ============================================================

def migrate(df: pd.DataFrame, connection):
    """
    Insert all books into MySQL using batches.
    """

    cursor = connection.cursor()

    total_rows = len(df)
    total_inserted = 0

    print("\n" + "=" * 60)
    print("STARTING MIGRATION")
    print("=" * 60)

    try:

        for start in range(
            0,
            total_rows,
            BATCH_SIZE,
        ):

            end = min(
                start + BATCH_SIZE,
                total_rows,
            )

            batch_df = df.iloc[start:end]

            # ------------------------------------------------
            # Convert DataFrame → records
            # ------------------------------------------------

            records = dataframe_to_records(
                batch_df
            )

            # ------------------------------------------------
            # Insert batch
            # ------------------------------------------------

            cursor.executemany(
                INSERT_SQL,
                records,
            )

            # ------------------------------------------------
            # Commit batch
            # ------------------------------------------------

            connection.commit()

            total_inserted += len(records)

            print(
                f"Inserted "
                f"{total_inserted:,} / "
                f"{total_rows:,}"
            )

    except Exception:

        connection.rollback()

        print(
            "\n✗ Migration failed."
        )

        print(
            "The current transaction was rolled back."
        )

        raise

    finally:

        cursor.close()

    print("\n✓ Migration completed")

    return total_inserted


# ============================================================
# VERIFICATION
# ============================================================

def verify_database(
    df: pd.DataFrame,
    connection,
):
    """
    Verify the number of migrated books
    in MySQL against the Parquet dataset.
    """

    print("\n" + "=" * 60)
    print("VERIFYING MIGRATION")
    print("=" * 60)

    cursor = connection.cursor()

    try:

        cursor.execute(
            "SELECT COUNT(*) FROM documents"
        )

        result = cursor.fetchone()

        mysql_count = int(result[0])

        parquet_count = int(
            df["book_id"].nunique()
        )

        print(
            f"Parquet unique books : "
            f"{parquet_count:,}"
        )

        print(
            f"MySQL document rows  : "
            f"{mysql_count:,}"
        )

        if mysql_count != parquet_count:

            raise RuntimeError(
                "Verification failed: "
                "Parquet count does not match "
                "MySQL document count."
            )

        print(
            "✓ Migration verification passed"
        )

    finally:

        cursor.close()


# ============================================================
# PREVIEW
# ============================================================

def preview_data(df: pd.DataFrame):
    """
    Display a small preview before migration.
    """

    print("\n" + "=" * 60)
    print("DATA PREVIEW")
    print("=" * 60)

    print(
        df[
            [
                "book_id",
                "title",
                "author",
                "publisher",
                "date_published",
            ]
        ].head(5).to_string(index=False)
    )


# ============================================================
# MAIN
# ============================================================

def main():

    connection = None

    try:

        # ----------------------------------------------------
        # 1. Validate configuration
        # ----------------------------------------------------

        validate_configuration()


        # ----------------------------------------------------
        # 2. Read Parquet
        # ----------------------------------------------------

        print("\n" + "=" * 60)
        print("READING PARQUET")
        print("=" * 60)

        df = pd.read_parquet(
            PARQUET_PATH,
            columns=REQUIRED_COLUMNS,
        )

        print(
            f"✓ Loaded {len(df):,} rows"
        )


        # ----------------------------------------------------
        # 3. Validate data
        # ----------------------------------------------------

        validate_dataframe(df)


        # ----------------------------------------------------
        # 4. Preview
        # ----------------------------------------------------

        preview_data(df)


        # ----------------------------------------------------
        # 5. Connect MySQL
        # ----------------------------------------------------

        connection = create_connection()


        # ----------------------------------------------------
        # 6. Run migration
        # ----------------------------------------------------

        migrate(
            df=df,
            connection=connection,
        )


        # ----------------------------------------------------
        # 7. Verify migration
        # ----------------------------------------------------

        verify_database(
            df=df,
            connection=connection,
        )


        print("\n" + "=" * 60)
        print("MIGRATION SUCCESSFUL")
        print("=" * 60)


    except Exception as e:

        print("\n" + "=" * 60)
        print("MIGRATION FAILED")
        print("=" * 60)

        print(
            f"{type(e).__name__}: {e}"
        )

        raise


    finally:

        if connection is not None:

            connection.close()

            print(
                "\n✓ MySQL connection closed"
            )


# ============================================================
# ENTRY POINT
# ============================================================

if __name__ == "__main__":
    main()