package com.thevarungupta.blog.rest.api.service;

import com.thevarungupta.blog.rest.api.entity.Post;

import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    Post getPostById(Long postId);
    Post createPost(Post newPost);
    Post updatePost(Long postId, Post updatedPost);
    void deletePost(Long postId);

}
