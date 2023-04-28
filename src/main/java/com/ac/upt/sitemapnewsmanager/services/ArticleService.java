package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.repositories.UrlRepository;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleService {

    UrlRepository urlRepository;

    SitemapNewsClient sitemapNewsClient;

    public Boolean getMappingRunning() {
        return isMappingRunning;
    }

    public void setMappingRunning(Boolean mappingRunning) {
        isMappingRunning = mappingRunning;
    }

    private Boolean isMappingRunning = Boolean.FALSE;

    @Autowired
    public ArticleService(UrlRepository urlRepository, SitemapNewsClient sitemapNewsClient){
        this.urlRepository = urlRepository;
        this.sitemapNewsClient = sitemapNewsClient;
    }
    public Url getArticle(String loc) {
        Optional<Url> byId = urlRepository.findById(loc);
        if (byId.isPresent()){
            return byId.get();
        }

        else throw new ArticleNotFoundException("Article with url: " + loc + " was not found.");
    }

    public void addArticle(Url article) {
        urlRepository.save(article);
    }

    public void updateArticle(Url article) {
        if(urlRepository.existsById(article.getLoc())){
            urlRepository.save(article);
        }
        else throw new ArticleNotFoundException("Article with url: " + article.getLoc() + " was not found.");
    }

    public void deleteArticle(String loc) {
        Optional<Url> byId = urlRepository.findById(loc);
        if (byId.isPresent()) {
            urlRepository.deleteById(loc);
        }
        else throw new ArticleNotFoundException("Article with url: " + loc + " was not found.");
    }

    public List<Url> getSitemapNews() {
        String stringResponse = sitemapNewsClient.getStringResponse();
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        try {
            List<Url> articles = xmlMapper.readValue(stringResponse, new TypeReference<>() {
            });
            articles = articles.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            return articles;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void startSitemapNewsMapping() {
        if (!isMappingRunning) {
            isMappingRunning = Boolean.TRUE;
            log.info("Sitemap news article mapping has started.");
            String stringResponse = sitemapNewsClient.getStringResponse();
            XMLInputFactory input = new WstxInputFactory();
            input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
            try {
                List<Url> articles = xmlMapper.readValue(stringResponse, new TypeReference<List<Url>>() {});
                articles = articles.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
                urlRepository.saveAll(articles);
                log.info("Sitemap news article mapping has ended.");
                isMappingRunning = Boolean.FALSE;
            } catch (Throwable e) {
                log.error("Sitemap news article mapping has failed.");
                isMappingRunning = Boolean.FALSE;
                throw new RuntimeException(e);
            }
        }
        else log.info("Sitemap news mapping already running.");
    }
}
