package com.manage.security.helpers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;

import com.manage.security.dtos.GeneralDto;

public class GeneralHelper {

    public static Map<String, Object> buildErrors(Errors errors) {
        Map<String, Object> json = new HashMap<>();
        
        errors.getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            json.put(fieldName, "The field " + fieldName + ", " + error.getDefaultMessage());
        });

        return json;
    }

    public static ResponseEntity<GeneralDto> okRequest(String message, Object data) {
        int code = HttpStatus.OK.value();
        String buildMessage = "The request was processed: " + message;
        return ResponseEntity.status(code).body(new GeneralDto(buildMessage, code, data));
    }

    public static ResponseEntity<GeneralDto> badRequest(String message, Object data) {
        int code = HttpStatus.BAD_REQUEST.value();
        String buildMessage = "The request was neglected: " + message;
        return ResponseEntity.status(code).body(new GeneralDto(buildMessage, code, data));
    }

}
