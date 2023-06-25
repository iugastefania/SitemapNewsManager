package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Sitemap;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SitemapRepository extends JpaRepository<Sitemap, Long> {
  Optional<Sitemap> findByLoc(String loc);

  Optional<Sitemap> findByChannel(String channel);
}
