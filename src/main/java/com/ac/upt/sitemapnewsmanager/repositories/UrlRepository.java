package com.ac.upt.sitemapnewsmanager.repositories;

import com.ac.upt.sitemapnewsmanager.models.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {

    List<Url> findAllByChannelName(String channelName);

    Optional<Url> findByLoc(String loc);

    Optional<Url> findByChannelNameAndLoc(String channelName, String loc);

    Long countAllByChannelName(String channelName);

    @Query("SELECT MAX(u.lastmod) FROM Url u WHERE u.channelName = :channelName")
    String findLatestLastmodByChannelName(@Param("channelName") String channelName);

}
