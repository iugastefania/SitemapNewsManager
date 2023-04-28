package com.ac.upt.sitemapnewsmanager.controllers;

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
        Url article = new Url("string", "string");
        when(articleService.getArticle(any())).thenReturn(article);
        mockMvc.perform(get("/getArticle").param("loc", loc)).andExpect(status().isOk()).andExpect(content().string(containsString("{\"loc\":\"string\",\"lastmod\":\"string\"}")));

    }

    @Test
    public void testAddArticleEndpoint() throws Exception{
        Url article = new Url("string", "string");
        mockMvc.perform(post("/addArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\",\n" +
                "  \"lastmod\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).addArticle(article);
    }

    @Test
    public void testUpdateArticleEndpoint() throws Exception{
        Url article = new Url("string", "string");
        mockMvc.perform(put("/updateArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\",\n" +
                "  \"lastmod\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).updateArticle(article);
    }

    @Test
    public void testDeleteArticleEndpoint() throws Exception{
        Url article = new Url("string", "string");
        String loc = article.getLoc();
        mockMvc.perform(delete("/deleteArticle").contentType(MediaType.APPLICATION_JSON).content(loc)).andExpect(status().isOk());
        verify(articleService, times(1)).deleteArticle(loc);
    }

    @Test
    public void testGetSitemapNewsEndpoint() throws Exception{
        List<Url> urlList = Arrays.asList(new Url("string", "string"));
        when(articleService.getSitemapNews()).thenReturn(urlList);
        mockMvc.perform(get("/getSitemapNews")).andExpect(content().string(containsString("[{\"loc\":\"string\",\"lastmod\":\"string\"}]")));
    }


}
