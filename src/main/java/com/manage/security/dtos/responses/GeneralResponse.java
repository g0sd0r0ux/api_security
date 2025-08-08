package com.manage.security.dtos.responses;

public record GeneralResponse(
    String message,
    Integer code,
    Object data
) {

}
