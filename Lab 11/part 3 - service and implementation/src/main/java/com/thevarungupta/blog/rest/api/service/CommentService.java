package com.thevarungupta.blog.rest.api.service;

import com.thevarungupta.blog.rest.api.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getAllComments(Long postId);
    Comment getCommentById(Long postId, Long commentId);
    Comment createComment(Long postId, Comment newComment);
    Comment updateComment(Long postId, Long commentId, Comment updatedComment);
    void deleteComment(Long postId, Long commentId);
}
