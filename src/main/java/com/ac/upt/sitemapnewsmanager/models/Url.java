package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "url", schema = "sitemaps")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {

    @Id
    private String loc;
}
