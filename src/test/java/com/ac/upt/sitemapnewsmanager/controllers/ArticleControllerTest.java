//package com.ac.upt.sitemapnewsmanager.controllers;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//import com.ac.upt.sitemapnewsmanager.models.Article;
//import com.ac.upt.sitemapnewsmanager.models.Sitemap;
//import com.ac.upt.sitemapnewsmanager.payloads.requests.ArticleRequest;
//import com.ac.upt.sitemapnewsmanager.services.ArticleService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Arrays;
//import java.util.List;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(ArticleController.class)
//public class ArticleControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ArticleService articleService;
//
//    @Test
//    public void testGetArticleEndpoint() throws Exception {
//        String loc = "string";
//        Article article = new Article(null, "string", "string", "string", "string", "string", null);
//        when(articleService.getArticle(any())).thenReturn(article);
//
//        mockMvc.perform(get("/api/app/getArticle")
//                        .param("loc", loc))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{\"loc\":\"string\",\"lastmod\":\"string\",\"channelName\":\"string\",\"title\":\"string\",\"description\":\"string\",\"thumbnail\":\"string\",\"user\":null}"));
//    }
//
//    @Test
//    public void testAddArticleEndpoint() throws Exception {
//        ArticleRequest articleRequest = new ArticleRequest(
//                "string",
//                "string",
//                "string",
//                "string",
//                "string",
//                "string",
//                "string"
//        );
//        Article article = new Article(
//                "string",
//                "string",
//                "string",
//                "string",
//                "string",
//                "string",
//                null
//        );
//
//        when(articleService.addArticle(any(ArticleRequest.class))).thenReturn(article);
//
//        mockMvc.perform(post("/api/app/addArticle")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\n" +
//                                "  \"loc\": \"string\",\n" +
//                                "  \"lastmod\": \"string\",\n" +
//                                "  \"channelName\": \"string\",\n" +
//                                "  \"description\": \"string\",\n" +
//                                "  \"thumbnail\": \"string\",\n" +
//                                "  \"title\": \"string\",\n" +
//                                "  \"user\": \"string\"\n" +
//                                "}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.loc").value("string"))
//                .andExpect(jsonPath("$.lastmod").value("string"))
//                .andExpect(jsonPath("$.channelName").value("string"))
//                .andExpect(jsonPath("$.description").value("string"))
//                .andExpect(jsonPath("$.thumbnail").value("string"))
//                .andExpect(jsonPath("$.title").value("string"))
//                .andExpect(jsonPath("$.user").doesNotExist());
//    }
//
//    @Test
//    public void testUpdateArticleEndpoint() throws Exception {
//        Article article = new Article(
//                null,
//                "string",
//                "string",
//                "string",
//                "string",
//                "string",
//                null
//        );
//
//        mockMvc.perform(put("/api/app/updateArticle")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\n" +
//                                "  \"loc\": \"string\",\n" +
//                                "  \"lastmod\": \"string\",\n" +
//                                "  \"channelName\": \"string\",\n" +
//                                "  \"description\": \"string\",\n" +
//                                "  \"thumbnail\": \"string\",\n" +
//                                "  \"title\": \"string\"\n" +
//                                "}"))
//                .andExpect(status().isOk());
//
//        verify(articleService, times(1)).updateArticle(article);
//    }
//
//    @Test
//    public void testDeleteArticleEndpoint() throws Exception {
//        String loc = "string";
//
//        mockMvc.perform(delete("/api/app/deleteArticle")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(loc))
//                .andExpect(status().isOk());
//
//        verify(articleService, times(1)).deleteArticle(loc);
//    }
//
//    @Test
//    public void testGetSitemapNewsEndpoint() throws Exception {
//        List<Sitemap> sitemapList = Arrays.asList(new Sitemap("string", "string"));
//        when(articleService.getAllSitemaps()).thenReturn(sitemapList);
//
//        mockMvc.perform(get("/api/app/getSitemapNews"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("[{\"loc\":\"string\",\"channel\":null}]"));
//    }
//
//    @Test
//    public void testGetUrlNewsEndpoint() throws Exception {
////        List<Article> urlList = Arrays.asList(new Article(null, "string", "string", "string", "string", "string", null));
////        when(articleService.getUrlNews(anyString())).thenReturn(urlList);
////
////        mockMvc.perform(get("/api/app/getUrlNews")
////                        .param("sitemapName", "example-sitemap"))
////                .andExpect(status().isOk())
////                .andExpect(content().json("[{\"loc\":null,\"lastmod\":\"string\",\"channelName\":\"string\",\"title\":\"string\",\"description\":\"string\",\"thumbnail\":\"string\",\"user\":null}]"));
//    }
//
//    @Test
//    public void testDeleteArticleFromChannelEndpoint() throws Exception {
//        String channelName = "channel";
//        String loc = "url";
//
//        mockMvc.perform(delete("/api/app/deleteArticleFromChannel")
//                        .param("channelName", channelName)
//                        .param("loc", loc))
//                .andExpect(status().isOk());
//
//        verify(articleService, times(1)).deleteArticleFromChannel(channelName, loc);
//    }
//
//    @Test
//    public void testTriggerSitemapNewsMappingEndpoint() throws Exception {
//        mockMvc.perform(post("/api/app/triggerSitemapNewsMapping"))
//                .andExpect(status().isOk());
//
//        verify(articleService, times(1)).startSitemapNewsMapping();
//    }
//}
//
