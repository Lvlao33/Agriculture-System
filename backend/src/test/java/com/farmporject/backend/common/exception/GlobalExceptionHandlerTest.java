package com.farmporject.backend.common.exception;

import com.farmporject.backend.common.api.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidation() throws NoSuchMethodException {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error = new FieldError("user", "username", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error));

        Method method = Dummy.class.getDeclaredMethod("dummy", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ApiResponse);
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("username: must not be blank", body.getMessage());
    }

    @Test
    void handle() {
        Exception ex = new RuntimeException("boom");

        ResponseEntity<Object> response = handler.handle(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ApiResponse<?> body = (ApiResponse<?>) response.getBody();
        assertFalse(body.isSuccess());
        assertEquals("boom", body.getMessage());
    }

    private static class Dummy {
        @SuppressWarnings("unused")
        void dummy(String input) {
        }
    }
}