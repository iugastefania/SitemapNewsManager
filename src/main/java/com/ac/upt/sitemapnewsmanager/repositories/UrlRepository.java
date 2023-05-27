package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    List<Url> findAllByChannelName(String channelName);

    Optional<Url> findByLoc(String loc);

    Optional<Url> findByChannelNameAndLoc(String channelName, String loc);

}
