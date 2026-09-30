package com.thevarungupta.blog.rest.api.controller;

import com.thevarungupta.blog.rest.api.entity.Comment;
import com.thevarungupta.blog.rest.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/posts")
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllCommentByPostId(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.getAllComments(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        Comment comment = commentService.getCommentById(postId, commentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable("postId") Long postId, @RequestBody Comment newComment) {
        Comment comment = commentService.createComment(postId, newComment);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId, @RequestBody Comment updatedComment) {
        Comment comment = commentService.updateComment(postId, commentId, updatedComment);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}
