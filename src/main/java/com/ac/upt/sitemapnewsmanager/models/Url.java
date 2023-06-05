package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "urls", schema = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String loc;
    private String lastmod;
    private String channelName;
    @Column(columnDefinition="text")
    private String title;
    @Column(columnDefinition="text")
    private String description;
    @Column(columnDefinition="text")
    private String thumbnail;
}
