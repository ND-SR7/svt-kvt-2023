package com.ftn.ac.rs.svtkvt2023.service;

import java.util.List;
import java.util.Map;

public interface GroupSearchService {

    List<Map<String, Object>> searchGroups(String name, String description, String fileContent, Long minPosts, Long maxPosts,
                                           Double minLikes, Double maxLikes, String phrase, String operator);
}
