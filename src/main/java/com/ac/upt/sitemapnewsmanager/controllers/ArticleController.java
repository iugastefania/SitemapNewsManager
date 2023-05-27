package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.Url;
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
    public ResponseEntity<Url> getArticle(@RequestParam String loc){
        Url url = articleService.getArticle(loc);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @PostMapping("/addArticle")
    public ResponseEntity<String> addArticle(@RequestBody Url article){
        articleService.addArticle(article);
        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was added.", HttpStatus.OK);
    }

    @PutMapping("/updateArticle")
    public ResponseEntity<String> updateArticle(@RequestBody Url article){
        articleService.updateArticle(article);
        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was updated.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteArticle")
    public ResponseEntity<String> deleteArticle(@RequestBody String loc){
        articleService.deleteArticle(loc);
        return new ResponseEntity<>("Article with URL: " + loc + " was deleted.", HttpStatus.OK);
    }

    @GetMapping("/getAllArticlesByChannel")
    public ResponseEntity<List<Url>> getAllArticlesByChannel(@RequestParam String channelName){
        List<Url> articles = articleService.getAllArticlesByChannel(channelName);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @PostMapping("/addArticleToChannel")
    public ResponseEntity<String> addArticleToChannel(@RequestParam String channelName, @RequestBody Url article){
        articleService.addArticleToChannel(channelName, article);
        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was added to channel: " + channelName, HttpStatus.OK);
    }

    @PutMapping("/updateArticleInChannel")
    public ResponseEntity<String> updateArticleInChannel(@RequestParam String channelName, @RequestBody Url article){
        articleService.updateArticleInChannel(channelName, article);
        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was updated in channel: " + channelName, HttpStatus.OK);
    }

    @DeleteMapping("/deleteArticleFromChannel")
    public ResponseEntity<String> deleteArticleFromChannel(@RequestParam String channelName, @RequestParam String loc){
        articleService.deleteArticleFromChannel(channelName, loc);
        return new ResponseEntity<>("Article with URL: " + loc + " was deleted from channel: " + channelName, HttpStatus.OK);
    }

    @GetMapping("/getSitemapNews")
    public List<Sitemap> getSitemapNews(){
        return articleService.getSitemapNews();
    }

    @GetMapping("/getUrlNews")
    public List<Url> getUrlNews(){
        return articleService.getUrlNews();
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
