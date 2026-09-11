import pandas as pd

from app.config.settings import METADATA_DIR


def main():
    metadata_file = next(METADATA_DIR.glob("*.parquet"))

    df = pd.read_parquet(metadata_file)

    print("Metadata file:")
    print(metadata_file)

    print("\nColumns:")
    print(df.columns.tolist())

    print("\nShape:")
    print(df.shape)

    print("\nFirst rows:")
    print(df.head())


if __name__ == "__main__":
    main()