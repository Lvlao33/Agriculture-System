# -*- coding: utf-8 -*-
import requests
import time

BASE_URL = "http://localhost:8080/api"

def test_full_business_scenario():
    print("----- 开始执行 S-001: 完整业务链路测试 (发布->查看->评论) -----")
    
    # -------------------------------------------
    # 步骤 1: 专家发布知识
    # -------------------------------------------
    print("\n[步骤 1] 模拟专家发布知识...")
    publish_payload = {
        "title": "S-001 全链路测试知识",
        "content": "这是一篇用于测试全流程的文章：发布-查看-评论。",
        "picPath": "scenario_test.jpg",
        "url": "http://s001.test.com"
    }
    
    try:
        pub_resp = requests.post(f"{BASE_URL}/knowledge", json=publish_payload)
        pub_data = pub_resp.json()
        
        if not pub_data.get('flag'):
            print(f"[失败] 发布失败: {pub_data.get('message')}")
            return

        # 获取 ID (适配你的后端返回结构，先尝试直接获取，不行再尝试从data获取)
        new_id = pub_data.get('data', {}).get('knowledgeId')
        if not new_id:
             new_id = pub_data.get('data', {}).get('id')
             
        print(f"[成功] 知识发布完成， ID: {new_id}")
        
        # -------------------------------------------
        # 步骤 2: 普通用户查看详情 (验证浏览)
        # -------------------------------------------
        print("\n[步骤 2] 模拟用户访问详情页...")
        # 稍微等待一下，确保数据库落盘
        time.sleep(0.5)
        
        detail_resp = requests.get(f"{BASE_URL}/knowledge/detail/{new_id}")
        
        # 兼容旧接口
        if detail_resp.status_code == 404:
            detail_resp = requests.get(f"{BASE_URL}/knowledge/selectById/{new_id}")

        if detail_resp.status_code == 200:
            print(f"[成功] 详情页访问正常 (HTTP 200)")
        else:
            print(f"[失败] 详情页访问错误: {detail_resp.status_code}")
            return

        # -------------------------------------------
        # 步骤 3: 用户发表评论
        # -------------------------------------------
        print("\n[步骤 3] 模拟用户发表评论...")
        comment_text = "自动评论测试"
        # 注意：这里使用你文档中提到的接口格式 /addByKnowledge/{id}/{content}
        comment_url = f"{BASE_URL}/knowledge/addByKnowledge/{new_id}/{comment_text}"
        
        # 因为接口参数在URL里，通常是POST请求，body可以为空
        comment_resp = requests.post(comment_url)
        
        if comment_resp.json().get('flag'):
             print(f"[成功] 评论发表成功")
        else:
             print(f"[失败] 评论发表失败: {comment_resp.json().get('message')}")

        # -------------------------------------------
        # 步骤 4: 验证评论是否显示
        # -------------------------------------------
        print("\n[步骤 4] 验证评论列表...")
        time.sleep(0.5) # 给一点时间写入
        list_url = f"{BASE_URL}/knowledge/selectByKnowledge/{new_id}"
        list_resp = requests.get(list_url)
        
        # --- 修复部分 START ---
        json_data = list_resp.json()
        # 这里的 or [] 就是为了防止后端返回 data: null
        comments = json_data.get('data') or []
        # --- 修复部分 END ---
        
        print(f"DEBUG: 当前获取到的评论列表: {comments}")

        found = False
        for c in comments:
            # 根据你后端返回的评论字段，可能是 content 也可能是其他，这里做个容错
            # 有时候后端返回的是字符串，有时候是对象
            if isinstance(c, str):
                c_content = c
            else:
                c_content = c.get('content', '')
                
            if comment_text in c_content:
                found = True
                break
        
        if found:
            print(f"[通过] S-001 测试完成：新发布的知识被成功评论，且评论已显示在列表中。")
        else:
            print(f"[失败] 评论发表提示成功，但在列表中未找到对应内容。")

    except Exception as e:
        print(f"[异常] 链路测试中断: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_full_business_scenario()