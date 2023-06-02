package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.services.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ArticleController.class)
public class ArticleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ArticleService articleService;

    @Test
    public void testGetArticleEndpoint() throws Exception{
        String loc = "string";
        Url article = new Url(null, "string", "string", "string", "string", "string", "string");
        when(articleService.getArticle(any())).thenReturn(article);
        mockMvc.perform(get("/getArticle").param("loc", loc)).andExpect(status().isOk()).andExpect(content().string(containsString("{\"loc\":\"string\",\"lastmod\":\"string\",\"channelName\":\"string\",\"description\":\"string\",\"thumbnail\":\"string\",\"title\":\"string\"}")));
    }

    @Test
    public void testAddArticleEndpoint() throws Exception{
        Url article = new Url(null, "string", "string", "string", "string", "string", "string");
        mockMvc.perform(post("/addArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\",\n" +
                "  \"lastmod\": \"string\",\n" +
                "  \"channelName\": \"string\",\n" +
                "  \"description\": \"string\",\n" +
                "  \"thumbnail\": \"string\",\n" +
                "  \"title\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).addArticle(article);
    }

    @Test
    public void testUpdateArticleEndpoint() throws Exception{
        Url article = new Url(null, "string", "string", "string", "string", "string", "string");
        mockMvc.perform(put("/updateArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\",\n" +
                "  \"lastmod\": \"string\",\n" +
                "  \"channelName\": \"string\",\n" +
                "  \"description\": \"string\",\n" +
                "  \"thumbnail\": \"string\",\n" +
                "  \"title\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).updateArticle(article);
    }

    @Test
    public void testDeleteArticleEndpoint() throws Exception{
        Url article = new Url(null, "string", "string", "string", "string", "string", "string");
        String loc = article.getLoc();
        mockMvc.perform(delete("/deleteArticle").contentType(MediaType.APPLICATION_JSON).content(loc)).andExpect(status().isOk());
        verify(articleService, times(1)).deleteArticle(loc);
    }

    @Test
    public void testGetSitemapNewsEndpoint() throws Exception{
        List<Sitemap> sitemapList = Arrays.asList(new Sitemap("string"));
        when(articleService.getSitemapNews()).thenReturn(sitemapList);
        mockMvc.perform(get("/getSitemapNews")).andExpect(content().string(containsString("[{\"loc\":\"string\"}]")));
    }

    @Test
    public void testGetUrlNewsEndpoint() throws Exception{
        List<Url> urlList = Arrays.asList(new Url(null, "string", "string", "string", "string", "string", "string"));
        when(articleService.getUrlNews()).thenReturn(urlList);
        mockMvc.perform(get("/getUrlNews")).andExpect(content().string(containsString("[{\"loc\":\"string\",\"lastmod\":\"string\",\"channelName\":\"string\",\"description\":\"string\",\"thumbnail\":\"string\",\"title\":\"string\"}]")));
    }

    @Test
    public void testGetAllArticlesByChannelEndpoint() throws Exception {
        String channelName = "channel";
        List<Url> articles = Arrays.asList(
                new Url(null, "url1", "2023-01-01", "channel", "Description 1", "Thumbnail 1", "Title 1"),
                new Url(null, "url2", "2023-01-02", "channel", "Description 2", "Thumbnail 2", "Title 2")
        );
        when(articleService.getAllArticlesByChannel(channelName)).thenReturn(articles);
        mockMvc.perform(get("/getAllArticlesByChannel").param("channelName", channelName))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[{\"loc\":\"url1\",\"lastmod\":\"2023-01-01\",\"channelName\":\"channel\",\"description\":\"Description 1\",\"thumbnail\":\"Thumbnail 1\",\"title\":\"Title 1\"},{\"loc\":\"url2\",\"lastmod\":\"2023-01-02\",\"channelName\":\"channel\",\"description\":\"Description 2\",\"thumbnail\":\"Thumbnail 2\",\"title\":\"Title 2\"}]")));
    }

    @Test
    public void testAddArticleToChannelEndpoint() throws Exception {
        String channelName = "channel";
        Url article = new Url(null, "url", "2023-01-01", channelName, "Description", "Thumbnail", "Title");
        mockMvc.perform(post("/addArticleToChannel")
                        .param("channelName", channelName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"loc\": \"url\",\n" +
                                "  \"lastmod\": \"2023-01-01\",\n" +
                                "  \"channelName\": \"" + channelName + "\",\n" +
                                "  \"description\": \"Description\",\n" +
                                "  \"thumbnail\": \"Thumbnail\",\n" +
                                "  \"title\": \"Title\"\n" +
                                "}"))
                .andExpect(status().isOk());
        verify(articleService, times(1)).addArticleToChannel(channelName, article);
    }

    @Test
    public void testUpdateArticleInChannelEndpoint() throws Exception {
        String channelName = "channel";
        Url article = new Url(null, "url", "2023-01-01", channelName, "Description", "Thumbnail", "Title");
        mockMvc.perform(put("/updateArticleInChannel")
                        .param("channelName", channelName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"loc\": \"url\",\n" +
                                "  \"lastmod\": \"2023-01-01\",\n" +
                                "  \"channelName\": \"" + channelName + "\",\n" +
                                "  \"description\": \"Description\",\n" +
                                "  \"thumbnail\": \"Thumbnail\",\n" +
                                "  \"title\": \"Title\"\n" +
                                "}"))
                .andExpect(status().isOk());
        verify(articleService, times(1)).updateArticleInChannel(channelName, article);
    }

    @Test
    public void testDeleteArticleFromChannelEndpoint() throws Exception {
        String channelName = "channel";
        String loc = "url";
        mockMvc.perform(delete("/deleteArticleFromChannel")
                        .param("channelName", channelName)
                        .param("loc", loc))
                .andExpect(status().isOk());
        verify(articleService, times(1)).deleteArticleFromChannel(channelName, loc);
    }

    @Test
    public void testTriggerSitemapNewsMappingEndpoint() throws Exception {
        mockMvc.perform(post("/triggerSitemapNewsMapping"))
                .andExpect(status().isOk());
        verify(articleService, times(1)).startSitemapNewsMapping();
    }
}
