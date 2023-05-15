package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ArticleExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ArticleNotFoundException.class})
    protected ResponseEntity sitemapNotFoundException(RuntimeException runtimeException, WebRequest webRequest){
        return handleExceptionInternal(runtimeException, runtimeException.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, webRequest);
    }

}
