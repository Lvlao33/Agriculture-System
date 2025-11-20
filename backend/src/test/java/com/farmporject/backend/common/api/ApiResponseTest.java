package com.farmporject.backend.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void ok() {
        ApiResponse<String> response = ApiResponse.ok("payload");

        assertTrue(response.isSuccess());
        assertEquals("payload", response.getData());
        assertEquals("OK", response.getMessage());
    }

    @Test
    void fail() {
        ApiResponse<String> response = ApiResponse.fail("boom");

        assertFalse(response.isSuccess());
        assertEquals("boom", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void isSuccess() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        assertTrue(response.isSuccess());

        response.setSuccess(false);
        assertFalse(response.isSuccess());
    }

    @Test
    void setSuccess() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setSuccess(true);
        assertTrue(response.isSuccess());
    }

    @Test
    void getMessage() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("hello");
        assertEquals("hello", response.getMessage());
    }

    @Test
    void setMessage() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("updated");
        assertEquals("updated", response.getMessage());
    }

    @Test
    void getData() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setData("value");
        assertEquals("value", response.getData());
    }

    @Test
    void setData() {
        ApiResponse<Integer> response = new ApiResponse<>();
        response.setData(100);
        assertEquals(100, response.getData());
    }
}