package com.ftn.ac.rs.svtkvt2023.indexrepository;

import com.ftn.ac.rs.svtkvt2023.indexmodel.PostIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostIndexRepository
    extends ElasticsearchRepository<PostIndex, String> {
}
