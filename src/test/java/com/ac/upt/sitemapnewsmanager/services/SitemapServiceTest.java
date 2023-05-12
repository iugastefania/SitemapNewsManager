package com.ac.upt.sitemapnewsmanager.services;

import com.ac.upt.sitemapnewsmanager.clients.SitemapNewsClient;
import com.ac.upt.sitemapnewsmanager.exceptions.SitemapNotFoundException;
import com.ac.upt.sitemapnewsmanager.models.Url;
import com.ac.upt.sitemapnewsmanager.repositories.UrlRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SitemapServiceTest {
    UrlRepository urlRepository = mock(UrlRepository.class);

    SitemapNewsClient sitemapNewsClient = mock(SitemapNewsClient.class);

    SitemapService sitemapService = new SitemapService(urlRepository, sitemapNewsClient);

    @Test
    public void testGetSitemapHappyPath(){
        Optional<Url> expectedSitemap = Optional.of(new Url("string"));
        when(urlRepository.findById(anyString())).thenReturn(expectedSitemap);
        Url resultSitemap = sitemapService.getSitemap("string");
        assertEquals(expectedSitemap.get(), resultSitemap);
    }

    @Test
    public void testGetSitemap_SitemapNotFound(){
        Optional<Url> expectedSitemap = Optional.empty();
        when(urlRepository.findById(anyString())).thenReturn(expectedSitemap);
        Exception exception = assertThrows(SitemapNotFoundException.class, () ->{
            sitemapService.getSitemap("string");
        });

        String expectedMessage = "Sitemap with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void addSitemapTest(){
        Url expectedSitemap = new Url("string");
        sitemapService.addSitemap(expectedSitemap);
        verify(urlRepository, times(1)).save(expectedSitemap);
    }

    @Test
    public void testUpdateSitemapHappyPath(){
        Url expectedSitemap = new Url("string");
        when(urlRepository.existsById(anyString())).thenReturn(Boolean.TRUE);
        sitemapService.updateSitemap(expectedSitemap);
        verify(urlRepository, times(1)).save(expectedSitemap);
    }

    @Test
    public void testUpdateSitemap_SitemapNotFound(){
        when(urlRepository.existsById(anyString())).thenReturn(Boolean.FALSE);
        Exception exception = assertThrows(SitemapNotFoundException.class, () ->{
            sitemapService.updateSitemap(new Url("string"));
        });

        String expectedMessage = "Sitemap with url: string was not found.";
        String actualMessage = exception.getMessage();

        assertEquals(actualMessage,expectedMessage);
    }

    @Test
    public void testDeleteSitemapHappyPath(){
        Optional<Url> expectedSitemap = Optional.of(new Url("string"));
        when(urlRepository.findById(anyString())).thenReturn(expectedSitemap);
        sitemapService.deleteSitemap("string");
        verify(urlRepository, times(1)).deleteById("string");
    }

    @Test
    public void testDeleteSitemap_SitemapNotFound(){
        Optional<Url> expectedSitemap = Optional.empty();
        when(urlRepository.findById(anyString())).thenReturn(expectedSitemap);
        Exception exception = assertThrows(SitemapNotFoundException.class, () ->{
            sitemapService.deleteSitemap("string");
        });

        String expectedMessage = "Sitemap with url: string was not found.";
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
        List<Url> expectedResponse= List.of(new Url("string"));
        List<Url> actualResponse = sitemapService.getSitemapNews();
        assertEquals(actualResponse, expectedResponse);
    }

    @Test
    public void testGetSitemapNews_JsonProcessingException(){
        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
        Exception exception = assertThrows(RuntimeException.class, () ->{
            sitemapService.getSitemapNews();
        });

        String expectedMessage = "JsonParseException";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testStartSitemapNewsMappingHappyPath(){
        sitemapService.setMappingRunning(Boolean.FALSE);
        String stringResponse = "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
                "<sitemap>\n" +
                "<loc>string</loc>\n" +
                "</sitemap>\n" +
                "</sitemapindex>";
        when(sitemapNewsClient.getStringResponse()).thenReturn(stringResponse);
        List<Url> expectedSitemaps= List.of(new Url("string"));
        sitemapService.startSitemapNewsMapping();
        verify(urlRepository, times(1)).saveAll(expectedSitemaps);
    }

    @Test
    public void testStartSitemapNewsMapping_Exception(){
        sitemapService.setMappingRunning(Boolean.FALSE);
        when(sitemapNewsClient.getStringResponse()).thenReturn("aaaa");
        Exception exception = assertThrows(RuntimeException.class, () ->{
            sitemapService.startSitemapNewsMapping();
        });

        String expectedMessage = "Exception";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testStartSitemapNewsMapping_IsRunning(){
        sitemapService.setMappingRunning(Boolean.TRUE);
        sitemapService.startSitemapNewsMapping();
        verify(sitemapNewsClient, times(0)).getStringResponse();
        verify(urlRepository, times(0)).saveAll(any());
    }
}
