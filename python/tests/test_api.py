"""健康检查与意向分类接口测试"""
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_health():
    resp = client.get("/health")
    assert resp.status_code == 200
    assert resp.json() == {"status": "ok"}


def test_intent_quote():
    resp = client.post("/ai/intent", json={
        "tenant_id": 1,
        "conversation_id": 1,
        "messages": [{"role": "user", "content": "你们的产品怎么报价？"}],
    })
    assert resp.status_code == 200
    body = resp.json()
    assert body["intent"] == "quote"
    assert 0 <= body["confidence"] <= 1


def test_intent_selection():
    resp = client.post("/ai/intent", json={
        "tenant_id": 1,
        "conversation_id": 2,
        "messages": [{"role": "user", "content": "帮我选型，需要什么型号的？"}],
    })
    assert resp.status_code == 200
    assert resp.json()["intent"] == "selection"
