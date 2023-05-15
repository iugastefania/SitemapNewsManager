package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.repositories.SitemapRepository;
import com.ac.upt.sitemapnewsmanager.repositories.UrlRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {
    SitemapRepository sitemapRepository = mock(SitemapRepository.class);

    UrlRepository urlRepository = mock(UrlRepository.class);

    SitemapNewsClient sitemapNewsClient = mock(SitemapNewsClient.class);

    ArticleService articleService = new ArticleService(sitemapRepository, urlRepository, sitemapNewsClient);

    @Test
    public void testGetArticleHappyPath(){
        Optional<Sitemap> expectedSitemap = Optional.of(new Sitemap("string"));
        when(sitemapRepository.findById(anyString())).thenReturn(expectedSitemap);
        Sitemap resultSitemap = articleService.getArticle("string");
        assertEquals(expectedSitemap.get(), resultSitemap);
    }

    @Test
    public void testGetArticle_ArticleNotFound(){
        Optional<Sitemap> expectedSitemap = Optional.empty();
        when(sitemapRepository.findById(anyString())).thenReturn(expectedSitemap);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.getArticle("string");
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void addArticleTest(){
        Sitemap expectedSitemap = new Sitemap("string");
        articleService.addArticle(expectedSitemap);
        verify(sitemapRepository, times(1)).save(expectedSitemap);
    }

    @Test
    public void testUpdateArticleHappyPath(){
        Sitemap expectedSitemap = new Sitemap("string");
        when(sitemapRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
        articleService.updateArticle(expectedSitemap);
        verify(sitemapRepository, times(1)).save(expectedSitemap);
    }

    @Test
    public void testUpdateArticle_ArticleNotFound(){
        when(sitemapRepository.existsById(anyString())).thenReturn(Boolean.FALSE);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.updateArticle(new Sitemap("string"));
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void testDeleteArticleHappyPath(){
        Optional<Sitemap> expectedSitemap = Optional.of(new Sitemap("string"));
        when(sitemapRepository.findById(anyString())).thenReturn(expectedSitemap);
        articleService.deleteArticle("string");
        verify(sitemapRepository, times(1)).deleteById("string");
    }

    @Test
    public void testDeleteArticle_ArticleNotFound(){
        Optional<Sitemap> expectedSitemap = Optional.empty();
        when(sitemapRepository.findById(anyString())).thenReturn(expectedSitemap);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.deleteArticle("string");
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void testGetSitemapNewsHappyPath(){
        String stringResponse = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "<sitemap>\n" +
                "<loc>string</loc>\n" +
                "</sitemap>\n" +
                "</sitemapindex>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Sitemap> expectedResponse= Arrays.asList(new Sitemap("string"));
        List<Sitemap> actualResponse = articleService.getSitemapNews();
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void testGetSitemapNews_JsonProcessingException(){
        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
        Exception exception = assertThrows(RuntimeException.class, () ->{
            articleService.getSitemapNews();
        });

        String expectedMessage = "JsonParseException";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testStartSitemapNewsMappingHappyPath(){
        articleService.setMappingRunning(Boolean.FALSE);
        String stringResponse = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "<sitemap>\n" +
                "<loc>string</loc>\n" +
                "</sitemap>\n" +
                "</sitemapindex>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Sitemap> expectedSitemaps= Arrays.asList(new Sitemap("string"));
        articleService.startSitemapNewsMapping();
        verify(sitemapRepository, times(1)).saveAll(expectedSitemaps);
    }

    @Test
    public void testStartSitemapNewsMapping_Exception(){
        articleService.setMappingRunning(Boolean.FALSE);
        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
        Exception exception = assertThrows(RuntimeException.class, () ->{
            articleService.startSitemapNewsMapping();
        });

        String expectedMessage = "Exception";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testStartSitemapNewsMapping_IsRunning(){
        articleService.setMappingRunning(Boolean.TRUE);
        articleService.startSitemapNewsMapping();
        verify(sitemapNewsClient, times(0)).getStringResponse();
        verify(sitemapRepository, times(0)).saveAll(any());
    }
}
