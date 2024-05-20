package com.ftn.ac.rs.svtkvt2023.indexrepository;

import com.ftn.ac.rs.svtkvt2023.indexmodel.GroupIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupIndexRepository
    extends ElasticsearchRepository<GroupIndex, String> {
}
