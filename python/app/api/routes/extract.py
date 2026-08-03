"""实体抽取接口：POST /ai/extract"""
from fastapi import APIRouter, Depends

from app.schemas.extract import ExtractRequest, ExtractResponse, ExtractedField
from app.services.llm_gateway import LLMProvider, get_provider

router = APIRouter()


@router.post("/extract", response_model=ExtractResponse)
async def extract(req: ExtractRequest, provider: LLMProvider = Depends(get_provider)):
    """抽取报价前关键字段：scene/qty/budget/lead_time/model"""
    messages = [{"role": m.role, "content": m.content} for m in req.messages]
    raw = await provider.chat(messages)
    # TODO(M3): 结构化抽取与字段校验
    _ = raw  # 框架阶段返回空抽取结果
    return ExtractResponse(fields=[], model_version=provider.name)
