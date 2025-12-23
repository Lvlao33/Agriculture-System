# JWT Token 系统实现总结

## 🎯 实现目标
将原有的简单token系统（`tk_userId_username`）替换为安全的JWT（JSON Web Token）认证系统，支持token过期验证、自动token注入和角色权限检查。

---

## ✅ 已完成工作清单

### 后端实现（Backend）

#### 1. **JWT 依赖管理** - `pom.xml`
```xml
<!-- JJWT 0.12.3 库（兼容 Spring Boot 3.5.6） -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

#### 2. **JWT 配置工具类** - `JwtConfig.java`
- **位置**: `backend/src/main/java/com/farmporject/backend/config/JwtConfig.java`
- **功能**:
  - `generateToken(userId, username, role)` - 生成JWT token
  - `validateToken(token)` - 验证token有效性
  - `getUserIdFromToken()` - 提取userId claim
  - `getUsernameFromToken()` - 提取username claim
  - `getRoleFromToken()` - 提取role claim
  - `extractTokenFromHeader(authHeader)` - 从Authorization header中提取Bearer token
- **Token 有效期**: 13小时（46800000毫秒）
- **签名算法**: HS512（HMAC with SHA-512）
- **API 版本**: 采用 JJWT 0.12.3 新 API（`Jwts.builder()`, `parser().verifyWith()`, `parseSignedClaims()`）

#### 3. **用户上下文管理** - `UserContext.java`
- **位置**: `backend/src/main/java/com/farmporject/backend/security/UserContext.java`
- **功能**:
  - ThreadLocal 模式存储当前请求的用户信息
  - `setCurrentUser(userId, username, role)` - 设置当前用户
  - `getCurrentUserId()`, `getCurrentUsername()`, `getCurrentRole()` - 获取用户信息
  - `isAuthenticated()` - 检查认证状态
  - `hasRole(role)` - 检查权限
  - `clear()` - 清理线程本地数据
- **用途**: 业务逻辑中可直接调用 `UserContext.getCurrentUserId()` 获取用户ID，无需通过参数传递

#### 4. **全局认证过滤器** - `JwtAuthenticationFilter.java`
- **位置**: `backend/src/main/java/com/farmporject/backend/security/JwtAuthenticationFilter.java`
- **功能**: 拦截所有请求，验证JWT token
- **公开路由**（无需token验证）:
  - `/api/auth/login` - 登录
  - `/api/auth/register` - 注册
  - `/api/user/forgetPassword` - 忘记密码
  - `/api/experts` - 专家列表
  - `/api/knowledge` - 知识库
  - `/api/finance/loan-products` - 贷款产品
  - `/api/trade/products` - 商品列表
  - `/api/qa/questions` - 问题列表
  - `/actuator/health`, `/img`, `/order`, `/kn` - 其他公开资源
- **受保护路由**: 所有其他路由需要有效的JWT token
- **流程**:
  1. 检查是否是公开路由
  2. 如果是受保护路由，提取并验证token
  3. 将用户信息存储到 UserContext ThreadLocal
  4. 请求完成后清理ThreadLocal

#### 5. **认证控制器更新** - `AuthController.java`
- **修改内容**:
  - 登录方法改为调用 `jwtConfig.generateToken(userId, username, role)`
  - 返回JWT token而非简单字符串
  - 角色标准化：`FARMER/EXPERT/BANK` → `farmer/expert/bank`
  - 添加 `normalizeRole(rawRole)` 和 `getRoleDisplayName()` 辅助方法
- **响应格式**:
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "username": "farmer_user",
      "nickname": "农户",
      "avatar": "url_to_avatar"
    },
    "role": "farmer",
    "roleName": "农户"
  }
  ```

#### 6. **JWT 配置属性** - `application.properties`
```properties
jwt.secret=AgricultureSystemJwtSecret2025AgricultureSystemJwtSecret2025
jwt.expiration=46800000
```
**⚠️ 生产环境提醒**: 应使用强随机密钥替换当前测试密钥

---

### 前端实现（Frontend）

#### 1. **Token 管理器** - `tokenManager.js`
- **位置**: `frontend/src/utils/tokenManager.js`
- **功能**:
  - `setToken(token, user, role)` - 保存token和用户信息到localStorage
  - `getToken()` - 获取token
  - `getRole()` - 获取用户角色
  - `getUser()`, `getUserId()`, `getUsername()` - 获取用户信息
  - `isTokenValid()` - 检查token是否有效（非空且存在）
  - `isAuthenticated()` - 检查认证状态
  - `clear()` - 清除所有本地存储数据
  - `hasRole(role)`, `isFarmer()`, `isExpert()`, `isBank()` - 权限检查
- **localStorage 键名**:
  - `jwtToken` - JWT token
  - `user` - 用户对象（JSON string）
  - `userRole` - 用户角色
  - `username` - 用户名
  - `userId` - 用户ID

#### 2. **请求拦截器** - `request.js`
- **位置**: `frontend/src/utils/request.js`
- **功能**:
  - **请求拦截**: 自动从tokenManager获取token，添加到 `Authorization: Bearer {token}` header
  - **响应拦截**:
    - `401 Unauthorized`: Token过期/无效 → 清除本地token → 重定向到 `/login`
    - `403 Forbidden`: 无权限访问资源 → 显示错误提示
    - `404 Not Found`: 资源不存在 → 显示错误提示
    - `500 Server Error`: 服务器错误 → 显示错误提示
- **日志记录**: 
  - ✓ Token自动添加成功
  - ⚠️ Token过期/无效
  - ✗ 请求失败

#### 3. **路由守卫** - `router/index.js` 的 `beforeEach`
- **导入**: 添加 `{ isTokenValid, getRole, clear } from '../utils/tokenManager'`
- **功能**:
  - **公开路由白名单**: `/login`, `/register`, `/forget`, `/home/front`, `/home/goods`, `/home/knowledge`, `/home/guide`, `/home/allExpert`
  - **步骤1**: 检查非公开路由的token有效性 → token无效则清除并重定向到登录页
  - **步骤2**: 统一处理 `/home` 路由重定向到 `/home/front`
  - **步骤3**: 检查路由的角色权限（通过 `route.meta.roles`）→ 无权限则重定向到首页
  - **步骤4**: 允许导航

#### 4. **Vuex Store 更新** - `store/index.js`
- **导入**: 添加 `{ getRole, clear as clearToken } from '../utils/tokenManager'`
- **新增 mutation**:
  - `syncRoleFromToken(state)` - 从tokenManager同步角色到store
- **改进 mutation**:
  - `removeStorage(state)` - 现在调用 `clearToken()` 清除tokenManager数据
- **新增 action**:
  - `restoreUserState({ commit })` - 应用启动时从localStorage恢复用户状态
  - `clearAllUserData({ commit })` - 清除所有用户数据

#### 5. **登录页面更新** - `Login.vue`
- **导入**: 添加 `import { setToken } from "../utils/tokenManager"`
- **修改登录成功处理**:
  1. 调用 `setToken(token, user, role)` 将token存储到tokenManager和localStorage
  2. 同时更新Vuex store保持兼容
  3. 根据role获取目标首页并导航

---

## 🔄 认证流程

### 登录流程
```
1. 用户输入用户名/密码 → Login.vue
2. 调用 /api/auth/login → AuthController
3. 验证凭据 → 生成 JWT token
4. 返回 {token, user, role}
5. Login.vue 调用 setToken() → 存储到tokenManager和localStorage
6. 调用 this.$router.push(target) → 跳转到角色对应页面
```

### 请求流程
```
1. 任何API请求通过 request.js
2. 请求拦截器: 从tokenManager获取token → 添加Authorization header
3. 请求发送到后端: Authorization: Bearer {JWT}
4. JwtAuthenticationFilter: 提取token → 验证签名 → 存储用户信息到UserContext
5. 业务控制器: 调用 UserContext.getCurrentUserId() 获取用户信息
6. 返回响应
7. 响应拦截器: 检查状态码 → 如果401则清除token并重定向到登录页
```

### 权限检查流程
```
1. 导航到受保护路由
2. router.beforeEach 守卫触发
3. 检查token有效性 (isTokenValid)
4. 获取当前角色 (getRole)
5. 检查路由的 meta.roles
6. 角色匹配 → 允许导航
7. 角色不匹配 → 重定向到首页
```

---

## 🔐 安全特性

### Token 验证
- ✅ 签名验证: HS512 算法，只有后端知道密钥
- ✅ 过期检查: 13小时有效期
- ✅ 格式验证: Bearer token格式
- ✅ 异常处理: 捕获所有JWT异常（过期、签名错误、格式错误等）

### 请求安全
- ✅ 自动token注入: 无需手动在每个请求中添加
- ✅ 公开路由白名单: 只有明确的公开路由才无需token
- ✅ 受保护路由验证: 所有其他路由都需要有效token

### 页面权限
- ✅ 路由级权限检查: beforeEach 守卫检查token和角色
- ✅ 角色专属页面: 不同角色只能访问自己的工作页面
- ✅ 自动重定向: 无权访问则自动回到首页

---

## 📋 改动文件清单

### 后端 (Backend)
- ✅ `pom.xml` - 添加JJWT依赖
- ✅ `backend/src/main/java/com/farmporject/backend/config/JwtConfig.java` - **新建**
- ✅ `backend/src/main/java/com/farmporject/backend/security/UserContext.java` - **新建**
- ✅ `backend/src/main/java/com/farmporject/backend/security/JwtAuthenticationFilter.java` - **新建**
- ✅ `backend/src/main/java/com/farmporject/backend/user/controller/AuthController.java` - 已修改
- ✅ `backend/src/main/resources/application.properties` - 添加JWT配置

### 前端 (Frontend)
- ✅ `frontend/src/utils/tokenManager.js` - **新建**
- ✅ `frontend/src/utils/request.js` - 已修改（添加自动token注入和错误处理）
- ✅ `frontend/src/router/index.js` - 已修改（添加beforeEach守卫和token验证）
- ✅ `frontend/src/store/index.js` - 已修改（导入tokenManager，添加同步方法）
- ✅ `frontend/src/views/Login.vue` - 已修改（集成tokenManager）

---

## ✨ 编译验证

### 后端
```
[INFO] Compiling 81 source files with javac
[INFO] BUILD SUCCESS
[INFO] Total time: 5.756 s
```
✅ **零编译错误**

### 前端
```
Compiled with 6 warnings (仅为缺失finance API函数，不影响JWT功能)
```
✅ **编译成功**

---

## 🚀 下一步工作（可选）

### 如果需要完全移除userId参数依赖：
1. 修改 `CartController.java` - 从 @RequestParam Long userId 改为 UserContext.getCurrentUserId()
2. 修改 `OrderController.java` - 同上
3. 修改 `ProductController.java` - 修改seller过滤逻辑

### 如果需要生产级安全：
1. 使用强随机密钥替换 `jwt.secret`（至少256位）
2. 考虑实现 Token 刷新机制（refresh token）
3. 添加 Rate Limiting 防止暴力破解
4. 启用 HTTPS
5. 添加CORS安全策略

### 如果需要更多功能：
1. 实现权限细粒度控制（@PreAuthorize注解）
2. 添加token黑名单机制（logout时标记token失效）
3. 实现用户会话管理
4. 添加审计日志

---

## 🧪 测试检查清单

- [ ] 登录并获取JWT token
- [ ] 验证token包含 userId, username, role 三个claim
- [ ] 登录后任何API请求自动包含Authorization header
- [ ] Token过期或无效时自动重定向到登录页
- [ ] 不同角色无法访问其他角色的工作页面（自动重定向）
- [ ] 注销后清除token和用户信息
- [ ] 公开路由（知识库、专家列表等）无需token可访问
- [ ] 受保护路由（购物车、订单等）需要有效token

---

## 📚 技术栈

- **后端**: Spring Boot 3.5.6, Java 17, JJWT 0.12.3
- **前端**: Vue 2, Vuex, Axios, Vue Router
- **认证**: JWT (JSON Web Tokens)
- **签名**: HS512 (HMAC with SHA-512)
- **存储**: localStorage (客户端), ThreadLocal (服务端)

---

## 📝 注意事项

1. **Token格式**: 所有请求中token必须以 "Bearer " 前缀出现在Authorization header中
2. **角色大小写**: 后端数据库存储大写（FARMER/EXPERT/BANK），自动转换为小写（farmer/expert/bank）用于前端
3. **Token有效期**: 13小时，需要手动重新登录（可选：实现自动刷新）
4. **ThreadLocal清理**: 确保过滤器在响应完成后清理ThreadLocal，否则会导致内存泄漏
5. **公开路由白名单**: 添加新的公开路由时需要更新过滤器中的白名单

---

**实现日期**: 2025年12月10日
**实现状态**: ✅ 完全实现并通过编译验证
