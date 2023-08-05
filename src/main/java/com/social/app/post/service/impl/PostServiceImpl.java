package com.social.app.post.service.impl;

import com.social.app.customSearch.CustomSearchService;
import com.social.app.exception.GeneralException;
import com.social.app.general.dto.PageableRequestDTO;
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
import com.social.app.util.GeneralUtil;
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
    public PostDTO createPost(CreateAndUpdatePostDTO request, String username) {
        log.info("Request to create post with payload = {}", request);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Date transactionDate = new Date();

        Post post = new Post();
        post.setContent(request.getContent());
        post.setUser(user);
        post.setTransactionDate(transactionDate);

        savePost(post);

        return getPostDTO(post, user);
    }

    @Override
    public PostDTO updatePost(Long postId, CreateAndUpdatePostDTO request, String username) {
        log.info("Request to update post with id = {} by user {}", postId, username);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Date transactionDate = new Date();

        Post post = getPost(postId);
        post.setContent(request.getContent());
        post.setUser(user);
        post.setTransactionDate(transactionDate);

        savePost(post);

        return getPostDTO(post, user);
    }

    @Override
    public void deletePost(Long postId, String username) {
        log.info("Request to delete post with id = {} by user {}", postId, username);

        //get post from db
        Post post = getPost(postId);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        ApplicationUser userThatPosted = post.getUser();

        if (!user.getId().equals(userThatPosted.getId())) {
            throw new GeneralException(ResponseCodeAndMessage.RECORD_NOT_FOUND_88.responseCode, "You can only delete a post you created");
        }

        postRepository.delete(post);
        log.info("successfully deleted post");
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
    public PostListDTO getAllPost(PageableRequestDTO request) {
        log.info("Request to get all Post");

        Pageable paged = generalService.getPageableObject(request.getSize(), request.getPage());
        Page<Post> applicationUsers = postRepository.findAll(paged);
        log.info("Post list {}", applicationUsers);

        PostListDTO postListDTO = new PostListDTO();

        List<Post> postList = applicationUsers.getContent();
        if (applicationUsers.getContent().size() > 0) {
            postListDTO.setHasNextRecord(applicationUsers.hasNext());
            postListDTO.setTotalCount((int) applicationUsers.getTotalElements());
        }

        List<PostDTO> postDTOList = convertToPostDTOList(postList);
        postListDTO.setPostList(postDTOList);

        return postListDTO;
    }

    @Override
    public PostListDTO getAllPostForAUser(PageableRequestDTO request, String username) {
        log.info("Request to get all Post for user = {}", username);

        Pageable paged = generalService.getPageableObject(request.getSize(), request.getPage());

        Page<Post> applicationUsers = postRepository.findAllByUser_Username(username, paged);
        log.info("Post list {}", applicationUsers);

        PostListDTO postListDTO = new PostListDTO();

        List<Post> postList = applicationUsers.getContent();
        if (applicationUsers.getContent().size() > 0) {
            postListDTO.setHasNextRecord(applicationUsers.hasNext());
            postListDTO.setTotalCount((int) applicationUsers.getTotalElements());
        }

        List<PostDTO> postDTOList = convertToPostDTOList(postList);
        postListDTO.setPostList(postDTOList);

        return postListDTO;

    }

    @Override
    public PostDTO likePost(Long postId, String username) {
        log.info("Request to like a post with postId {} by user {}", postId, username);

        ApplicationUser user = userService.getUserByUsername(username);

        //get post
        Post post = getPost(postId);

        Set<ApplicationUser> userWhoLikedPosts = post.getUsersWhoLiked();

        if (Objects.nonNull(userWhoLikedPosts) && !userWhoLikedPosts.isEmpty()) {

            // check if user has liked the post before
            if (!hasUserLikedPost(user, userWhoLikedPosts)) {
                // Increment the number of likes on the post
                post.setNumberOfLikes(post.getNumberOfLikes() + 1);

                // add the user that liked the post to the usersWhoLiked List
                userWhoLikedPosts.add(user);

                post.setUsersWhoLiked(userWhoLikedPosts);

                // save post
                post = savePost(post);
            } else {
                log.info("User Already liked this post");
                throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "User Already liked this post!");
            }
        } else {

            // Increment the number of likes on the post
            post.setNumberOfLikes(1);

            // add the user that liked the post to the usersWhoLiked List
            userWhoLikedPosts.add(user);

            post.setUsersWhoLiked(userWhoLikedPosts);

            // save post
            post = savePost(post);
        }

        return getPostDTO(post, user);
    }

    @Override
    public PostDTO getPostDTO(Post request, ApplicationUser user) {
        log.info("Request to get Post DTO");

        AppUserDTO userDTO = userService.getUserDTO(user);

        String trnxDate = GeneralUtil.getDateAsString(request.getTransactionDate());

        PostDTO postDTO = new PostDTO();
        postDTO.setContent(request.getContent());
        postDTO.setUserDTO(userDTO);
        postDTO.setTransactionDate(trnxDate);
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

    public boolean hasUserLikedPost(ApplicationUser user, Set<ApplicationUser> usersWhoLiked) {
        return usersWhoLiked.contains(user);
    }

    private Post savePost(Post post) {
        Post savedPost = postRepository.save(post);
        log.info("successfully saved post to db");

        return savedPost;
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
