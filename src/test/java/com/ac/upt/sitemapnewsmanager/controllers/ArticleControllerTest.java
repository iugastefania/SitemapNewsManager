package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
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
        Sitemap sitemap = new Sitemap("string");
        when(articleService.getArticle(any())).thenReturn(sitemap);
        mockMvc.perform(get("/getArticle").param("loc", loc)).andExpect(status().isOk()).andExpect(content().string(containsString("{\"loc\":\"string\"}")));

    }

    @Test
    public void testAddArticleEndpoint() throws Exception{
        Sitemap sitemap = new Sitemap("string");
        mockMvc.perform(post("/addArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).addArticle(sitemap);
    }

    @Test
    public void testUpdateArticleEndpoint() throws Exception{
        Sitemap sitemap = new Sitemap("string");
        mockMvc.perform(put("/updateArticle").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(articleService, times(1)).updateArticle(sitemap);
    }

    @Test
    public void testDeleteArticleEndpoint() throws Exception{
        Sitemap sitemap = new Sitemap("string");
        String loc = sitemap.getLoc();
        mockMvc.perform(delete("/deleteArticle").contentType(MediaType.APPLICATION_JSON).content(loc)).andExpect(status().isOk());
        verify(articleService, times(1)).deleteArticle(loc);
    }

    @Test
    public void testGetSitemapNewsEndpoint() throws Exception{
        List<Sitemap> sitemapList = Arrays.asList(new Sitemap("string"));
        when(articleService.getSitemapNews()).thenReturn(sitemapList);
        mockMvc.perform(get("/getSitemapNews")).andExpect(content().string(containsString("[{\"loc\":\"string\"}]")));
    }


}
