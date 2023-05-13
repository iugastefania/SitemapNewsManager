package com.ac.upt.sitemapnewsmanager.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "url", schema = "news")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Url {

    @Id
    private String loc;
}
