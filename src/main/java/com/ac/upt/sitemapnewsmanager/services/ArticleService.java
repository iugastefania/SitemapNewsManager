package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.repositories.SitemapRepository;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleService {

    SitemapRepository sitemapRepository;

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
    public ArticleService(SitemapRepository sitemapRepository, UrlRepository urlRepository, SitemapNewsClient sitemapNewsClient){
        this.sitemapRepository = sitemapRepository;
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

    public List<Sitemap> getSitemapNews() {
        String stringResponse = sitemapNewsClient.getStringResponse();
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        try {
            List<Sitemap> sitemaps = xmlMapper.readValue(stringResponse, new TypeReference<List<Sitemap>>() {});
            sitemaps = sitemaps.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            return sitemaps;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 300000)
    public void startSitemapNewsMapping() {
        if (!isMappingRunning) {
            isMappingRunning = Boolean.TRUE;
            log.info("Sitemap news mapping has started.");
            String stringResponse = sitemapNewsClient.getStringResponse();
            XMLInputFactory input = new WstxInputFactory();
            input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
            try {
                List<Sitemap> sitemaps = xmlMapper.readValue(stringResponse, new TypeReference<List<Sitemap>>() {});
                sitemaps = sitemaps.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
                sitemapRepository.saveAll(sitemaps);
                log.info("Sitemap news mapping has ended.");
                isMappingRunning = Boolean.FALSE;
            } catch (Throwable e) {
                log.error("Sitemap news mapping has failed.");
                isMappingRunning = Boolean.FALSE;
                throw new RuntimeException(e);
            }
        }
        else log.info("Sitemap news mapping already running.");
    }

    public List<Url> getUrlNews() {
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        List<Sitemap> sitemaps = getSitemapNews();
        List<Url> urlList = null;
        for (Sitemap sitemap : sitemaps) {
            String sitemapUrl = sitemap.getLoc();
            String channelName = sitemapUrl.substring(sitemapUrl.indexOf("https://www.telegraph.co.uk/") + "https://www.telegraph.co.uk/".length(), sitemapUrl.lastIndexOf("/sitemap.xml"));
            String urlStringResponse = getStringResponseFromUrl(sitemapUrl);
            try {
                urlList = xmlMapper.readValue(urlStringResponse, new TypeReference<List<Url>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            urlList = urlList.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            urlList.forEach(url -> url.setChannelName(channelName));
            urlRepository.saveAll(urlList);
            return urlList;
            }
        return urlList;
    }

    public void addUrlNews() {
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        List<Sitemap> sitemaps = getSitemapNews();
        List<Url> urlList = null;
        for (Sitemap sitemap : sitemaps) {
            String sitemapUrl = sitemap.getLoc();
            String channelName = sitemapUrl.substring(sitemapUrl.indexOf("https://www.telegraph.co.uk/") + "https://www.telegraph.co.uk/".length(), sitemapUrl.lastIndexOf("/sitemap.xml"));
            String urlStringResponse = getStringResponseFromUrl(sitemapUrl);
            try {
                urlList = xmlMapper.readValue(urlStringResponse, new TypeReference<List<Url>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            urlList = urlList.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            urlList.forEach(url -> url.setChannelName(channelName));
            urlRepository.saveAll(urlList);
            log.info("Saved articles.");
            break;
        }
    }

    public String getStringResponseFromUrl(String url) {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = connection.getInputStream();
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("Tried to access endpoint with no success.");
            throw new RuntimeException(e);
        }
    }
}
