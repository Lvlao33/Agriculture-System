import requests
import json

BASE_URL = "http://localhost:8080/api"

def test_data_integrity():
    print("----- 开始执行 D-001: 知识发布数据一致性测试 -----")
    
    # 1. 模拟专家发布知识 [cite: 273]
    publish_url = f"{BASE_URL}/knowledge"
    payload = {
        "title": "集成测试专用标题-D001",
        "content": "测试内容：第一行\n第二行包含特殊字符!@#$",
        "picPath": "test_integrity.jpg",
        "url": "https://test.example.com"
    }
    
    try:
        # 发送 POST 请求
        response = requests.post(publish_url, json=payload)
        res_json = response.json()
        
        # 断言 1: 接口返回成功
        if not res_json.get('flag'):
            print(f"[失败] 发布接口调用失败: {res_json.get('message')}")
            return

        knowledge_data = res_json.get('data', {})
        new_id = knowledge_data.get('knowledgeId') # 假设后端返回了对象含ID
        print(f"[成功] 知识发布成功，ID: {new_id}")

        # 2. 模拟前端获取详情进行验证 [cite: 269]
        detail_url = f"{BASE_URL}/knowledge/detail/{new_id}"
        detail_resp = requests.get(detail_url)
        detail_data = detail_resp.json().get('data', {})

        # 3. 核心校验：比对发送的数据与查到的数据是否完全一致
        errors = []
        if detail_data.get('title') != payload['title']:
            errors.append(f"标题不匹配: 发送 '{payload['title']}' vs 接收 '{detail_data.get('title')}'")
        if detail_data.get('content') != payload['content']:
            errors.append(f"内容不匹配 (检查换行符或特殊字符截断)")
        if detail_data.get('picPath') != payload['picPath']:
            errors.append("图片路径丢失或篡改")

        if not errors:
            print("[通过] D-001 测试通过：所有字段在传输与存储过程中保持一致。")
        else:
            print("[失败] D-001 发现数据不一致:")
            for e in errors:
                print(f"  - {e}")

    except Exception as e:
        print(f"[异常] 测试脚本运行出错: {e}")

if __name__ == "__main__":
    test_data_integrity()