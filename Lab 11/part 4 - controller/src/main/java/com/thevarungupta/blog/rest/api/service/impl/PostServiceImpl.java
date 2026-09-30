package com.thevarungupta.blog.rest.api.service.impl;

import com.thevarungupta.blog.rest.api.entity.Post;
import com.thevarungupta.blog.rest.api.repository.PostRepository;
import com.thevarungupta.blog.rest.api.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostRepository postRepository;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post getPostById(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        return post;
    }

    @Override
    public Post createPost(Post newPost) {
        return postRepository.save(newPost);
    }

    @Override
    public Post updatePost(Long postId, Post updatedPost) {
        // find post by id
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        // update post details
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setDescription(updatedPost.getDescription());
        // save updated post
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        Post post = postRepository
                .findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        postRepository.delete(post);
    }
}
