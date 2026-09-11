from pathlib import Path
import pandas as pd
from app.config.settings import METADATA_DIR

class MetadataService:
    def __init__(self):
        self.metadata_dir = Path(METADATA_DIR)
        if not self.metadata_dir.exists():
            raise FileNotFoundError(f"Metadata directory {METADATA_DIR} does not exist.")
        
        self.metadata_ranges = self._build_metadata_ranges()

    def _build_metadata_ranges(self):
        metadata_ranges = []

        parquet_files = sorted(self.metadata_dir.glob("*.parquet"))
        if not parquet_files:
            raise FileNotFoundError(f"No Parquet files found in metadata directory {METADATA_DIR}.")

        for parquet_file in parquet_files:
            df_ids = pd.read_parquet(parquet_file, columns=['faiss_id'])

            if df_ids.empty:
                continue  # Skip empty files

            start_id = df_ids['faiss_id'].min()
            end_id = df_ids['faiss_id'].max()

            metadata_ranges.append({
                "path": parquet_file,
                "start_id": start_id,
                "end_id": end_id
            })
        metadata_ranges.sort(key=lambda x: x['start_id'])
        return metadata_ranges

    def find_metadata_file(self, faiss_id: int):
        for metadata in self.metadata_ranges:
            if metadata['start_id'] <= faiss_id <= metadata['end_id']:
                return metadata['path']
        raise KeyError(f"No metadata file found for FAISS ID {faiss_id}.")

    def get_metadata_by_faiss_id(self, faiss_ids, columns=None):
        if faiss_ids is None:
            raise ValueError("faiss_ids must be a non-empty list.")

        faiss_ids = [
            int(faiss_id)
            for faiss_id in faiss_ids
            if int(faiss_id) >= 0
        ]
        # Group requested FAISS IDs by their
        # corresponding Parquet file.
        ids_by_file = {}

        for faiss_id in faiss_ids:
            metadata_file = self.find_metadata_file(faiss_id)
            ids_by_file.setdefault(metadata_file, []).append(faiss_id)

        result_frames = []

        for metadata_file, file_faiss_ids in ids_by_file.items():
            df = pd.read_parquet(metadata_file, columns=columns)
            df = df[df['faiss_id'].isin(file_faiss_ids)]
            if not df.empty:
                result_frames.append(df)
            if not result_frames:
                return pd.DataFrame()

        result = pd.concat(result_frames, ignore_index=True)
        return result