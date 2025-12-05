# 删除问题的 API 使用方法

## API 端点
```
DELETE http://localhost:8080/api/qa/questions/{id}
```

## 使用 curl 命令（在终端执行）

### Windows PowerShell:
```powershell
# 删除问题 ID = 10
Invoke-WebRequest -Uri "http://localhost:8080/api/qa/questions/10" -Method DELETE
```

### Windows CMD 或 Linux/Mac:
```bash
# 删除问题 ID = 10
curl -X DELETE http://localhost:8080/api/qa/questions/10
```

## 使用 Postman
1. 方法选择：DELETE
2. URL：`http://localhost:8080/api/qa/questions/10`
3. 点击 Send

## 使用浏览器（需要安装插件）
安装 "REST Client" 或类似插件，然后发送 DELETE 请求

## 响应格式
成功：
```json
{
  "flag": true,
  "message": "问题删除成功"
}
```

失败：
```json
{
  "flag": false,
  "message": "删除问题失败: ..."
}
```

