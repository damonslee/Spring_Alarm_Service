package com.hanaset.sky.exception;

public class SkyResponseException extends RuntimeException {

    private String code;

    public SkyResponseException(String code, String msg){
        super(msg);
        this.code = code;
    }

    public String getCode() { return this.code; }
}
