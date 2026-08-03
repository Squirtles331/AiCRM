"""意向分类相关 Schema"""
from typing import Literal

from pydantic import BaseModel, Field


class ChatMessage(BaseModel):
    role: Literal["system", "user", "assistant"]
    content: str


class IntentRequest(BaseModel):
    tenant_id: int
    conversation_id: int
    messages: list[ChatMessage]


class IntentResponse(BaseModel):
    intent: Literal["quote", "sample", "selection", "other"]
    confidence: float = Field(ge=0.0, le=1.0)
    evidence: str
    model_version: str
