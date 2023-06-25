//package com.ac.upt.sitemapnewsmanager.services;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//import com.ac.upt.sitemapnewsmanager.exceptions.ArticleNotFoundException;
//import com.ac.upt.sitemapnewsmanager.models.Article;
//import com.ac.upt.sitemapnewsmanager.models.Role;
//import com.ac.upt.sitemapnewsmanager.models.Sitemap;
//import com.ac.upt.sitemapnewsmanager.models.User;
//import com.ac.upt.sitemapnewsmanager.payloads.requests.ArticleRequest;
//import com.ac.upt.sitemapnewsmanager.payloads.requests.SitemapRequest;
//import com.ac.upt.sitemapnewsmanager.repositories.ArticleRepository;
//import com.ac.upt.sitemapnewsmanager.repositories.SitemapRepository;
//import com.ac.upt.sitemapnewsmanager.repositories.UserRepository;
//import java.util.*;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest
//public class ArticleServiceTest {
//  @MockBean private SitemapRepository sitemapRepository;
//
//  @MockBean private ArticleRepository articleRepository;
//
//  @MockBean private UserRepository userRepository;
//
//  @Autowired private ArticleService articleService;
//
//  private final ArticleRequest articleRequest =
//      new ArticleRequest(
//          1L,
//          "mockLoc",
//          "mockDate",
//          "mockChannel",
//          "mockTitle",
//          "mockDescription",
//          "mockThumbnail",
//          "mockUser");
//  private final User user = new User("mockMail", "mockUser", "mockPassword", Role.ADMINISTRATOR);
//  private final Sitemap sitemap = new Sitemap("mockUrl", "mockChannel");
//  private final Article article =
//      new Article(
//          1L,
//          "mockLoc",
//          "mockDate",
//          "mockChannel",
//          "mockTitle",
//          "mockDescription",
//          "mockThumbnail",
//          1L);
//
//  @Test
//  public void updateArticleSuccess() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(article));
//    when(sitemapRepository.findByChannel(anyString())).thenReturn(Optional.of(sitemap));
//    when(articleRepository.save(any(Article.class))).thenReturn(article);
//
//    // when
//    articleService.updateArticle(article);
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(1)).findByChannel(anyString());
//    verify(articleRepository, times(1)).save(any(Article.class));
//  }
//
//  @Test
//  public void updateArticleNotFound() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(ArticleNotFoundException.class, () -> articleService.updateArticle(article));
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(0)).findByChannel(anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void getArticleSuccess() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(article));
//
//    // when
//    Article result = articleService.getArticle("mockLoc");
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    assertEquals(article, result);
//  }
//
//  @Test
//  public void getArticleNotFound() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(ArticleNotFoundException.class, () -> articleService.getArticle("mockLoc"));
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//  }
//
//  @Test
//  public void deleteArticleSuccess() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(article));
//
//    // when
//    articleService.deleteArticle("mockLoc");
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(articleRepository, times(1)).deleteById(article.getId());
//  }
//
//  @Test
//  public void deleteArticleNotFound() {
//    // given
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(ArticleNotFoundException.class, () -> articleService.deleteArticle("mockLoc"));
//
//    // then
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(articleRepository, times(0)).deleteById(anyLong());
//  }
//
//  @Test
//  public void getAllArticlesByChannel() {
//    // given
//    List<Article> articleList = new ArrayList<>();
//    articleList.add(article);
//    when(articleRepository.findAllByChannelName(anyString())).thenReturn(articleList);
//
//    // when
//    List<Article> result = articleService.getAllArticlesByChannel("mockChannel");
//
//    // then
//    verify(articleRepository, times(1)).findAllByChannelName(anyString());
//    assertEquals(articleList, result);
//  }
//
//  @Test
//  public void addArticleSuccess() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//    when(sitemapRepository.findByChannel(anyString())).thenReturn(Optional.of(sitemap));
//    when(articleRepository.save(any(Article.class))).thenReturn(article);
//
//    article.setSitemapId(sitemap.getId());
//    article.setUserId(user.getId());
//
//    // when
//    Article result = articleService.addArticle(articleRequest);
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(1)).findByChannel(anyString());
//    verify(articleRepository, times(1)).save(any(Article.class));
//    assertEquals(article, result);
//  }
//
//  @Test
//  public void addArticleUserNotFound() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(Exception.class, () -> articleService.addArticle(articleRequest));
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(0)).findByLoc(anyString());
//    verify(sitemapRepository, times(0)).findByChannel(anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void addArticleAlreadyExists() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(article));
//
//    // when
//    assertThrows(IllegalArgumentException.class, () -> articleService.addArticle(articleRequest));
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(0)).findByChannel(anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void addArticleNoSitemapFound() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//    when(sitemapRepository.findByChannel(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(IllegalArgumentException.class, () -> articleService.addArticle(articleRequest));
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(1)).findByChannel(anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void addArticleToChannelSuccess() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//    when(articleRepository.save(any(Article.class))).thenReturn(article);
//
//    // when
//    Article result = articleService.addArticleToChannel("mockChannel", articleRequest);
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(articleRepository, times(1)).save(any(Article.class));
//
//    article.setUserId(user.getId());
//    assertEquals(article, result);
//  }
//
//  @Test
//  public void addArticleToChannelAlreadyExists() throws Exception {
//    // given
//    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//    when(articleRepository.findByLoc(anyString())).thenReturn(Optional.of(article));
//
//    // when
//    assertThrows(
//        IllegalArgumentException.class,
//        () -> articleService.addArticleToChannel("mockChannel", articleRequest));
//
//    // then
//    verify(userRepository, times(1)).findByUsername(anyString());
//    verify(articleRepository, times(1)).findByLoc(anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void updateArticleInChannelSuccess() {
//    // given
//    when(articleRepository.findByChannelNameAndLoc(anyString(), anyString()))
//        .thenReturn(Optional.of(article));
//    when(articleRepository.save(any(Article.class))).thenReturn(article);
//
//    // when
//    articleService.updateArticleInChannel("mockChannel", article);
//
//    // then
//    verify(articleRepository, times(1)).findByChannelNameAndLoc(anyString(), anyString());
//    verify(articleRepository, times(1)).save(any(Article.class));
//  }
//
//  @Test
//  public void updateArticleInChannelNotFound() {
//    // given
//    when(articleRepository.findByChannelNameAndLoc(anyString(), anyString()))
//        .thenReturn(Optional.empty());
//
//    // when
//    assertThrows(
//        ArticleNotFoundException.class,
//        () -> articleService.updateArticleInChannel("mockChannel", article));
//
//    // then
//    verify(articleRepository, times(1)).findByChannelNameAndLoc(anyString(), anyString());
//    verify(articleRepository, times(0)).save(any(Article.class));
//  }
//
//  @Test
//  public void deleteArticleFromChannelSuccess() {
//    // given
//    when(articleRepository.findByChannelNameAndLoc(anyString(), anyString()))
//        .thenReturn(Optional.of(article));
//
//    // when
//    articleService.deleteArticleFromChannel("mockChannel", "mockLoc");
//
//    // then
//    verify(articleRepository, times(1)).findByChannelNameAndLoc(anyString(), anyString());
//    verify(articleRepository, times(1)).delete(any(Article.class));
//  }
//
//  @Test
//  public void deleteArticleFromChannelNotFound() {
//    // given
//    when(articleRepository.findByChannelNameAndLoc(anyString(), anyString()))
//        .thenReturn(Optional.empty());
//
//    // when
//    assertThrows(
//        ArticleNotFoundException.class,
//        () -> articleService.deleteArticleFromChannel("mockChannel", "mockLoc"));
//
//    // then
//    verify(articleRepository, times(1)).findByChannelNameAndLoc(anyString(), anyString());
//    verify(articleRepository, times(0)).delete(any(Article.class));
//  }
//
//  @Test
//  public void getAllChannelNames() {
//    // given
//    List<Article> articleList = new ArrayList<>();
//    articleList.add(article);
//    when(articleRepository.findAll()).thenReturn(articleList);
//
//    // when
//    List<String> result = articleService.getAllChannelNames();
//
//    // then
//    verify(articleRepository, times(1)).findAll();
//    assertEquals(Collections.singletonList("mockChannel"), result);
//  }
//
//  @Test
//  public void getAllArticles() {
//    // given
//    List<Article> articleList = new ArrayList<>();
//    articleList.add(article);
//    when(articleRepository.findAll()).thenReturn(articleList);
//
//    // when
//    List<Article> result = articleService.getAllArticles();
//
//    // then
//    verify(articleRepository, times(1)).findAll();
//    assertEquals(articleList, result);
//  }
//
//  @Test
//  public void countUrlsByChannel() {
//    // given
//    when(articleRepository.countAllByChannelName(anyString())).thenReturn(1L);
//
//    // when
//    Long result = articleService.countUrlsByChannel("mockChannel");
//
//    // then
//    verify(articleRepository, times(1)).countAllByChannelName(anyString());
//    assertEquals(1L, result.longValue());
//  }
//
//  @Test
//  void testGetAllSitemaps() {
//    // Given
//    List<Sitemap> sitemaps = new ArrayList<>();
//    when(sitemapRepository.findAll()).thenReturn(sitemaps);
//
//    // When
//    List<Sitemap> result = articleService.getAllSitemaps();
//
//    // Then
//    assertEquals(sitemaps, result);
//    verify(sitemapRepository).findAll();
//  }
//
//  @Test
//  public void getLatestLastmodByChannelSuccess() {
//    // given
//    String expectedLastmod = "2023-05-15T10:30:00Z";
//    when(articleRepository.findLatestLastmodByChannelName(anyString())).thenReturn(expectedLastmod);
//
//    // when
//    String result = articleService.getLatestLastmodByChannel("mockChannel");
//
//    // then
//    verify(articleRepository, times(1)).findLatestLastmodByChannelName(anyString());
//    assertEquals(expectedLastmod, result);
//  }
//
//  @Test
//  public void addSitemapSuccess() {
//    // given
//    String loc = "http://example.com/sitemap.xml";
//    String channel = "mockChannel";
//    SitemapRequest sitemapRequest = new SitemapRequest(loc, channel);
//    when(sitemapRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//    when(sitemapRepository.save(any(Sitemap.class))).thenReturn(new Sitemap(loc, channel));
//
//    // when
//    Sitemap result = articleService.addSitemap(sitemapRequest);
//
//    // then
//    verify(sitemapRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(1)).save(any(Sitemap.class));
//    assertEquals(loc, result.getLoc());
//    assertEquals(channel, result.getChannel());
//  }
//
//  @Test
//  public void addSitemapAlreadyExists() {
//    // given
//    String loc = "http://example.com/sitemap.xml";
//    String channel = "mockChannel";
//    SitemapRequest sitemapRequest = new SitemapRequest(loc, channel);
//    when(sitemapRepository.findByLoc(anyString()))
//        .thenReturn(Optional.of(new Sitemap(loc, channel)));
//
//    // when
//    assertThrows(IllegalArgumentException.class, () -> articleService.addSitemap(sitemapRequest));
//
//    // then
//    verify(sitemapRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(0)).save(any(Sitemap.class));
//  }
//
//  @Test
//  public void deleteSitemapSuccess() {
//    // given
//    String loc = "http://example.com/sitemap.xml";
//    Sitemap sitemap = new Sitemap(loc, "mockChannel");
//    when(sitemapRepository.findByLoc(anyString())).thenReturn(Optional.of(sitemap));
//
//    // when
//    articleService.deleteSitemap(loc);
//
//    // then
//    verify(sitemapRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(1)).deleteById(sitemap.getId());
//  }
//
//  @Test
//  public void deleteSitemapNotFound() {
//    // given
//    String loc = "http://example.com/sitemap.xml";
//    when(sitemapRepository.findByLoc(anyString())).thenReturn(Optional.empty());
//
//    // when
//    assertThrows(ArticleNotFoundException.class, () -> articleService.deleteSitemap(loc));
//
//    // then
//    verify(sitemapRepository, times(1)).findByLoc(anyString());
//    verify(sitemapRepository, times(0)).deleteById(anyLong());
//  }
//}
