package com.smart_receipt.ex;

public class UserUnauthorizedException extends RuntimeException {

    public UserUnauthorizedException(String message){
        super(message);
    }
}
