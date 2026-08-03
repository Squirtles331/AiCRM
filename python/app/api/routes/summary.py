"""对话摘要接口：POST /ai/summary"""
from fastapi import APIRouter, Depends

from app.schemas.summary import SummaryRequest, SummaryResponse
from app.services.llm_gateway import LLMProvider, get_provider

router = APIRouter()


@router.post("/summary", response_model=SummaryResponse)
async def summary(req: SummaryRequest, provider: LLMProvider = Depends(get_provider)):
    """生成对话摘要（转人工交接包用）"""
    messages = [{"role": m.role, "content": m.content} for m in req.messages]
    messages = [{"role": "system", "content": "请用 3-5 句话总结这段销售对话：客户需求、意向、缺失信息、建议下一步。"}] + messages
    raw = await provider.chat(messages)
    return SummaryResponse(summary=raw, model_version=provider.name)
