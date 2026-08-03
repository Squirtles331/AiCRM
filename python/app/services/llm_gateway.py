"""LLM 网关：统一提供商抽象（mock / openai / dashscope）

约定：
- 所有调用返回字符串；结构化输出（JSON）由调用方负责解析
- 新增提供商：实现 LLMProvider 接口并在 get_provider() 中注册
"""
import json
from abc import ABC, abstractmethod

import httpx

from app.core.config import settings


class LLMProvider(ABC):
    """LLM 提供商抽象"""

    name: str

    @abstractmethod
    async def chat(self, messages: list[dict], temperature: float = 0.3) -> str:
        """对话补全，返回文本"""


class MockProvider(LLMProvider):
    """开发环境 Mock：根据消息内容返回固定结果，保证联调可用"""

    name = "mock"

    async def chat(self, messages: list[dict], temperature: float = 0.3) -> str:
        last = messages[-1]["content"] if messages else ""
        if "报价" in last or "价格" in last:
            return json.dumps({"intent": "quote", "confidence": 0.95,
                               "evidence": "命中关键词：报价", "model_version": "mock-1.0"}, ensure_ascii=False)
        if "样本" in last or "样品" in last:
            return json.dumps({"intent": "sample", "confidence": 0.95,
                               "evidence": "命中关键词：样本", "model_version": "mock-1.0"}, ensure_ascii=False)
        if "选型" in last or "型号" in last:
            return json.dumps({"intent": "selection", "confidence": 0.95,
                               "evidence": "命中关键词：选型", "model_version": "mock-1.0"}, ensure_ascii=False)
        return json.dumps({"intent": "other", "confidence": 0.6,
                           "evidence": "未命中明确意向关键词", "model_version": "mock-1.0"}, ensure_ascii=False)


class OpenAICompatProvider(LLMProvider):
    """OpenAI 兼容接口提供商（海外 OpenAI / 国内可接入兼容网关）"""

    name = "openai"

    def __init__(self) -> None:
        self.api_key = settings.openai_api_key
        self.base_url = settings.openai_base_url
        self.model = settings.chat_model

    async def chat(self, messages: list[dict], temperature: float = 0.3) -> str:
        headers = {"Authorization": f"Bearer {self.api_key}"}
        payload = {
            "model": self.model,
            "messages": messages,
            "temperature": temperature,
        }
        async with httpx.AsyncClient(timeout=30) as client:
            resp = await client.post(
                f"{self.base_url}/chat/completions", headers=headers, json=payload
            )
            resp.raise_for_status()
            return resp.json()["choices"][0]["message"]["content"]


def get_provider() -> LLMProvider:
    """按配置返回 LLM 提供商实例"""
    provider = settings.llm_provider.lower()
    if provider == "mock":
        return MockProvider()
    return OpenAICompatProvider()
