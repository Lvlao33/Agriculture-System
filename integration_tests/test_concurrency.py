import requests
import threading
import time

BASE_URL = "http://localhost:8080/api"
KNOWLEDGE_ID = 1  # 请替换为数据库中真实存在的 ID
CONCURRENT_USERS = 50 # 模拟50人同时操作

def simulate_view(k_id):
    # 模拟调用详情接口，触发浏览量+1 [cite: 269]
    try:
        requests.get(f"{BASE_URL}/knowledge/detail/{k_id}")
    except:
        pass

def test_concurrency_safety():
    print(f"----- 开始执行 G-001: 全局计数器并发安全测试 (模拟{CONCURRENT_USERS}人) -----")
    
    # 1. 获取初始浏览量
    initial_resp = requests.get(f"{BASE_URL}/knowledge/detail/{KNOWLEDGE_ID}")
    initial_count = initial_resp.json().get('data', {}).get('viewCount', 0)
    print(f"初始浏览量: {initial_count}")

    # 2. 发起并发请求
    threads = []
    for _ in range(CONCURRENT_USERS):
        t = threading.Thread(target=simulate_view, args=(KNOWLEDGE_ID,))
        threads.append(t)
        t.start()

    # 等待所有请求完成
    for t in threads:
        t.join()
    
    # 3. 验证最终结果
    # 给一点缓冲时间让数据库提交事务
    time.sleep(1)
    final_resp = requests.get(f"{BASE_URL}/knowledge/detail/{KNOWLEDGE_ID}")
    final_count = final_resp.json().get('data', {}).get('viewCount', 0)
    
    print(f"最终浏览量: {final_count}")
    
    expected_min = initial_count + CONCURRENT_USERS
    # 注意：如果是分布式缓存，可能会有延迟，这里主要验证不丢失严重
    if final_count >= expected_min:
         print(f"[通过] 计数逻辑在并发下安全。增量 {final_count - initial_count} >= 预期 {CONCURRENT_USERS}")
    else:
         print(f"[警告/失败] 发现计数丢失！预期至少 {expected_min}，实际 {final_count}。可能存在线程安全问题。")

if __name__ == "__main__":
    test_concurrency_safety()