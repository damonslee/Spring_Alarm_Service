package com.hanaset.sky.web.rest.advice;

import com.hanaset.sky.exception.SkyResponseException;
import com.hanaset.sky.web.rest.support.SkyApiRestSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SkyApiRestAdvice extends SkyApiRestSupport {

    @ExceptionHandler(SkyResponseException.class)
    public ResponseEntity handleSkyResponseException(SkyResponseException ex){
        return skyResponseExecption(ex.getCode(), ex.getMessage());
    }
}
