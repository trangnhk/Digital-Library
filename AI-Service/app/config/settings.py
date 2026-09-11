from pathlib import Path
import os
from dotenv import load_dotenv

BASE_DIR = Path(__file__).resolve().parents[2]

# load environment variables from .env file
ENV_FILE = BASE_DIR / ".env"
load_dotenv(dotenv_path=ENV_FILE)

DATA_DIR = BASE_DIR / "data"
INDEX_DIR = DATA_DIR / "index"
METADATA_DIR = DATA_DIR / "metadata"
FAISS_INDEX_PATH = INDEX_DIR / "book_index_ivfpq.faiss"

EMBEDDING_MODEL_NAME = os.getenv("EMBEDDING_MODEL_NAME")
EMBEDDING_DIM = int(os.getenv("EMBEDDING_DIM"))

DEFAULT_TOP_K = int(os.getenv("DEFAULT_TOP_K"))
DEFAULT_NPROBE = int(os.getenv("DEFAULT_NPROBE"))

# Maximum number of conversation turns retained
MAX_HISTORY_TURNS = 5
MAX_CONVERSATION_MESSAGES = 10
MAX_DISTANCE = 0.75

# Gemini API configuration
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY")
GEMINI_MODEL_NAME = os.getenv("GEMINI_MODEL_NAME", "gemini-3.5-flash-lite")
GEMINI_TEMPERATURE = float(os.getenv("GEMINI_TEMPERATURE", "0.2"))
GEMINI_MAX_OUTPUT_TOKENS = int(os.getenv("GEMINI_MAX_OUTPUT_TOKENS", "1024"))


