package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.services.SitemapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SitemapController {

    @Autowired
    SitemapService sitemapService;

    @GetMapping("/getSitemap")
    public ResponseEntity<Url> getSitemap(@RequestParam String loc){
        Url sitemap = sitemapService.getSitemap(loc);
        return new ResponseEntity<>(sitemap, HttpStatus.OK);
    }
    @GetMapping("/EndpointForTest")
    public ResponseEntity<Url> EndpointForTest(@RequestParam String loc){
        Url sitemap = sitemapService.getSitemap(loc);
        return new ResponseEntity<>(sitemap, HttpStatus.OK);
    }
    @PostMapping("/addSitemap")
    public ResponseEntity<String> addSitemap(@RequestBody Url sitemap){
        sitemapService.addSitemap(sitemap);
        return new ResponseEntity<>("Sitemap with url: " + sitemap.toString() + " was added.", HttpStatus.OK);
    }

    @PutMapping("/updateSitemap")
    public ResponseEntity<String> updateSitemap(@RequestBody Url sitemap){
        sitemapService.updateSitemap(sitemap);
        return new ResponseEntity<>("Sitemap with url: " + sitemap.toString() + " was updated.", HttpStatus.OK);
    }

    @DeleteMapping("/deleteSitemap")
    public ResponseEntity<String> deleteSitemap(@RequestBody String loc){
        sitemapService.deleteSitemap(loc);
        return new ResponseEntity<>("Sitemap with url: " + loc + " was deleted.", HttpStatus.OK);
    }

    @GetMapping("/getSitemapNews")
    public List<Url> getSitemapNews(){
        return sitemapService.getSitemapNews();
    }

    @PostMapping("/triggerSitemapNewsMapping")
    public ResponseEntity<String> triggerSitemapNewsMapping(){
        Thread thread = new Thread(){
            public synchronized void run(){
                sitemapService.startSitemapNewsMapping();
            }
        };
        thread.start();
        return new ResponseEntity<>("Sitemap news mapping has been started.", HttpStatus.OK);
    }
}
