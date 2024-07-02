package com.ftn.ac.rs.svtkvt2023.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.ftn.ac.rs.svtkvt2023.indexmodel.GroupIndex;
import com.ftn.ac.rs.svtkvt2023.indexmodel.PostIndex;
import com.ftn.ac.rs.svtkvt2023.service.PostSearchService;
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
public class PostSearchServiceImpl implements PostSearchService {

    private final ElasticsearchOperations elasticsearchRestTemplate;

    @Override
    public List<Map<String, Object>> searchPosts(String title, String fullContent, String fileContent, Long minLikes,
                                                 Long maxLikes, Long minComments, Long maxComments, String commentContent,
                                                 String operator) {
        if (title == null && fullContent == null && fileContent == null && minLikes == null && maxLikes == null &&
                minComments == null && maxComments == null && commentContent == null && operator == null) {
            return Collections.emptyList();
        }

        // dinamicki sazetak
        ArrayList<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("title"));
        fields.add(new HighlightField("full_content"));
        fields.add(new HighlightField("file_content"));
        fields.add(new HighlightField("comment_content"));
        HighlightQuery highlightQuery = new HighlightQuery(new Highlight(fields), GroupIndex.class);

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(BoolQuery.of(q -> q.must(mb -> mb.bool(b -> {
                    // upotreba AND ili OR operatora
                    boolean useAnd = "AND".equalsIgnoreCase(operator);

                    if (title != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("title")
                                .fuzziness(Fuzziness.ONE.asString()).query(title)));
                        else b.should(sb -> sb.match(m -> m.field("title")
                                .fuzziness(Fuzziness.ONE.asString()).query(title)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("title")
                                .slop(1).query(title)));
                    }

                    if (fullContent != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("full_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fullContent)));
                        else b.should(sb -> sb.match(m -> m.field("full_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fullContent)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("full_content")
                                .slop(1).query(fullContent)));
                    }

                    if (fileContent != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("file_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fileContent)));
                        else b.should(sb -> sb.match(m -> m.field("file_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(fileContent)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("file_content")
                                .slop(1).query(fileContent)));
                    }

                    if (minLikes != null || maxLikes != null) {
                        String minVal = minLikes != null ? minLikes.toString() : "0";
                        String maxVal = maxLikes != null ? maxLikes.toString() : String.valueOf(Long.MAX_VALUE);

                        if (useAnd) b.must(sb -> sb.range(r -> r.field("number_of_likes")
                                .from(minVal).to(maxVal)));
                        else b.should(sb -> sb.range(r -> r.field("number_of_likes")
                                .from(minVal).to(maxVal)));
                    }

                    if (minComments != null || maxComments != null) {
                        String minVal = minComments != null ? minComments.toString() : "0";
                        String maxVal = maxComments != null ? maxComments.toString() : String.valueOf(Long.MAX_VALUE);

                        if (useAnd) b.must(sb -> sb.range(r -> r.field("number_of_comments")
                                .from(minVal).to(maxVal)));
                        else b.should(sb -> sb.range(r -> r.field("number_of_comments")
                                .from(minVal).to(maxVal)));
                    }

                    if (commentContent != null) {
                        if (useAnd) b.must(sb -> sb.match(m -> m.field("comment_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(commentContent)));
                        else b.should(sb -> sb.match(m -> m.field("comment_content")
                                .fuzziness(Fuzziness.ONE.asString()).query(commentContent)));

                        b.should(sb -> sb.matchPhrase(p -> p.field("comment_content")
                                .slop(1).query(commentContent)));
                    }

                    return b;
                })))._toQuery())
                .withHighlightQuery(highlightQuery)
                .build();

        SearchHits<PostIndex> searchHits = elasticsearchRestTemplate.search(searchQuery, PostIndex.class,
                IndexCoordinates.of("post_index"));

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit<PostIndex> hit : searchHits) {
            Map<String, Object> result = new HashMap<>();
            result.put("source", hit.getContent());
            result.put("highlights", hit.getHighlightFields());
            results.add(result);
        }

        return results;
    }
}
