"""RAG 问答相关 Schema"""
from pydantic import BaseModel, Field

from app.schemas.intent import ChatMessage


class KnowledgeDoc(BaseModel):
    """知识片段（由 Java 侧从知识库检索后传入）"""
    doc_id: int
    title: str
    content: str


class AnswerRequest(BaseModel):
    tenant_id: int
    conversation_id: int
    messages: list[ChatMessage]
    # 检索到的知识片段（RAG 上下文）
    knowledge_docs: list[KnowledgeDoc] = Field(default_factory=list)
    # 产品/竞品资料包片段
    product_docs: list[KnowledgeDoc] = Field(default_factory=list)


class AnswerResponse(BaseModel):
    answer: str
    quoted_doc_ids: list[int]
    confidence: float = Field(ge=0.0, le=1.0)
    model_version: str
