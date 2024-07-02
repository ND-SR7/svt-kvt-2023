package com.ftn.ac.rs.svtkvt2023.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.ftn.ac.rs.svtkvt2023.indexmodel.GroupIndex;
import com.ftn.ac.rs.svtkvt2023.service.GroupSearchService;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupSearchServiceImpl implements GroupSearchService {

    private final ElasticsearchOperations elasticsearchRestTemplate;

    @Override
    public List<Map<String, Object>> searchGroups(String name, String description, String fileContent, Long minPosts, Long maxPosts,
                                                  Double minLikes, Double maxLikes, String operator) {
        if (name == null && description == null && fileContent == null
                && minPosts == null && maxPosts == null && minLikes == null && maxLikes == null) {
            return Collections.emptyList();
        }

        // dinamicki sazetak
        ArrayList<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("name"));
        fields.add(new HighlightField("description"));
        fields.add(new HighlightField("file_content"));
        HighlightQuery highlightQuery = new HighlightQuery(new Highlight(fields), GroupIndex.class);

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
                    // upotreba AND ili OR operatora
                    boolean useAnd = "AND".equalsIgnoreCase(operator);

                    if (name != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("name")
                                .fuzziness(Fuzziness.ONE.asString()).query(name)));
                        else b.should(sb -> sb.match(m -> m.field("name")
                                .fuzziness(Fuzziness.ONE.asString()).query(name)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("name")
                                .slop(1).query(name)));
                    }

                    if (description != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("description")
                                .fuzziness(Fuzziness.ONE.asString()).query(description)));
                        else b.should(sb -> sb.match(m -> m.field("description")
                                .fuzziness(Fuzziness.ONE.asString()).query(description)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("description")
                                .slop(1).query(description)));
                    }

                    if (fileContent != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("file_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fileContent)));
                        else b.should(sb -> sb.match(m -> m.field("file_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fileContent)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("file_content")
                                .slop(1).query(fileContent)));
                    }

                    if (minPosts != null || maxPosts != null) {
                        String minVal = minPosts != null ? minPosts.toString() : "0";
                        String maxVal = maxPosts != null ? maxPosts.toString() : String.valueOf(Long.MAX_VALUE);

                        if (useAnd) b.must(sb -> sb.range(r -> r.field("number_of_posts")
                                .from(minVal).to(maxVal)));
                        else b.should(sb -> sb.range(r -> r.field("number_of_posts")
                                .from(minVal).to(maxVal)));
                    }

                    if (minLikes != null || maxLikes != null) {
                        String minVal = minLikes != null ? minLikes.toString() : "0";
                        String maxVal = maxLikes != null ? maxLikes.toString() : String.valueOf(Long.MAX_VALUE);

                        if (useAnd) b.must(sb -> sb.range(r -> r.field("average_likes")
                                .from(minVal).to(maxVal)));
                        else b.should(sb -> sb.range(r -> r.field("average_likes")
                                .from(minVal).to(maxVal)));
                    }

                    return b;
                })))._toQuery())
                .withHighlightQuery(highlightQuery)
                .build();

        SearchHits<GroupIndex> searchHits = elasticsearchRestTemplate.search(searchQuery, GroupIndex.class,
                IndexCoordinates.of("group_index"));

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit<GroupIndex> hit : searchHits) {
            Map<String, Object> result = new HashMap<>();
            result.put("source", hit.getContent());
            result.put("highlights", hit.getHighlightFields());
            results.add(result);
        }

        return results;
    }
}
