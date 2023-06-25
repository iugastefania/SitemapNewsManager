package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Article;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.ac.upt.sitemapnewsmanager.payloads.requests.ArticleRequest;
import com.ac.upt.sitemapnewsmanager.payloads.requests.SitemapRequest;
import com.ac.upt.sitemapnewsmanager.repositories.ArticleRepository;
import com.ac.upt.sitemapnewsmanager.repositories.SitemapRepository;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.stream.XMLInputFactory;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableScheduling
public class ArticleService {
  SitemapRepository sitemapRepository;

  ArticleRepository articleRepository;

  SitemapNewsClient sitemapNewsClient;

  @Autowired UserRepository userRepository;

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
  public ArticleService(
      SitemapRepository sitemapRepository,
      ArticleRepository articleRepository,
      SitemapNewsClient sitemapNewsClient,
      UserRepository userRepository) {
    this.sitemapRepository = sitemapRepository;
    this.articleRepository = articleRepository;
    this.sitemapNewsClient = sitemapNewsClient;
    this.userRepository = userRepository;
  }

  public void updateArticle(Article article) {
    Optional<Article> byLoc = articleRepository.findByLoc(article.getLoc());
    if (byLoc.isPresent()) {
      Article existingArticle = byLoc.get();
      existingArticle.setChannelName(article.getChannelName());
      Optional<Sitemap> sitemap = sitemapRepository.findByChannel(article.getChannelName());
      sitemap.ifPresent(value -> existingArticle.setSitemapId(value.getId()));
      existingArticle.setDescription(article.getDescription());
      existingArticle.setThumbnail(article.getThumbnail());
      existingArticle.setLastmod(article.getLastmod());
      existingArticle.setTitle(article.getTitle());
      existingArticle.setUserId(article.getUserId());
      articleRepository.save(existingArticle);
    } else {
      throw new ArticleNotFoundException(
          "Article with URL: " + article.getLoc() + " was not found.");
    }
  }

  public Article getArticle(String loc) {
    Optional<Article> byLoc = articleRepository.findByLoc(loc);
    if (byLoc.isPresent()) {
      return byLoc.get();
    } else {
      throw new ArticleNotFoundException("Article with URL: " + loc + " was not found.");
    }
  }

  public void deleteArticle(String loc) {
    Optional<Article> byLoc = articleRepository.findByLoc(loc);
    if (byLoc.isPresent()) {
      articleRepository.deleteById(byLoc.get().getId());
    } else {
      throw new ArticleNotFoundException("Article with URL: " + loc + " was not found.");
    }
  }

  public List<Article> getAllArticlesByChannel(String channelName) {
    return articleRepository.findAllByChannelName(channelName);
  }

  public Article addArticle(ArticleRequest articleRequest) throws Exception {
    Optional<User> user = userRepository.findByUsername(articleRequest.getUser());
    if (user.isPresent()) {
      Optional<Article> existingArticle = articleRepository.findByLoc(articleRequest.getLoc());
      if (existingArticle.isPresent()) {
        throw new IllegalArgumentException(
            "Article with URL: " + articleRequest.getLoc() + " already exists.");
      } else {
        String channelName = articleRequest.getChannelName();
        Optional<Sitemap> sitemap = sitemapRepository.findByChannel(channelName);
        if (sitemap.isPresent()) {
          Article entity =
              new Article(
                  sitemap.get().getId(),
                  articleRequest.getLoc(),
                  articleRequest.getLastmod(),
                  channelName,
                  articleRequest.getTitle(),
                  articleRequest.getDescription(),
                  articleRequest.getThumbnail(),
                  user.get().getId());
          articleRepository.save(entity);
          return entity;
        } else {
          throw new IllegalArgumentException("No sitemap found for channel: " + channelName);
        }
      }
    } else {
      throw new Exception("Invalid user");
    }
  }

  public Article addArticleToChannel(String channelName, ArticleRequest articleRequest)
      throws Exception {
    Optional<User> user = userRepository.findByUsername(articleRequest.getUser());
    if (user.isPresent()) {
      Optional<Article> existingArticle = articleRepository.findByLoc(articleRequest.getLoc());
      if (existingArticle.isPresent()) {
        throw new IllegalArgumentException(
            "Article with URL: " + articleRequest.getLoc() + " already exists.");
      } else {
        Article entity =
            new Article(
                articleRequest.getSitemapId(),
                articleRequest.getLoc(),
                articleRequest.getLastmod(),
                channelName,
                articleRequest.getTitle(),
                articleRequest.getDescription(),
                articleRequest.getThumbnail(),
                user.get().getId());
        articleRepository.save(entity);
        return entity;
      }
    } else throw new Exception("Invalid user");
  }

  public void updateArticleInChannel(String channelName, Article article) {
    Optional<Article> existingArticle =
        articleRepository.findByChannelNameAndLoc(channelName, article.getLoc());
    if (existingArticle.isPresent()) {
      Article updatedArticle = existingArticle.get();
      updatedArticle.setThumbnail(article.getThumbnail());
      updatedArticle.setChannelName(article.getChannelName());
      updatedArticle.setDescription(article.getDescription());
      updatedArticle.setLastmod(article.getLastmod());
      updatedArticle.setTitle(article.getTitle());
      articleRepository.save(updatedArticle);
    } else {
      throw new ArticleNotFoundException(
          "Article with URL: "
              + article.getLoc()
              + " and channel name: "
              + channelName
              + " was not found.");
    }
  }

  public void deleteArticleFromChannel(String channelName, String loc) {
    Optional<Article> existingArticle = articleRepository.findByChannelNameAndLoc(channelName, loc);
    if (existingArticle.isPresent()) {
      articleRepository.delete(existingArticle.get());
    } else {
      throw new ArticleNotFoundException(
          "Article with URL: " + loc + " and channel name: " + channelName + " was not found.");
    }
  }

//  public List<String> getAllChannelNames() {
//    List<Article> articles = articleRepository.findAll();
//    return articles.stream().map(Article::getChannelName).distinct().collect(Collectors.toList());
//  }
public List<String> getAllChannelNames() {
  List<Sitemap> sitemaps = sitemapRepository.findAll();
  return sitemaps.stream()
          .map(Sitemap::getChannel)
          .distinct()
          .collect(Collectors.toList());
}

  public List<Article> getAllArticles() {
    return articleRepository.findAll();
  }

  public Long countUrlsByChannel(String channelName) {
    return articleRepository.countAllByChannelName(channelName);
  }

  public String getLatestLastmodByChannel(String channelName) {
    return articleRepository.findLatestLastmodByChannelName(channelName);
  }

  public List<Sitemap> getAllSitemaps() {
    return sitemapRepository.findAll();
  }

  public List<Article> getUrlNews(String sitemapLoc) {
    Long sitemapId = sitemapRepository.findByLoc(sitemapLoc).get().getId();
    List<Article> articles = articleRepository.findAllBySitemapId(sitemapId);
    if (!articles.isEmpty()) {
      return articles;
    } else {
      XMLInputFactory input = new WstxInputFactory();
      input.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
      XmlMapper xmlMapper = new XmlMapper(new XmlFactory(input, new WstxOutputFactory()));
      if (sitemapsDisallowed.contains(sitemapLoc)) {
        return Collections.emptyList();
      }
      String urlStringResponse = getStringResponseFromUrl(sitemapLoc);
      try {
        articles = xmlMapper.readValue(urlStringResponse, new TypeReference<List<Article>>() {});
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
      articles =
          articles.stream()
              .filter(article -> article.getLoc() != null)
              .collect(Collectors.toList());
      return articles;
    }
  }

  @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Athens")
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
        sitemaps.removeIf(sitemap -> sitemapsDisallowed.contains(sitemap.getLoc()));

        sitemaps =
            sitemaps.stream()
                .filter(sitemap -> sitemap.getLoc() != null)
                .peek(
                    sitemap -> {
                      String sitemapUrl = sitemap.getLoc();
                      String channel =
                          sitemapUrl.substring(
                              sitemapUrl.indexOf("https://www.telegraph.co.uk/")
                                  + "https://www.telegraph.co.uk/".length(),
                              sitemapUrl.lastIndexOf("/sitemap"));
                      sitemap.setChannel(channel);
                    })
                .collect(Collectors.toList());
        sitemapRepository.saveAll(sitemaps);
        log.info("Sitemap mapping has ended.");
        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<CompletableFuture<Void>> futures =
            sitemaps.stream()
                .filter(sitemap -> !sitemapsDisallowed.contains(sitemap.getLoc()))
                .map(sitemap -> processSitemapAsync(sitemap, xmlMapper, executorService))
                .collect(Collectors.toList());

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

  private CompletableFuture<Void> processSitemapAsync(
      Sitemap sitemap, XmlMapper xmlMapper, ExecutorService executorService) {
    return CompletableFuture.supplyAsync(
            () -> {
              String sitemapUrl = sitemap.getLoc();
              String channelName =
                  sitemapUrl.substring(
                      sitemapUrl.indexOf("https://www.telegraph.co.uk/")
                          + "https://www.telegraph.co.uk/".length(),
                      sitemapUrl.lastIndexOf("/sitemap"));
              log.info("Article mapping for channel: " + channelName + " has started.");
              String urlStringResponse = getStringResponseFromUrl(sitemapUrl);
              List<Article> articleList;
              try {
                articleList =
                    xmlMapper.readValue(urlStringResponse, new TypeReference<List<Article>>() {});
              } catch (JsonProcessingException e) {
                log.error("Failed to parse the URL response for channel: " + channelName, e);
                return null;
              }
              articleList =
                  articleList.stream()
                      .filter(article -> article.getLoc() != null)
                      .collect(Collectors.toList());
              articleList.forEach(article -> article.setChannelName(channelName));
              articleList.forEach(article -> article.setSitemapId(sitemap.getId()));
//              articleList.forEach(article -> article.setUserId(1L));

              List<CompletableFuture<List<Article>>> futures =
                  articleList.stream()
                      .map(article -> extractDataFromUrlAsync(article, executorService))
                      .collect(Collectors.toList());

              CompletableFuture<List<Article>> combinedFuture =
                  CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                      .thenApply(
                          v ->
                              futures.stream()
                                  .flatMap(future -> future.join().stream())
                                  .collect(Collectors.toList()));

              return combinedFuture
                  .thenAccept(updatedUrls -> articleRepository.saveAll(updatedUrls))
                  .thenRun(
                      () -> log.info("Article mapping for channel:" + channelName + " has ended."))
                  .exceptionally(
                      e -> {
                        log.error("Article mapping for channel:" + channelName + " failed.", e);
                        return null;
                      });
            },
            executorService)
        .thenCompose(Function.identity());
  }

  private CompletableFuture<List<Article>> extractDataFromUrlAsync(
      Article article, ExecutorService executorService) {
    return CompletableFuture.supplyAsync(
        () -> {
          String urlLoc = article.getLoc();
          try {
            Thread.sleep(1000);

            Document document = Jsoup.parse(new URL(urlLoc), 10000);

            String title = document.select("meta[property=og:title]").attr("content");
            String description = document.select("meta[name=description]").attr("content");
            if (description.isEmpty()) {
              String[] pathSegments = urlLoc.split("/");
              String desiredString = pathSegments[pathSegments.length - 1].replace("-", " ");
              description =
                  desiredString.substring(0, 1).toUpperCase() + desiredString.substring(1);
            }

            String thumbnail = document.select("meta[property=og:image]").attr("content");

            article.setTitle("title");
            article.setDescription("description");
            article.setThumbnail("thumbnail");

            return Collections.singletonList(article);
          } catch (IOException | InterruptedException e) {
            log.error("Failed to extract data from URL: " + urlLoc, e);
            return Collections.emptyList();
          }
        },
        executorService);
  }

  public String getStringResponseFromUrl(String url) {
    try {
      Thread.sleep(100000);

      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      try (InputStream inputStream = connection.getInputStream();
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
        return reader.lines().collect(Collectors.joining("\n"));
      }
    } catch (IOException | InterruptedException e) {
      log.error("Tried to access the article endpoint without success.");
      throw new RuntimeException(e);
    }
  }

  public Sitemap addSitemap(SitemapRequest sitemapRequest) {
    Optional<Sitemap> existingArticle = sitemapRepository.findByLoc(sitemapRequest.getLoc());
    if (existingArticle.isPresent()) {
      throw new IllegalArgumentException(
          "Sitemap with URL: " + sitemapRequest.getLoc() + " already exists.");
    } else {
      Sitemap entity = new Sitemap(sitemapRequest.getLoc(), sitemapRequest.getChannel());
      sitemapRepository.save(entity);
      return entity;
    }
  }

  public void deleteSitemap(String loc) {
    Optional<Sitemap> byLoc = sitemapRepository.findByLoc(loc);
    if (byLoc.isPresent()) {
      sitemapRepository.deleteById(byLoc.get().getId());
    } else {
      throw new ArticleNotFoundException("Sitemap with URL: " + loc + " was not found.");
    }
  }

  //  public void updateSitemap(Sitemap sitemap) {
  //    Optional<Sitemap> byLoc = sitemapRepository.findByLoc(sitemap.getLoc());
  //    if (byLoc.isPresent()) {
  //      Sitemap existingSitemap = byLoc.get();
  //      existingSitemap.setChannel(sitemap.getChannel());
  //      sitemapRepository.save(existingSitemap);
  //    } else {
  //      throw new ArticleNotFoundException(
  //              "Sitemap with URL: " + sitemap.getLoc() + " was not found.");
  //    }
  //  }

}
