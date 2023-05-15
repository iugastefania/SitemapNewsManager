package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SitemapRepository extends JpaRepository<Sitemap, String> {
}
