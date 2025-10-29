package edu.famu.cop3060.resources.dto;


import java.util.List;

public record ResourceDTO(
    String id,
    String name,
    String category,
    String location,
    String url,
    List<String> tags
){}
