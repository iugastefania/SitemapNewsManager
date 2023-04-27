package com.ac.upt.sitemapnewsmanager.exceptions;

public class ArticleNotFoundException extends RuntimeException{
    public ArticleNotFoundException(String message){
        super(message);
    }
}
