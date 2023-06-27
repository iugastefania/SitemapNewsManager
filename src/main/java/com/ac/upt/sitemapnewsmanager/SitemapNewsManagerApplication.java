package com.ac.upt.sitemapnewsmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SitemapNewsManagerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SitemapNewsManagerApplication.class, args);
  }
}
