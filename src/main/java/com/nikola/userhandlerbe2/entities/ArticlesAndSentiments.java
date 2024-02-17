package com.nikola.userhandlerbe2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticlesAndSentiments {
    private List<String> articles;
    private String sentiments;
}
