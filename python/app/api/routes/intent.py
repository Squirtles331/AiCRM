"""意向分类接口：POST /ai/intent"""
import json

from fastapi import APIRouter, Depends

from app.schemas.intent import IntentRequest, IntentResponse
from app.services.llm_gateway import LLMProvider, get_provider

router = APIRouter()


@router.post("/intent", response_model=IntentResponse)
async def classify_intent(req: IntentRequest, provider: LLMProvider = Depends(get_provider)):
    """识别客户意向：quote/sample/selection/other"""
    messages = [{"role": m.role, "content": m.content} for m in req.messages]
    raw = await provider.chat(messages)
    # TODO(M3): 结构化输出校验与重试；mock 阶段直接解析 JSON
    data = json.loads(raw) if raw.strip().startswith("{") else {"intent": "other", "confidence": 0.5,
                                                                "evidence": raw, "model_version": provider.name}
    return IntentResponse(
        intent=data.get("intent", "other"),
        confidence=float(data.get("confidence", 0.5)),
        evidence=data.get("evidence", ""),
        model_version=data.get("model_version", provider.name),
    )
