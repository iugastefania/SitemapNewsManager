package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Url;
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
    UrlRepository urlRepository = mock(UrlRepository.class);

    SitemapNewsClient sitemapNewsClient = mock(SitemapNewsClient.class);

    ArticleService articleService = new ArticleService(urlRepository, sitemapNewsClient);

    @Test
    public void testGetArticleHappyPath(){
        Optional<Url> expectedArticle = Optional.of(new Url("string", "string"));
        when(urlRepository.findById(anyString())).thenReturn(expectedArticle);
        Url resultArticle = articleService.getArticle("string");
        assertEquals(expectedArticle.get(), resultArticle);
    }

    @Test
    public void testGetArticle_ArticleNotFound(){
        Optional<Url> expectedArticle = Optional.empty();
        when(urlRepository.findById(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.getArticle("string");
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void addArticleTest(){
        Url expectedArticle = new Url("string", "string");
        articleService.addArticle(expectedArticle);
        verify(urlRepository, times(1)).save(expectedArticle);
    }

    @Test
    public void testUpdateArticleHappyPath(){
        Url expectedArticle = new Url("string", "string");
        when(urlRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
        articleService.updateArticle(expectedArticle);
        verify(urlRepository, times(1)).save(expectedArticle);
    }

    @Test
    public void testUpdateArticle_ArticleNotFound(){
        when(urlRepository.existsById(anyString())).thenReturn(Boolean.FALSE);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.updateArticle(new Url("string", "string"));
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void testDeleteArticleHappyPath(){
        Optional<Url> expectedArticle = Optional.of(new Url("string", "string"));
        when(urlRepository.findById(anyString())).thenReturn(expectedArticle);
        articleService.deleteArticle("string");
        verify(urlRepository, times(1)).deleteById("string");
    }

    @Test
    public void testDeleteArticle_ArticleNotFound(){
        Optional<Url> expectedArticle = Optional.empty();
        when(urlRepository.findById(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () ->{
            articleService.deleteArticle("string");
        });

        String expectedMessage = "Article with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void testGetSitemapNewsHappyPath(){
        String stringResponse = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n" +
                "<url>\n" +
                "<loc>string</loc>\n" +
                "<lastmod>string</lastmod>\n" +
                "</url>\n" +
                "</urlset>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Url> expectedResponse= Arrays.asList(new Url("string", "string"));
        List<Url> actualResponse = articleService.getSitemapNews();
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
        String stringResponse = "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:news=\"http://www.google.com/schemas/sitemap-news/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">\n" +
                "<url>\n" +
                "<loc>string</loc>\n" +
                "<lastmod>string</lastmod>\n" +
                "</url>\n" +
                "</urlset>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Url> expectedArticles= Arrays.asList(new Url("string", "string"));
        articleService.startSitemapNewsMapping();
        verify(urlRepository, times(1)).saveAll(expectedArticles);
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
        verify(urlRepository, times(0)).saveAll(any());
    }
}
