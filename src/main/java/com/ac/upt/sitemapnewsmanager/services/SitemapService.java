package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.SitemapNotFoundException;
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
public class SitemapService {

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
    public SitemapService(UrlRepository urlRepository, SitemapNewsClient sitemapNewsClient){
        this.urlRepository = urlRepository;
        this.sitemapNewsClient = sitemapNewsClient;
    }
    public Url getSitemap(String loc) {
        Optional<Url> byId = urlRepository.findById(loc);
        if (byId.isPresent()){
            return byId.get();
        }

        else throw new SitemapNotFoundException("Sitemap with url: " + loc + " was not found.");
    }

    public void addSitemap(Url sitemap) {
        urlRepository.save(sitemap);
    }

    public void updateSitemap(Url sitemap) {
        if(urlRepository.existsById(sitemap.getLoc())){
            urlRepository.save(sitemap);
        }
        else throw new SitemapNotFoundException("Sitemap with url: " + sitemap.getLoc() + " was not found.");
    }

    public void deleteSitemap(String loc) {
        Optional<Url> byId = urlRepository.findById(loc);
        if (byId.isPresent()) {
            urlRepository.deleteById(loc);
        }
        else throw new SitemapNotFoundException("Sitemap with url: " + loc + " was not found.");
    }

    public List<Url> getSitemapNews() {
        String stringResponse = sitemapNewsClient.getStringResponse();
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        try {
            List<Url> sitemaps = xmlMapper.readValue(stringResponse, new TypeReference<>() {
            });
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
                List<Url> sitemaps = xmlMapper.readValue(stringResponse, new TypeReference<List<Url>>() {});
                sitemaps = sitemaps.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
                urlRepository.saveAll(sitemaps);
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
}
