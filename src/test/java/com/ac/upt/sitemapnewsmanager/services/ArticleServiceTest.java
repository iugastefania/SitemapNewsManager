package com.ac.upt.sitemapnewsmanager.services;



import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Article;
import com.ac.upt.sitemapnewsmanager.models.User;
import com.ac.upt.sitemapnewsmanager.repositories.ArticleRepository;
import com.ac.upt.sitemapnewsmanager.repositories.SitemapRepository;
import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArticleServiceTest {

    SitemapRepository sitemapRepository = mock(SitemapRepository.class);
    ArticleRepository articleRepository = mock(ArticleRepository.class);
    SitemapNewsClient sitemapNewsClient = mock(SitemapNewsClient.class);
    UserRepository userRepository = mock(UserRepository.class);

    ArticleService articleService = new ArticleService(sitemapRepository, articleRepository, sitemapNewsClient, userRepository);

    @Test
    public void testGetArticleHappyPath() {
        Optional<Article> expectedArticle = Optional.of(new Article(null, "string", "string", "string", "string", "string", "string", null));
        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Article resultArticle = articleService.getArticle("string");
        assertEquals(expectedArticle.get(), resultArticle);
    }

    @Test
    public void testGetArticle_ArticleNotFound() {
        Optional<Article> expectedArticle = Optional.empty();
        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.getArticle("string");
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void addArticleTest() throws Exception {
        Article expectedArticle = new Article(null, "string", "string", "string", "string", "string", "string", null);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));
        when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
        Article resultArticle = articleService.addArticle(expectedArticle.toRequest());
        verify(articleRepository, times(1)).save(expectedArticle);
        assertEquals(expectedArticle, resultArticle);
    }

    @Test
    public void testUpdateArticleHappyPath() {
        Article expectedArticle = new Article(null, "string", "string", "string", "string", "string", "string", null);
        when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(expectedArticle));
        articleService.updateArticle(expectedArticle);
        verify(articleRepository, times(1)).save(expectedArticle);
    }

    @Test
    public void testUpdateArticle_ArticleNotFound() {
        when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.updateArticle(new Article(null, "string", "string", "string", "string", "string", "string", null));
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testDeleteArticleHappyPath() {
        Optional<Article> expectedArticle = Optional.of(new Article(null, "string", "string", "string", "string", "string", "string", null));
        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        articleService.deleteArticle("string");
        verify(articleRepository, times(1)).delete(expectedArticle.get());
    }

    @Test
    public void testDeleteArticle_ArticleNotFound() {
        Optional<Article> expectedArticle = Optional.empty();
        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
            articleService.deleteArticle("string");
        });

        String expectedMessage = "Article with loc: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void testGetAllArticles() {
        List<Article> expectedArticles = new ArrayList<>();
        expectedArticles.add(new Article(null, "string1", "string", "string", "string", "string", "string", null));
        expectedArticles.add(new Article(null, "string2", "string", "string", "string", "string", "string", null));
        when(articleRepository.findAll()).thenReturn(expectedArticles);
        List<Article> resultArticles = articleService.getAllUrls();
        assertEquals(expectedArticles, resultArticles);
    }
}
  //    SitemapRepository sitemapRepository = mock(SitemapRepository.class);
  //
  //    ArticleRepository articleRepository = mock(ArticleRepository.class);
  //
  //    SitemapNewsClient sitemapNewsClient = mock(SitemapNewsClient.class);
  //
  //    ArticleService articleService = new ArticleService(sitemapRepository, articleRepository,
  // sitemapNewsClient);
  //
  //    @Test
  //    public void testGetArticleHappyPath() {
  //        Optional<Article> expectedArticle = Optional.of(new Article(null, "string", "string", "string",
  // "string", "string", "string"));
  //        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
  //        Article resultArticle = articleService.getArticle("string");
  //        assertEquals(expectedArticle.get(), resultArticle);
  //    }
  //
  //    @Test
  //    public void testGetArticle_ArticleNotFound() {
  //        Optional<Article> expectedArticle = Optional.empty();
  //        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
  //        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
  //            articleService.getArticle("string");
  //        });
  //
  //        String expectedMessage = "Article with loc: string was not found.";
  //        String actualMessage = exception.getMessage();
  //
  //        assertEquals(expectedMessage, actualMessage);
  //    }
  //
  //    @Test
  //    public void addArticleTest() {
  //        Article expectedArticle = new Article(null, "string", "string", "string", "string", "string",
  // "string");
  //        articleService.addArticle(expectedArticle);
  //        verify(articleRepository, times(1)).save(expectedArticle);
  //    }
  //
  //    @Test
  //    public void testUpdateArticleHappyPath() {
  //        Article expectedArticle = new Article(null, "string", "string", "string", "string", "string",
  // "string");
  //        when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(expectedArticle));
  //        articleService.updateArticle(expectedArticle);
  //        verify(articleRepository, times(1)).save(expectedArticle);
  //    }
  //
  //    @Test
  //    public void testUpdateArticle_ArticleNotFound() {
  //        when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
  //        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
  //            articleService.updateArticle(new Article(null, "string", "string", "string", "string",
  // "string", "string"));
  //        });
  //
  //        String expectedMessage = "Article with loc: string was not found.";
  //        String actualMessage = exception.getMessage();
  //
  //        assertEquals(expectedMessage, actualMessage);
  //    }
  //
  //    @Test
  //    public void testDeleteArticleHappyPath() {
  //        Optional<Article> expectedArticle = Optional.of(new Article(null, "string", "string", "string",
  // "string", "string", "string"));
  //        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
  //        articleService.deleteArticle("string");
  //        verify(articleRepository, times(1)).deleteById(expectedArticle.get().getId());
  //    }
  //
  //    @Test
  //    public void testDeleteArticle_ArticleNotFound() {
  //        Optional<Article> expectedArticle = Optional.empty();
  //        when(articleRepository.findByLoc(anyString())).thenReturn(expectedArticle);
  //        Exception exception = assertThrows(ArticleNotFoundException.class, () -> {
  //            articleService.deleteArticle("string");
  //        });
  //
  //        String expectedMessage = "Article with loc: string was not found.";
  //        String actualMessage = exception.getMessage();
  //
  //        assertEquals(expectedMessage, actualMessage);
  //    }
  //
  //    @Test
  //    public void testGetSitemapNewsHappyPath() {
  //        String stringResponse = "<sitemapindex
  // xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
  //                "<sitemap>\n" +
  //                "<loc>string</loc>\n" +
  //                "</sitemap>\n" +
  //                "</sitemapindex>";
  //        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
  //        List<Sitemap> expectedResponse = Arrays.asList(new Sitemap("string"));
  //        List<Sitemap> actualResponse = articleService.getSitemapNews();
  //        assertEquals(actualResponse, expectedResponse);
  //    }
  //
  //    @Test
  //    public void testGetSitemapNews_JsonProcessingException() {
  //        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
  //        Exception exception = assertThrows(RuntimeException.class, () -> {
  //            articleService.getSitemapNews();
  //        });
  //
  //        String expectedMessage = "JsonParseException";
  //        String actualMessage = exception.getMessage();
  //
  //        assertTrue(actualMessage.contains(expectedMessage));
  //    }
  //
  ////    @Test
  ////    public void testStartSitemapNewsMappingHappyPath() {
  ////        articleService.setMappingRunning(Boolean.FALSE);
  ////        String stringResponse = "<sitemapindex
  // xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
  ////                "<sitemap>\n" +
  ////                "<loc>string</loc>\n" +
  ////                "</sitemap>\n" +
  ////                "</sitemapindex>";
  ////        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
  ////        List<Sitemap> expectedSitemaps = Arrays.asList(new Sitemap("string"));
  ////        articleService.startSitemapNewsMapping();
  ////        verify(sitemapRepository, times(1)).saveAll(expectedSitemaps);
  ////    }
  ////
  ////    @Test
  ////    public void testStartSitemapNewsMapping_Exception() {
  ////        articleService.setMappingRunning(Boolean.FALSE);
  ////        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
  ////        Exception exception = assertThrows(RuntimeException.class, () -> {
  ////            articleService.startSitemapNewsMapping();
  ////        });
  ////
  ////        String expectedMessage = "Mapping has failed.";
  ////        String actualMessage = exception.getMessage();
  ////
  ////        assertTrue(actualMessage.contains(expectedMessage));
  ////    }
  //
  //    @Test
  //    public void testStartSitemapNewsMapping_IsRunning() {
  //        articleService.setMappingRunning(Boolean.TRUE);
  //        articleService.startSitemapNewsMapping();
  //        verify(sitemapNewsClient, times(0)).getStringResponse();
  //        verify(sitemapRepository, times(0)).saveAll(any());
  //    }
  //
  // add tests
//}
