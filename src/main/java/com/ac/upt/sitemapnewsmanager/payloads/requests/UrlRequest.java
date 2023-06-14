package com.ac.upt.sitemapnewsmanager.payloads.requests;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrlRequest {
    private String loc;

    private String lastmod;

    private String channelName;

    private String title;

    private String description;

    private String thumbnail;

    private String user;

}
