from google import genai
from google.genai import types
from typing import Dict, Optional, Any
import json
from app.config.settings import (
    GEMINI_API_KEY,
    GEMINI_MODEL_NAME,
    GEMINI_TEMPERATURE,
    GEMINI_MAX_OUTPUT_TOKENS,
)

from app.prompt.rag_prompt import RAG_SYSTEM_INSTRUCTION
from app.model.chatbot_response import ChatbotResponse
from app.model.referenced_book import ReferencedBook

class GeminiService:
    def __init__(self):
        self._validate_configuration()
        self.client = genai.Client(api_key=GEMINI_API_KEY)

    def _validate_configuration(self):
        if not GEMINI_API_KEY:
            raise ValueError("GEMINI_API_KEY is not set. Please provide a valid API key.")

        if not GEMINI_MODEL_NAME:
            raise ValueError("GEMINI_MODEL_NAME is not set. Please provide a valid model name.")

    def generate_response(self, prompt: str) -> ChatbotResponse:
        if prompt is None:
            raise ValueError("Prompt cannot be None.")
        prompt = str(prompt).strip()

        if not prompt:
            raise ValueError("Prompt cannot be empty.")

        try:

            response = (
                self.client.models.generate_content(
                    model=GEMINI_MODEL_NAME,
                    contents=prompt,
                    config=types.GenerateContentConfig(
                        system_instruction=(RAG_SYSTEM_INSTRUCTION),
                        temperature=(GEMINI_TEMPERATURE),
                        max_output_tokens=(GEMINI_MAX_OUTPUT_TOKENS),
                        response_mime_type=("application/json"),
                        response_schema=(ChatbotResponse),
                        
                    ),
                )
            )

        except Exception as e:
            raise RuntimeError(f"Error generating response from Gemini API: {e}") from e

        response_text = getattr(response, "text", None,)

        if response_text is None:
            raise RuntimeError("Gemini API returned no response text")

        response_text = str(response_text).strip()

        if not response_text:
            raise RuntimeError("Gemini API returned an empty response")

        try:
            chatbot_response = (ChatbotResponse.model_validate_json(response_text))

        except Exception as e:
            raise RuntimeError("Gemini returned an invalid ChatbotResponse structure.") from e

        
        # VALIDATE ANSWER
        if chatbot_response.answer is None:

            raise RuntimeError("Gemini response does not contain an answer.")

        answer = str(chatbot_response.answer).strip()

        if not answer:
            raise RuntimeError("Gemini response contains an empty answer.")

        chatbot_response.answer = answer

        # NORMALIZE REFERENCED BOOK
        referenced_book = (chatbot_response.referenced_book)

        if referenced_book is not None:

            # Validate book_id
            if referenced_book.book_id is None:
                chatbot_response.referenced_book = (None)

            # Validate title
            elif not referenced_book.title:
                chatbot_response.referenced_book = (None)

            else:
                referenced_book.title = str(referenced_book.title).strip()

                if not referenced_book.title:
                    chatbot_response.referenced_book = (None)

        return chatbot_response
