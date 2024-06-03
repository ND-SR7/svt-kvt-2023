package com.ftn.ac.rs.svtkvt2023.indexmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "post_index")
public class PostIndex {

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "title", analyzer = "serbian", searchAnalyzer = "serbian")
    private String title;

    @Field(type = FieldType.Text, store = true, name = "full_content", analyzer = "serbian", searchAnalyzer = "serbian")
    private String fullContent;

    @Field(type = FieldType.Text, store = true, name = "file_content", analyzer = "serbian", searchAnalyzer = "serbian")
    private String fileContent;

    @Field(type = FieldType.Long, store = true, name = "number_of_likes")
    private Long numberOfLikes;

    @Field(type = FieldType.Long, store = true, name = "number_of_comments")
    private Long numberOfComments;

    @Field(type = FieldType.Text, store = true, name = "comment_content", analyzer = "serbian", searchAnalyzer = "serbian")
    private String commentContent;

    @Field(type = FieldType.Long, store = true, name = "database_id")
    private Long databaseId;
}
