package com.ac.upt.sitemapnewsmanager.controllers;

import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.services.SitemapService;
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
@WebMvcTest(SitemapController.class)
public class SitemapControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SitemapService sitemapService;

    @Test
    public void testGetSitemapEndpoint() throws Exception{
        String loc = "string";
        Url sitemap = new Url("string");
        when(sitemapService.getSitemap(any())).thenReturn(sitemap);
        mockMvc.perform(get("/getSitemap").param("loc", loc)).andExpect(status().isOk()).andExpect(content().string(containsString("{\"loc\":\"string\"}")));

    }

    @Test
    public void testAddSitemapEndpoint() throws Exception{
        Url sitemap = new Url("string");
        mockMvc.perform(post("/addSitemap").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(sitemapService, times(1)).addSitemap(sitemap);
    }

    @Test
    public void testUpdateSitemapEndpoint() throws Exception{
        Url sitemap = new Url("string");
        mockMvc.perform(put("/updateSitemap").contentType(MediaType.APPLICATION_JSON).content("{\n" +
                "  \"loc\": \"string\"\n" +
                "}")).andExpect(status().isOk());
        verify(sitemapService, times(1)).updateSitemap(sitemap);
    }

    @Test
    public void testDeleteSitemapEndpoint() throws Exception{
        Url sitemap = new Url("string");
        String loc = sitemap.getLoc();
        mockMvc.perform(delete("/deleteSitemap").contentType(MediaType.APPLICATION_JSON).content(loc)).andExpect(status().isOk());
        verify(sitemapService, times(1)).deleteSitemap(loc);
    }

    @Test
    public void testGetSitemapNewsEndpoint() throws Exception{
        List<Url> urlList = Arrays.asList(new Url("string"));
        when(sitemapService.getSitemapNews()).thenReturn(urlList);
        mockMvc.perform(get("/getSitemapNews")).andExpect(content().string(containsString("[{\"loc\":\"string\"}]")));
    }


}
