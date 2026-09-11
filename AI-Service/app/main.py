from fastapi import FastAPI
from app.apis.routes.chat import router as chat_router

app = FastAPI(
    title="Digital Library AI Service",
    description="AI conversational service for digital library",
    version="1.0.0"
)

app.include_router(chat_router)