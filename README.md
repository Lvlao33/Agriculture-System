# Agriculture-System（农业支持系统）

一个基于 Spring Boot + Vue.js 的农业支持系统，包含专家咨询、农资交易、金融支持等功能。

## 技术栈

### 后端

- Spring Boot 3.5.6
- Spring Data JPA
- MySQL
- Java 17

### 前端

- Vue.js
- Vue Router
- Less
- Axios

## 快速开始

### 后端启动

1. 确保已安装：

   - JDK 17+
   - MySQL 8.0+
   - Maven 3.8+

2. 配置数据库

   ```properties
   # 编辑 backend/src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/你的数据库名
   spring.datasource.username=你的用户名
   spring.datasource.password=你的密码
   ```

3. 启动后端服务

   ```bash
   cd backend
   # 使用 mvnw（推荐）
   ./mvnw clean install
   ./mvnw spring-boot:run

   # 或使用已安装的 maven
   mvn clean install
   mvn spring-boot:run

   # 或在 IDE 中运行 BackendApplication.java
   ```

### 前端启动

1. 确保已安装：

   - Node.js 16+
   - pnpm（推荐）或 npm

2. 安装依赖并启动

   ```bash
   cd frontend

   # 使用 pnpm（推荐）
   pnpm install
   pnpm run serve

   # 或使用 npm
   npm install
   npm run serve
   ```

## 接口测试指南

### Postman 测试方法

**步骤 1：启动你的 Spring Boot 后端**

首先，确保 Spring Boot 项目已经启动。如果你是用命令行启动：

```
mvn spring-boot:run
```

或者直接在 IDE（如 IntelliJ 或 Eclipse）中运行项目。

**步骤 2：打开 Postman**

1. 下载并安装 **Postman**（如果还没有的话），你可以从 [Postman 官网](https://www.postman.com/) 下载并安装。
2. 打开 Postman 客户端，进入主界面。

**步骤 3：配置请求**

1. **GET 请求**

假设你有一个简单的 GET 接口，比如 `GET /api/users`，用来获取用户列表。

**配置步骤：**

- 在 Postman 中，点击左上角的 `+` 按钮，创建一个新的请求。

- 选择 `GET` 请求方法。

- 在 URL 栏中输入接口地址，例如：

  ```
  http://localhost:8080/api/users
  ```

- 点击 **Send** 按钮发送请求。

**如果接口正常工作，你应该能在 Postman 中看到类似这样的响应：**

```
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com"
  },
  {
    "id": 2,
    "name": "Jane Doe",
    "email": "jane@example.com"
  }
]
```

2. **POST 请求**

假设你有一个 POST 请求接口 `POST /api/login`，用来处理登录。

**配置步骤：**

- 在 Postman 中，点击左上角的 `+` 按钮，创建一个新的请求。

- 选择 `POST` 请求方法。

- 在 URL 栏中输入接口地址，例如：

  ```
  http://localhost:8080/api/login
  ```

- 在 **Body** 标签页中选择 `raw`，然后选择 `JSON` 格式：

  ```
  {
    "username": "admin",
    "password": "123456"
  }
  ```

- 点击 **Send** 按钮发送请求。

**如果接口正常工作，你应该能看到返回的 JSON 响应，可能是这样的：**

```
{
  "status": "success",
  "token": "abc123xyz456"
}
```

3. **POST 请求（带文件）**

如果你要上传文件，比如 `POST /api/upload` 接口，选择 `form-data` 格式：

- 在 **Body** 标签页选择 `form-data`。

- 添加文件字段，比如 `file`，并选择你要上传的文件。

- 点击 **Send** 按钮。

  ***

**步骤 4：查看响应**

- 在 Postman 的 **响应区域**，你将看到接口的响应数据。
  - **状态码**：例如 `200 OK` 表示请求成功，`404` 表示接口未找到，`500` 表示服务器错误等。
  - **响应体**：根据接口返回的 JSON 或其他数据格式展示。
  - **响应头**：查看请求的响应头信息（如 `Content-Type`）。

**步骤 5：检查响应结果**

Postman 会显示请求的结果，你可以检查以下内容：

1. **状态码**：确认返回的 HTTP 状态码是否是预期的（比如 `200 OK`）。
2. **响应内容**：查看返回的 JSON 数据是否符合预期。
3. **响应时间**：Postman 还会显示请求的响应时间，帮助你评估接口性能。

**步骤 6：保存请求**

如果你经常需要测试相同的接口，Postman 允许你保存请求：

1. 在请求界面右上角，点击 **Save** 按钮。
2. 为请求命名并选择保存的位置（可以创建一个集合来组织请求）。

---

**小贴士**

1. **Authorization**：如果接口需要认证，可以在 Postman 中配置认证信息（比如 Basic Auth 或 Bearer Token）。
   - 点击 **Authorization** 标签页，选择认证方式并填写相应的认证信息。
2. **Headers**：有些接口可能需要特定的请求头，比如 `Content-Type` 或 `Authorization`，这些可以在 **Headers** 标签页中配置。
3. **环境变量**：Postman 允许你使用环境变量来管理不同环境下的接口请求（比如开发环境和生产环境的不同 URL）。

4. 响应说明

   ```json
   // 成功响应
   {
     "success": true,
     "data": {
       "loanId": 123,
       // 其他数据...
     },
     "message": "success"
   }

   // 错误响应
   {
     "success": false,
     "errorCode": "REQUIRED_FIELD_NULL",
     "message": "贷款金额不能为空"
   }
   ```

### 接口约定

1. 状态码

   - 200: 成功
   - 400: 请求参数错误
   - 401: 未授权
   - 403: 无权限
   - 404: 资源不存在
   - 500: 服务器错误

2. 通用接口
   - 用户注册: POST `/api/user/register`
   - 用户登录: POST `/api/user/login`
   - 获取专家列表: GET `/api/expert/list`
   - 发布农资: POST `/api/trade/goods`
   - 申请贷款: POST `/api/finance/loans/apply`

## 开发建议

1. IDE 配置（VS Code）：

   - 安装 Vetur (Vue 支持)
   - 安装 Spring Boot Extension Pack
   - 安装 Java Extension Pack

2. 本地开发

   - 前端服务: http://localhost:8081
   - 后端服务: http://localhost:8080

3. 调试技巧
   - 使用 Vue Devtools 调试前端
   - 使用 Postman 测试接口
   - 查看应用日志: `backend/logs/`

## 常见问题

1. 端口占用解决：

   ```bash
   # Windows
   netstat -ano | findstr 8080
   taskkill /F /PID 对应进程ID

   # Linux/Mac
   lsof -i :8080
   kill -9 对应进程ID
   ```

2. 数据库连接失败：
   - 检查 MySQL 服务状态
   - 验证数据库凭据
   - 确认数据库名称
