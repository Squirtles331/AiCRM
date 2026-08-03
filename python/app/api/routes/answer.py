"""RAG 问答接口：POST /ai/answer"""
from fastapi import APIRouter, Depends

from app.schemas.answer import AnswerRequest, AnswerResponse
from app.services.llm_gateway import LLMProvider, get_provider

router = APIRouter()


@router.post("/answer", response_model=AnswerResponse)
async def answer(req: AnswerRequest, provider: LLMProvider = Depends(get_provider)):
    """基于知识片段回答问题，返回引用文档 ID 与置信度"""
    # TODO(M3): 接入真实 RAG（知识检索在 Java 侧或本服务内实现）
    context = "\n".join(f"[{d.doc_id}] {d.title}\n{d.content}" for d in req.knowledge_docs)
    messages = [
        {"role": "system",
         "content": "你是 B2B 销售助手。只能基于提供的知识片段回答，不要编造。引用格式标注 [文档ID]。"},
        {"role": "user", "content": f"知识片段：\n{context}\n\n客户问题：{req.messages[-1].content}"}
    ] if context else req.messages
    raw = await provider.chat(messages)
    quoted = [d.doc_id for d in req.knowledge_docs][:5]
    return AnswerResponse(answer=raw, quoted_doc_ids=quoted, confidence=0.9, model_version=provider.name)
