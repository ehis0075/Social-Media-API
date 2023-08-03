package com.social.app.post.service;

import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.general.dto.Response;
import com.social.app.post.dto.CreateAndUpdatePostDTO;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.dto.PostListDTO;
import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.model.Post;
import com.social.app.user.model.ApplicationUser;

public interface PostService {

    Response createPost(CreateAndUpdatePostDTO request, String username);

    Response updatePost(Long postId, CreateAndUpdatePostDTO request, String username);

    void deletePost(Long postId, String username);

    Post getPost(Long postId);

    Response getAllPost(PageableRequestDTO request, String username);

    Response likePost(Long postId, String username);

    PostDTO getPostDTO(Post request, ApplicationUser user);

    PostListDTO searchTransactions(PostSearchRequestDTO requestDTO);
}
