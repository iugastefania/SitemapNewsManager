package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @GetMapping("/getArticle")
    public ResponseEntity<Url> getArticle(@RequestParam String loc){
        Url url = articleService.getArticle(loc);
        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @PostMapping("/addArticle")
    public ResponseEntity<String> addArticle(@RequestBody Url article) {
        articleService.addArticle(article);
        return new ResponseEntity<>(HttpStatus.OK);
//        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was added.", HttpStatus.OK);
    }

    @PutMapping("/updateArticle")
    public ResponseEntity<String> updateArticle(@RequestBody Url article){
        articleService.updateArticle(article);
        return new ResponseEntity<>(HttpStatus.OK);

//        return new ResponseEntity<>("Article with URL: " + article.getLoc() + " was updated.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteArticle")
    public ResponseEntity<String> deleteArticle(@RequestBody String loc){
        articleService.deleteArticle(loc);
        return new ResponseEntity<>("Article with URL: " + loc + " was deleted.", HttpStatus.OK);
    }
    @GetMapping(value = "/getAllArticlesByChannel/{channelName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Url>> getAllArticlesByChannel(@PathVariable String channelName) {
        List<Url> articles = articleService.getAllArticlesByChannel(channelName);
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @PostMapping("/addArticleToChannel")
    public ResponseEntity<String> addArticleToChannel(@RequestParam String channelName, @RequestBody Url article) {
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
    @GetMapping("/channelNames")
    public List<String> getAllChannelNames() {
        return articleService.getAllChannelNames();
    }

    @GetMapping("/getSitemapNews")
    public List<Sitemap> getSitemapNews(){
        return articleService.getSitemapNews();
    }

    @GetMapping("/getUrlNews")
    public List<Url> getUrlNews(){
        return articleService.getUrlNews();
    }

    @GetMapping("/getAllUrls")
    public ResponseEntity<List<Url>> getAllUrls() {
        List<Url> urls = articleService.getAllUrls();
        return new ResponseEntity<>(urls, HttpStatus.OK);
    }
    @GetMapping("/countUrlsByChannel")
    public ResponseEntity<Long> countUrlsByChannel(@RequestParam String channelName) {
        Long count = articleService.countUrlsByChannel(channelName);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

//    @GetMapping("/latestArticleByChannel")
//    public ResponseEntity<String> latestArticleByChannel(@RequestParam String channelName) {
//        String latestLastmod = articleService.getLatestLastmodByChannel(channelName);
//        return new ResponseEntity<>(latestLastmod, HttpStatus.OK);
//    }

    @GetMapping("/latestArticleByChannel")
    public ResponseEntity<Map<String, String>> latestArticleByChannel(@RequestParam String channelName) {
        String latestLastmod = articleService.getLatestLastmodByChannel(channelName);
        Map<String, String> response = new HashMap<>();
        response.put("lastUpdatedDate", latestLastmod);
        return ResponseEntity.ok(response);
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
