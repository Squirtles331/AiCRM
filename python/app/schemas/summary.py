"""对话摘要相关 Schema"""
from pydantic import BaseModel

from app.schemas.intent import ChatMessage


class SummaryRequest(BaseModel):
    tenant_id: int
    conversation_id: int
    messages: list[ChatMessage]


class SummaryResponse(BaseModel):
    summary: str
    model_version: str
