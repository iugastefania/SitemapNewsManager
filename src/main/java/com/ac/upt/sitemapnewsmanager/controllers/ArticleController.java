package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Article;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.payloads.requests.ArticleRequest;
import com.ac.upt.sitemapnewsmanager.payloads.requests.SitemapRequest;
import com.ac.upt.sitemapnewsmanager.services.ArticleService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/app")
@CrossOrigin(origins = "http://localhost:4200")
public class ArticleController {

  @Autowired ArticleService articleService;

  @GetMapping("/getArticle")
  public ResponseEntity<Article> getArticle(@RequestParam String loc) {
    Article article = articleService.getArticle(loc);
    return new ResponseEntity<>(article, HttpStatus.OK);
  }

  @PostMapping("/addArticle")
  public ResponseEntity<Article> addArticle(@Valid @RequestBody ArticleRequest articleRequest) {
    try {
      Article article = articleService.addArticle(articleRequest);
      return new ResponseEntity<>(article, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/updateArticle")
  public ResponseEntity<String> updateArticle(@Valid @RequestBody Article article) {
    articleService.updateArticle(article);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping("/deleteArticle")
  public ResponseEntity<String> deleteArticle(@RequestParam String loc) {
    articleService.deleteArticle(loc);
    return new ResponseEntity<>("Article with URL: " + loc + " was deleted.", HttpStatus.OK);
  }

  @PostMapping("/addArticleToChannel")
  public ResponseEntity<Article> addArticleToChannel(
      @RequestParam String channelName, @Valid @RequestBody ArticleRequest articleRequest) {
    try {
      Article addedArticle = articleService.addArticleToChannel(channelName, articleRequest);
      return new ResponseEntity<>(addedArticle, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/updateArticleInChannel")
  public ResponseEntity<String> updateArticleInChannel(
      @RequestParam String channelName, @RequestBody Article article) {
    articleService.updateArticleInChannel(channelName, article);
    return new ResponseEntity<>(
        "Article with URL: " + article.getLoc() + " was updated in channel: " + channelName,
        HttpStatus.OK);
  }

  @DeleteMapping("/deleteArticleFromChannel")
  public ResponseEntity<String> deleteArticleFromChannel(
      @RequestParam String channelName, @RequestParam String loc) {
    articleService.deleteArticleFromChannel(channelName, loc);
    return new ResponseEntity<>(
        "Article with URL: " + loc + " was deleted from channel: " + channelName, HttpStatus.OK);
  }

  @GetMapping("/getAllSitemaps")
  public ResponseEntity<List<Sitemap>> getAllSitemaps() {
    List<Sitemap> sitemapNews = articleService.getAllSitemaps();
    return ResponseEntity.ok(sitemapNews);
  }

  @PostMapping("/addSitemap")
  public ResponseEntity<Sitemap> addSitemap(@Valid @RequestBody SitemapRequest sitemapRequest) {
    Sitemap sitemap = articleService.addSitemap(sitemapRequest);
    return new ResponseEntity<>(sitemap, HttpStatus.CREATED);
  }

  @DeleteMapping("/deleteSitemap")
  public ResponseEntity<String> deleteSitemap(@RequestParam String loc) {
    articleService.deleteSitemap(loc);
    return new ResponseEntity<>("Sitemap with URL: " + loc + " was deleted.", HttpStatus.OK);
  }

  //  @PutMapping("/updateSitemap")
  //  public ResponseEntity<String> updateSitemap(@Valid @RequestBody Sitemap sitemap) {
  //    articleService.updateSitemap(sitemap);
  //    return new ResponseEntity<>(HttpStatus.OK);
  //  }

  @GetMapping(
      value = "/getAllArticlesByChannel/{channelName}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Article>> getAllArticlesByChannel(@PathVariable String channelName) {
    List<Article> articles = articleService.getAllArticlesByChannel(channelName);
    return new ResponseEntity<>(articles, HttpStatus.OK);
  }

  @GetMapping("/channelNames")
  public ResponseEntity<List<String>> getAllChannelNames() {
    List<String> channelNames = articleService.getAllChannelNames();
    return ResponseEntity.ok(channelNames);
  }

  @GetMapping("/getAllArticles")
  public ResponseEntity<List<Article>> getAllArticles() {
    List<Article> articles = articleService.getAllArticles();
    return new ResponseEntity<>(articles, HttpStatus.OK);
  }

  @GetMapping("/getUrlNews")
  public ResponseEntity<List<Article>> getUrlNews(@RequestParam String sitemapLoc) {
    List<Article> articleNews = articleService.getUrlNews(sitemapLoc);
    return ResponseEntity.ok(articleNews);
  }

  @GetMapping("/countUrlsByChannel")
  public ResponseEntity<Long> countUrlsByChannel(@RequestParam String channelName) {
    Long count = articleService.countUrlsByChannel(channelName);
    return new ResponseEntity<>(count, HttpStatus.OK);
  }

  @GetMapping("/latestArticleByChannel")
  public ResponseEntity<Map<String, String>> latestArticleByChannel(
      @RequestParam String channelName) {
    String latestLastmod = articleService.getLatestLastmodByChannel(channelName);
    Map<String, String> response = new HashMap<>();
    response.put("lastUpdatedDate", latestLastmod);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/triggerSitemapNewsMapping")
  public ResponseEntity<String> triggerSitemapNewsMapping() {
    Thread thread =
        new Thread() {
          public synchronized void run() {
            articleService.startSitemapNewsMapping();
          }
        };
    thread.start();
    return new ResponseEntity<>("Sitemap news mapping has been started.", HttpStatus.OK);
  }
}
