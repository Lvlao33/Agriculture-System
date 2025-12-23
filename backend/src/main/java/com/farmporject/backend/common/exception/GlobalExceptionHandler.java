package com.farmporject.backend.common.exception;

import com.farmporject.backend.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理器 - 合并校验与通用异常处理，并记录请求 ID
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Validation error");
        String rid = request != null ? String.valueOf(request.getAttribute("X-Request-ID")) : "-";
        System.err.println("[VALIDATION] requestId=" + rid + " message=" + msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail(msg));
    }

    // 向后兼容：保留无 request 参数的重载，供单元测试或旧代码直接调用
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        return handleValidation(ex, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex, HttpServletRequest request) {
        String rid = request != null ? String.valueOf(request.getAttribute("X-Request-ID")) : "-";
        System.err.println("[EXCEPTION] requestId=" + rid + " message=" + ex.getMessage());
        ex.printStackTrace();
        try {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.fail(ex.getMessage()));
        } catch (IllegalStateException e) {
            // 响应已提交，无法再次写入
            System.err.println("Response already committed for requestId=" + rid);
            return null;
        }
    }

    // 向后兼容：保留无 request 参数的重载
    public ResponseEntity<Object> handle(Exception ex) {
        return handle(ex, null);
    }
}
