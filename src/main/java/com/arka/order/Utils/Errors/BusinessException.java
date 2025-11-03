package com.arka.order.Utils.Errors;

public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message);
    }
}
