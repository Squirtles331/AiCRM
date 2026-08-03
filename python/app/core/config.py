"""应用配置（pydantic-settings）"""
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    # 服务
    ai_service_host: str = "0.0.0.0"
    ai_service_port: int = 8100

    # LLM Provider: mock / openai / dashscope
    llm_provider: str = "mock"

    # OpenAI 兼容接口
    openai_api_key: str = ""
    openai_base_url: str = "https://api.openai.com/v1"
    openai_model: str = "gpt-4o-mini"

    # 阿里云百炼（国内）
    dashscope_api_key: str = ""
    dashscope_model: str = "qwen-plus"

    # 任务级模型兜底
    llm_model_fallback: str = ""

    @property
    def chat_model(self) -> str:
        if self.llm_model_fallback:
            return self.llm_model_fallback
        if self.llm_provider == "openai":
            return self.openai_model
        return self.dashscope_model


settings = Settings()
