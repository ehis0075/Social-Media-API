package com.social.app.post.service.impl;

import com.social.app.customSearch.CustomSearchService;
import com.social.app.exception.GeneralException;
import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.post.dto.CreateAndUpdatePostDTO;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.dto.PostListDTO;
import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.model.Post;
import com.social.app.post.repository.PostRepository;
import com.social.app.post.service.PostService;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    private final CustomSearchService customSearchService;
    private final PostRepository postRepository;
    private final UserService userService;
    private final GeneralService generalService;

    public PostServiceImpl(CustomSearchService customSearchService, PostRepository postRepository, UserService userService, GeneralService generalService) {
        this.customSearchService = customSearchService;
        this.postRepository = postRepository;
        this.userService = userService;
        this.generalService = generalService;
    }

    @Override
    public Response createPost(CreateAndUpdatePostDTO request, String username) {
        log.info("Request to create post with payload = {}", request);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Date transactionDate = new Date();

        Post post = new Post();
        post.setContent(request.getContent());
        post.setUser(user);
        post.setTransactionDate(transactionDate);

        savePost(post);

        PostDTO postDTO = getPostDTO(post, user);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(postDTO);

        return response;
    }

    @Override
    public Response updatePost(Long postId, CreateAndUpdatePostDTO request, String username) {
        log.info("Request to update post with id = {} by user {}", postId, username);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Date transactionDate = new Date();

        Post post = getPost(postId);
        post.setContent(request.getContent());
        post.setUser(user);
        post.setTransactionDate(transactionDate);

        savePost(post);

        PostDTO postDTO = getPostDTO(post, user);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(postDTO);

        return response;
    }

    @Override
    public void deletePost(Long postId, String username) {
        log.info("Request to delete post with id = {} by user {}", postId, username);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Post post = getPost(postId);
        postRepository.delete(post);
    }

    @Override
    public Post getPost(Long postId) {
        log.info("Request to get a post with postId = {}", postId);

        //get user
        Optional<Post> post = postRepository.findById(postId);

        if (post.isEmpty()) {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "Post does not Exist");
        }

        return post.get();
    }


    @Override
    public Response getAllPost(PageableRequestDTO request, String username) {
        log.info("Request to get all Post for user = {}", username);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);
        AppUserDTO userDTO = userService.getUserDTO(user);

        Pageable paged = generalService.getPageableObject(request.getSize(), request.getPage());
        Page<Post> postPage = postRepository.findAll(paged);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(postPage);

        return response;
    }

    @Override
    public Response likePost(Long postId, String username) {
        log.info("Request to like a post with postId {} by user {}", postId, username);

        ApplicationUser user = userService.getUserByUsername(username);

        //get post
        Post post = getPost(postId);

        // Increment the number of likes on the post
        post.setNumberOfLikes(post.getNumberOfLikes() + 1);

        // add the user that liked the post to the usersWhoLiked List
        Set<ApplicationUser> followerList = new HashSet<>();
        followerList.add(user);

        post.setUsersWhoLiked(followerList);

        // save post
        savePost(post);

        PostDTO postDTO = getPostDTO(post, user);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(postDTO);

        return response;
    }


    @Override
    public PostDTO getPostDTO(Post request, ApplicationUser user) {
        log.info("Request to get Post DTO");

        AppUserDTO userDTO = userService.getUserDTO(user);

        PostDTO postDTO = new PostDTO();
        postDTO.setContent(request.getContent());
        postDTO.setUserDTO(userDTO);
        postDTO.setTransactionDate(request.getTransactionDate());
        postDTO.setLikeCount(request.getNumberOfLikes());
        postDTO.setUsersWhoLiked(request.getUsersWhoLiked());

        return postDTO;
    }

    @Override
    public PostListDTO searchTransactions(PostSearchRequestDTO requestDTO) {
        log.info("Searching Post with -> {}", requestDTO);

        Page<Post> transactionPage = customSearchService.searchPost(requestDTO);
        return getPostListDTO(transactionPage);
    }

    private void savePost(Post post) {
        postRepository.save(post);
        log.info("successfully saved post to db");
    }

    private PostListDTO getPostListDTO(Page<Post> postPage) {
        log.info("getting PostListDTO");

        PostListDTO listDTO = new PostListDTO();

        List<Post> posts = postPage.getContent();
        if (postPage.getContent().size() > 0) {
            listDTO.setHasNextRecord(postPage.hasNext());
            listDTO.setTotalCount((int) postPage.getTotalElements());
        }
        List<PostDTO> postDTOS = convertToPostDTOList(posts);
        listDTO.setPostList(postDTOS);

        return listDTO;
    }

    private List<PostDTO> convertToPostDTOList(List<Post> postList) {
        log.info("Converting Post List to Post DTO List");

        return postList.stream().map(Post::getPostDTO).collect(Collectors.toList());
    }

}
