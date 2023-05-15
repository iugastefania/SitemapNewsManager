package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @GetMapping("/getArticle")
    public ResponseEntity<Sitemap> getArticle(@RequestParam String loc){
        Sitemap sitemap = articleService.getArticle(loc);
        return new ResponseEntity<>(sitemap, HttpStatus.OK);
    }
    @PostMapping("/addArticle")
    public ResponseEntity<String> addArticle(@RequestBody Sitemap sitemap){
        articleService.addArticle(sitemap);
        return new ResponseEntity<>("Article with url: " + sitemap.toString() + " was added.", HttpStatus.OK);
    }

    @PutMapping("/updateArticle")
    public ResponseEntity<String> updateArticle(@RequestBody Sitemap sitemap){
        articleService.updateArticle(sitemap);
        return new ResponseEntity<>("Article with url: " + sitemap.toString() + " was updated.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteArticle")
    public ResponseEntity<String> deleteArticle(@RequestBody String loc){
        articleService.deleteArticle(loc);
        return new ResponseEntity<>("Article with url: " + loc + " was deleted.", HttpStatus.OK);
    }

    @GetMapping("/getSitemapNews")
    public List<Sitemap> getSitemapNews(){
        return articleService.getSitemapNews();
    }

    @PostMapping("/triggerSitemapNewsMapping")
    public ResponseEntity<String> triggerSitemapNewsMapping(){
        Thread thread = new Thread(){
            public synchronized void run(){
                articleService.startSitemapNewsMapping();
            }
        };
        thread.start();
        return new ResponseEntity<>("Sitemap news mapping has been started.", HttpStatus.OK);
    }
}
