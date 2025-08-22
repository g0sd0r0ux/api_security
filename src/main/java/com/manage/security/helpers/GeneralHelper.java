package com.manage.security.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.security.dtos.responses.GeneralResponse;

import jakarta.servlet.http.HttpServletResponse;

public class GeneralHelper {

    public static Map<String, Object> buildErrors(Errors errors) {
        Map<String, Object> json = new HashMap<>();
        
        errors.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            json.put(fieldName, "The field " + fieldName + ", " + error.getDefaultMessage());
        });

        return json;
    }

    public static ResponseEntity<GeneralResponse> okRequest(String message, Object data) {
        int code = HttpStatus.OK.value();
        String buildMessage = "The request was processed: " + message;
        return ResponseEntity.status(code).body(new GeneralResponse(buildMessage, code, data));
    }

    public static ResponseEntity<GeneralResponse> badRequest(String message, Object data) {
        int code = HttpStatus.BAD_REQUEST.value();
        String buildMessage = "The request was neglected: " + message;
        return ResponseEntity.status(code).body(new GeneralResponse(buildMessage, code, data));
    }

    public static void unauthorized(HttpServletResponse response, String message) 
        throws JsonProcessingException, IOException
    {
        int code = HttpStatus.UNAUTHORIZED.value();
        GeneralResponse responseObj = new GeneralResponse(message, code, null);
        response.setContentType("application/json");
        response.setStatus(code);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseObj));
    }

    public static void expectationFailed(HttpServletResponse response, String message)
        throws JsonProcessingException, IOException
    {
        int code = HttpStatus.EXPECTATION_FAILED.value();
        GeneralResponse responseObj = new GeneralResponse(message, code, null);
        response.setContentType("application/json");
        response.setStatus(code);
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseObj));
    }

    public static boolean isNullOrBlank(String field) {
        return field == null || field.isBlank();
    }

}
