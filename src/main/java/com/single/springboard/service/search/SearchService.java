package com.single.springboard.service.search;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.single.springboard.aop.MeasureExecutionTime;
import com.single.springboard.domain.post.dto.PostDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final ElasticsearchTemplate elasticsearchTemplate;

    private List<Object> searchAfter;

    @MeasureExecutionTime
    public List<PostDocumentResponse> findPostsByKeyword(String keyword) {
        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(q ->
                        q.bool(builder -> builder.should(
                                match(m -> m.field("title").query(keyword)),
                                match(m -> m.field("content").query(keyword)),
                                match(m -> m.field("author").query(keyword))
                        )))
                .withSort(builder -> builder.field(sort -> sort
                                .field("_score")
                                .order(SortOrder.Desc)))
                .withSort(builder -> builder.field(sort -> sort
                        .field("modified_date")
                        .order(SortOrder.Desc)))
                .build();

        if (!ObjectUtils.isEmpty(searchAfter)) {
            searchQuery.setSearchAfter(searchAfter);
        }

        SearchHits<PostDocumentResponse> searchHits = elasticsearchTemplate
                .search(searchQuery, PostDocumentResponse.class);

        if (searchHits.hasSearchHits()) {
            searchAfter = searchHits.getSearchHits().get(searchHits.getSearchHits().size() - 1).getSortValues();
        }

        return searchHits.get()
                .map(searchHit -> {
                    PostDocumentResponse document = searchHit.getContent();
                    String documentId = searchHit.getId();

                    return PostDocumentResponse.builder()
                            .id(documentId)
                            .author(document.getAuthor())
                            .title(document.getTitle())
                            .content(document.getContent())
                            .modifiedDate(document.getModifiedDate())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
