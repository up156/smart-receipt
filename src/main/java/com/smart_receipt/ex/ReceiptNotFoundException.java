package com.smart_receipt.ex;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(String message){
        super(message);
    }
}
