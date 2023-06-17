package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Article;
import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SitemapRepository extends JpaRepository<Sitemap, String> {
    Optional<Sitemap> findByLoc(String loc);
}
