package com.manage.security.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.manage.security.helpers.GeneralHelper;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex) {
        return GeneralHelper.badRequest("The data is not available", Map.of("extra_info", ex.getMessage()));
    }

}
