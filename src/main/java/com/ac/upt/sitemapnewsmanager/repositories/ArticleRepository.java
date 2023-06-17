package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Article;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

  List<Article> findAllByChannelName(String channelName);

  Optional<Article> findByLoc(String loc);

  Optional<Article> findByChannelNameAndLoc(String channelName, String loc);

  Long countAllByChannelName(String channelName);

  @Query("SELECT MAX(u.lastmod) FROM Article u WHERE u.channelName = :channelName")
  String findLatestLastmodByChannelName(@Param("channelName") String channelName);

  List<Article> findAllBySitemapLoc(String sitemapLoc);
}
