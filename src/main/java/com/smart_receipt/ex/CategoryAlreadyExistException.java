package com.smart_receipt.ex;

public class CategoryAlreadyExistException extends RuntimeException {

    public CategoryAlreadyExistException(String message){
        super(message);
    }
}
