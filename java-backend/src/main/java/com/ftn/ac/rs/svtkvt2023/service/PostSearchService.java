package com.ftn.ac.rs.svtkvt2023.service;

import java.util.List;
import java.util.Map;

public interface PostSearchService {

    List<Map<String, Object>> searchPosts(String title, String fullContent, String fileContent, Long minLikes, Long maxLikes,
                                          Long minComments, Long maxComments, String commentContent, String phrase, String operator);
}
