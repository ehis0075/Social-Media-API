package com.social.app.post.service.impl;

import com.social.app.exception.GeneralException;
import com.social.app.post.dto.CreateAndUpdatePostDTO;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.model.Post;
import com.social.app.post.repository.PostRepository;
import com.social.app.post.service.PostService;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.repository.UserRepository;
import com.social.app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceImplTest {

    @Autowired
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Autowired
    private UserService userService;

    @Test
    public void testCreatePost_Success() {
        // Create sample input data
        String username = "ola";
        CreateAndUpdatePostDTO request = new CreateAndUpdatePostDTO();
        request.setContent("Test content");

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        Post post = new Post();
        post.setContent(request.getContent());
        post.setUser(user);

        // Mock the behavior of userService.getUserByUsername()
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Mock the behavior of postRepository.save()
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Perform the createPost operation
        PostDTO result = postService.createPost(request, username);

        // Verify that userService.getUserByUsername() was called once
        verify(userService, times(1)).getUserByUsername(anyString());

        // Verify that postRepository.save() was called once
        verify(postRepository, times(1)).save(any(Post.class));

        // Verify the returned result
        assertNotNull(result);
        assertEquals(request.getContent(), result.getContent());
        assertEquals(username, result.getUserDTO().getUsername());
    }

    @Test
    public void testUpdatePost_Success() {
        // Create sample input data
        Long postId = 1L;
        String username = "ola";
        CreateAndUpdatePostDTO request = new CreateAndUpdatePostDTO();
        request.setContent("Updated content");

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        Post post = new Post();
        post.setId(postId);
        post.setContent(request.getContent());
        post.setUser(user);

        // Mock the behavior of userService.getUserByUsername()
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Mock the behavior of postRepository.findById() and postRepository.save()
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // Perform the updatePost operation
        PostDTO result = postService.updatePost(postId, request, username);

        // Verify that userService.getUserByUsername() was called once
        verify(userService, times(1)).getUserByUsername(anyString());

        // Verify that postRepository.findById() and postRepository.save() were called once each
        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(Post.class));

        // Verify the returned result
        assertNotNull(result);
        assertEquals(request.getContent(), result.getContent());
        assertEquals(username, result.getUserDTO().getUsername());
    }

    @Test
    public void testDeletePost_UnauthorizedUser_ThrowsException() {
        // Create sample input data
        Long postId = 1L;
        String username = "ola";
        String postUsername = "dami";

        ApplicationUser user = new ApplicationUser();
        user.setUsername(username);

        ApplicationUser postUser = new ApplicationUser();
        postUser.setUsername(postUsername);

        Post post = new Post();
        post.setId(postId);
        post.setUser(postUser);

        // Mock the behavior of userService.getUserByUsername()
        when(userService.getUserByUsername(username)).thenReturn(user);

        // Mock the behavior of postRepository.findById()
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Perform the deletePost operation, expecting an exception
        postService.deletePost(postId, username);

        // Verify that userService.getUserByUsername() and postRepository.findById() were called once each
        verify(userService, times(1)).getUserByUsername(anyString());
        verify(postRepository, times(1)).findById(anyLong());
    }
}