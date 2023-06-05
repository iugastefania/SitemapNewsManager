package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.Url;
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
    public void testGetArticleHappyPath() {
        Optional<Url> expectedArticle = Optional.of(new Url(null, "string", "string", "string", "string", "string", "string"));
        when(urlRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Url resultArticle = articleService.getArticle("string");
        assertEquals(expectedArticle.get(), resultArticle);
    }

    @Test
    public void testGetArticle_ArticleNotFound() {
        Optional<Url> expectedArticle = Optional.empty();
        when(urlRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.getArticle("string");
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void addArticleTest() {
        Url expectedArticle = new Url(null, "string", "string", "string", "string", "string", "string");
        articleService.addArticle(expectedArticle);
        verify(urlRepository, times(1)).save(expectedArticle);
    }

    @Test
    public void testUpdateArticleHappyPath() {
        Url expectedArticle = new Url(null, "string", "string", "string", "string", "string", "string");
        when(urlRepository.findByLoc(anyString())).thenReturn(Optional.of(expectedArticle));
        articleService.updateArticle(expectedArticle);
        verify(urlRepository, times(1)).save(expectedArticle);
    }

    @Test
    public void testUpdateArticle_ArticleNotFound() {
        when(urlRepository.findByLoc(anyString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.updateArticle(new Url(null, "string", "string", "string", "string", "string", "string"));
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testDeleteArticleHappyPath() {
        Optional<Url> expectedArticle = Optional.of(new Url(null, "string", "string", "string", "string", "string", "string"));
        when(urlRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        articleService.deleteArticle("string");
        verify(urlRepository, times(1)).deleteById(expectedArticle.get().getId());
    }

    @Test
    public void testDeleteArticle_ArticleNotFound() {
        Optional<Url> expectedArticle = Optional.empty();
        when(urlRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.deleteArticle("string");
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testGetSitemapNewsHappyPath() {
        String stringResponse = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "<sitemap>\n" +
                "<loc>string</loc>\n" +
                "</sitemap>\n" +
                "</sitemapindex>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Sitemap> expectedResponse = Arrays.asList(new Sitemap("string"));
        List<Sitemap> actualResponse = articleService.getSitemapNews();
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void testGetSitemapNews_JsonProcessingException() {
        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            articleService.getSitemapNews();
        });

        String expectedMessage = "JsonParseException";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

//    @Test
//    public void testStartSitemapNewsMappingHappyPath() {
//        articleService.setMappingRunning(Boolean.FALSE);
//        String stringResponse = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
//                "<sitemap>\n" +
//                "<loc>string</loc>\n" +
//                "</sitemap>\n" +
//                "</sitemapindex>";
//        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
//        List<Sitemap> expectedSitemaps = Arrays.asList(new Sitemap("string"));
//        articleService.startSitemapNewsMapping();
//        verify(sitemapRepository, times(1)).saveAll(expectedSitemaps);
//    }
//
//    @Test
//    public void testStartSitemapNewsMapping_Exception() {
//        articleService.setMappingRunning(Boolean.FALSE);
//        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            articleService.startSitemapNewsMapping();
//        });
//
//        String expectedMessage = "Mapping has failed.";
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));
//    }

    @Test
    public void testStartSitemapNewsMapping_IsRunning() {
        articleService.setMappingRunning(Boolean.TRUE);
        articleService.startSitemapNewsMapping();
        verify(sitemapNewsClient, times(0)).getStringResponse();
        verify(sitemapRepository, times(0)).saveAll(any());
    }

    //add tests
}
