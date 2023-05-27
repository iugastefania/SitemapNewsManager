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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
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

    @Value("${sitemaps.disallowed}")
    private List<String> sitemapsDisallowed;

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

    public List<Url> getUrlNews() {
        XMLInputFactory input = new WstxInputFactory();
        input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
        List<Sitemap> sitemaps = getSitemapNews();
        List<Url> processedUrls = new ArrayList<>(); // Create a separate list to accumulate the processed items
        for (Sitemap sitemap : sitemaps) {
            String sitemapUrl = sitemap.getLoc();
            if (sitemapsDisallowed.contains(sitemapUrl)) {
                continue; // Skip processing and go to the next iteration
            }
            String channelName = sitemapUrl.substring(sitemapUrl.indexOf("https://www.telegraph.co.uk/") + "https://www.telegraph.co.uk/".length(), sitemapUrl.lastIndexOf("/sitemap"));
            String urlStringResponse = getStringResponseFromUrl(sitemapUrl);
            List<Url> urlList;
            try {
                urlList = xmlMapper.readValue(urlStringResponse, new TypeReference<List<Url>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            urlList = urlList.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            urlList.forEach(url -> url.setChannelName(channelName));
            processedUrls.addAll(urlList); // Add the processed items to the accumulated list
        }
        return processedUrls; // Return the accumulated list of processed items
    }

    @Scheduled(fixedDelay = 300000)
    public void startSitemapNewsMapping() {
        if (!isMappingRunning) {
            isMappingRunning = Boolean.TRUE;
            log.info("Sitemap mapping has started.");
            String stringResponse = sitemapNewsClient.getStringResponse();
            XMLInputFactory input = new WstxInputFactory();
            input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
            XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
            try {
                List<Sitemap> sitemaps;
                try {
                    sitemaps = xmlMapper.readValue(stringResponse, new TypeReference<List<Sitemap>>() {});
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse the sitemap response.", e);
                    return;
                }
                sitemaps = sitemaps.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
                sitemapRepository.saveAll(sitemaps);
                log.info("Sitemap mapping has ended.");
                ExecutorService executorService = Executors.newFixedThreadPool(10); // Adjust the thread pool size as needed

                List<CompletableFuture<Void>> futures = sitemaps.stream()
                        .filter(sitemap -> !sitemapsDisallowed.contains(sitemap.getLoc()))
                        .map(sitemap -> processSitemapAsync(sitemap, xmlMapper, executorService))
                        .collect(Collectors.toList());

                // Wait for all futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                executorService.shutdown();

                log.info("Article mapping has ended.");
                isMappingRunning = Boolean.FALSE;
            } catch (Throwable e) {
                log.error("Mapping has failed.");
                isMappingRunning = Boolean.FALSE;
                throw new RuntimeException(e);
            }
        } else {
            log.info("Mapping already running.");
        }
    }

    private CompletableFuture<Void> processSitemapAsync(Sitemap sitemap, XmlMapper xmlMapper, ExecutorService executorService) {
        return CompletableFuture.supplyAsync(() -> {
            String sitemapUrl = sitemap.getLoc();
            String channelName = sitemapUrl.substring(sitemapUrl.indexOf("https://www.telegraph.co.uk/") + "https://www.telegraph.co.uk/".length(), sitemapUrl.lastIndexOf("/sitemap"));
            log.info("Article mapping for channel: " + channelName + " has started.");
            String urlStringResponse = getStringResponseFromUrl(sitemapUrl);
            List<Url> urlList;
            try {
                urlList = xmlMapper.readValue(urlStringResponse, new TypeReference<List<Url>>() {});
            } catch (JsonProcessingException e) {
                log.error("Failed to parse the URL response for channel: " + channelName, e);
                return null;
            }
            urlList = urlList.stream().filter(url -> url.getLoc() != null).collect(Collectors.toList());
            urlList.forEach(url -> url.setChannelName(channelName));

            List<CompletableFuture<List<Url>>> futures = urlList.stream()
                    .map(url -> extractDataFromUrlAsync(url, executorService))
                    .collect(Collectors.toList());

            CompletableFuture<List<Url>> combinedFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream().flatMap(future -> future.join().stream()).collect(Collectors.toList()));

            return combinedFuture.thenAccept(updatedUrls -> urlRepository.saveAll(updatedUrls))
                    .thenRun(() -> log.info("Article mapping for channel:" + channelName + " has ended."))
                    .exceptionally(e -> {
                        log.error("Article mapping for channel:" + channelName + " failed.", e);
                        return null;
                    });
        }, executorService).thenCompose(Function.identity());
    }

    private CompletableFuture<List<Url>> extractDataFromUrlAsync(Url url, ExecutorService executorService) {
        return CompletableFuture.supplyAsync(() -> {
            String urlLoc = url.getLoc();
            try {
                // Introduce a delay of 1 second before making the request
                Thread.sleep(1000);

                // Retrieve the web page source using Jsoup's parse method
                Document document = Jsoup.parse(new URL(urlLoc), 10000);

                String description = document.select("meta[name=description]").attr("content");
                if (description.isEmpty()) {
                    // Set value for description if it is empty
                    String[] pathSegments = urlLoc.split("/");
                    String desiredString = pathSegments[pathSegments.length - 1].replace("-", " ");
                    description = desiredString.substring(0, 1).toUpperCase() + desiredString.substring(1);
                }

                String thumbnail = document.select("meta[property=og:image]").attr("content");

                // Set the extracted values in the Url object
                url.setDescription(description);
                url.setThumbnail(thumbnail);

                // Return the updated Url object
                return Collections.singletonList(url);
            } catch (IOException | InterruptedException e) {
                log.error("Failed to extract data from URL: " + urlLoc, e);
                return Collections.emptyList();
            }
        }, executorService);
    }

    public String getStringResponseFromUrl(String url) {
        try {
            // Introduce a delay of 1 second before making the request
            Thread.sleep(1000);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException | InterruptedException e) {
            log.error("Tried to access the article endpoint without success.");
            throw new RuntimeException(e);
        }
    }



//    private void extractDataFromUrl(Url url) {
//        String urlLoc = url.getLoc();
//        Document document;
//        try {
//            document = Jsoup.connect(urlLoc).timeout(5000).get();
//        } catch (IOException e) {
//            log.error("Failed to extract data from URL: " + urlLoc);
//            return;
//        }
//
//        String description = document.select("meta[name=description]").attr("content");
//        if (description.isEmpty()) {
//            // Set value for description if it is empty
//            String[] pathSegments = urlLoc.split("/");
//            String desiredString = pathSegments[pathSegments.length - 1].replace("-", " ");
//            description = desiredString.substring(0, 1).toUpperCase() + desiredString.substring(1);
//            // description = null;
//        }
//
//        String thumbnail = document.select("meta[property=og:image]").attr("content");
//
//        // Set the extracted values in the Url object
//        url.setDescription(description);
//        url.setThumbnail(thumbnail);
//
//        // Save the updated Url object
//        urlRepository.save(url);
//    }
}
