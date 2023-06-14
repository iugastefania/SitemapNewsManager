package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "url", schema = "news")
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

    @ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Url(String loc, String lastmod, String channelName, String title, String description, String thumbnail, User user) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.channelName = channelName;
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.user = user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
