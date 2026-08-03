"""实体抽取相关 Schema"""
from typing import Literal

from pydantic import BaseModel, Field

from app.schemas.intent import ChatMessage


class ExtractedField(BaseModel):
    field_key: Literal["scene", "qty", "budget", "lead_time", "model"]
    field_value: str
    confidence: float = Field(ge=0.0, le=1.0)


class ExtractRequest(BaseModel):
    tenant_id: int
    lead_id: int
    conversation_id: int
    messages: list[ChatMessage]


class ExtractResponse(BaseModel):
    fields: list[ExtractedField]
    model_version: str
