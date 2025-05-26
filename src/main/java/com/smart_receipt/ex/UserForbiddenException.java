package com.smart_receipt.ex;

public class UserForbiddenException extends RuntimeException {

    public UserForbiddenException(String message){
        super(message);
    }
}
