package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, String> {
}
