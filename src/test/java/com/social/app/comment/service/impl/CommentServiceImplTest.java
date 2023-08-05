package com.social.app.comment.service.impl;

import com.social.app.comment.dto.CommentDTO;
import com.social.app.comment.dto.CreateUpdateCommentDTO;
import com.social.app.comment.model.Comment;
import com.social.app.comment.repository.CommentRepository;
import com.social.app.comment.service.CommentService;
import com.social.app.post.model.Post;
import com.social.app.post.service.PostService;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class CommentServiceImplTest {

    @Autowired
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Test
    public void testCreateComment_Success() {
        // Create sample input data
        Long postId = 1L;
        String username = "user";
        CreateUpdateCommentDTO request = new CreateUpdateCommentDTO();
        request.setText("Test comment");

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        Post post = new Post();
        post.setId(postId);
        post.setUser(user);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPost(post);
        comment.setApplicationUser(user);

        // Mock the behavior of userService.getUserByUsername()
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Mock the behavior of postService.getPost()
        when(postService.getPost(postId)).thenReturn(post);

        // Mock the behavior of commentRepository.save()
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Perform the createComment operation
        CommentDTO result = commentService.createComment(request, postId, username);

        // Verify that userService.getUserByUsername() and postService.getPost() were called once each
        verify(userService, times(1)).getUserByUsername(anyString());
        verify(postService, times(1)).getPost(anyLong());

        // Verify that commentRepository.save() was called once
        verify(commentRepository, times(1)).save(any(Comment.class));

        // Verify the returned result
        assertNotNull(result);
        assertEquals(request.getText(), result.getText());
        assertEquals(postId, result.getPost().getId());
        assertEquals(username, result.getApplicationUser().getUsername());
    }

    @Test
    public void testUpdateComment_Success() {
        // Create sample input data
        Long commentId = 1L;
        String username = "user";
        CreateUpdateCommentDTO request = new CreateUpdateCommentDTO();
        request.setText("Updated comment");

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        Post post = new Post();
        post.setId(1L);
        post.setUser(user);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setText("Old comment");
        comment.setPost(post);
        comment.setApplicationUser(user);

        // Mock the behavior of userService.getUserByUsername()
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Mock the behavior of commentRepository.findById() and commentRepository.save()
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Perform the updateComment operation
        CommentDTO result = commentService.updateComment(request, commentId, username);

        // Verify that userService.getUserByUsername() was called once
        verify(userService, times(1)).getUserByUsername(anyString());

        // Verify that commentRepository.findById() and commentRepository.save() were called once each
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));

        // Verify the returned result
        assertNotNull(result);
        assertEquals(request.getText(), result.getText());
        assertEquals(1L, result.getPost().getId().longValue());
        assertEquals(username, result.getApplicationUser().getUsername());
    }

    @Test
    public void testDeleteComment_Success() {
        // Create sample input data
        Long commentId = 1L;
        String username = "user";

        Comment comment = new Comment();
        comment.setId(commentId);

        // Mock the behavior of commentRepository.findById()
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Perform the deleteComment operation
        commentService.deleteComment(commentId, username);

        // Verify that commentRepository.findById() was called once
        verify(commentRepository, times(1)).findById(anyLong());

        // Verify that commentRepository.delete() was called once
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

}