"""AI 能力服务（Python FastAPI）

职责：为 Java 核心业务服务提供 AI 能力：
- POST /ai/intent    意向分类（quote/sample/selection/other）
- POST /ai/answer    RAG 问答（知识检索 + 生成，带引用）
- POST /ai/extract   实体抽取（场景/数量/预算/交期/型号等）
- POST /ai/summary   对话摘要（转人工交接包）
- GET  /health       健康检查

设计约定：
- LLM 提供商通过 LLMProvider 抽象切换（mock/openai/dashscope）
- 所有结果带 model_version 与置信度，便于 Java 侧审计回溯
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.routes import answer, extract, health, intent, summary
from app.core.config import settings

app = FastAPI(
    title="AiCRM AI 能力服务",
    description="AI获客销售系统 - AI 能力层（意向分类/RAG问答/实体抽取/对话摘要）",
    version="0.1.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(health.router, prefix="/health", tags=["health"])
app.include_router(intent.router, prefix="/ai", tags=["intent"])
app.include_router(answer.router, prefix="/ai", tags=["answer"])
app.include_router(extract.router, prefix="/ai", tags=["extract"])
app.include_router(summary.router, prefix="/ai", tags=["summary"])


@app.get("/", tags=["meta"])
def root():
    return {
        "service": "aicrm-ai-service",
        "version": app.version,
        "provider": settings.llm_provider,
    }
