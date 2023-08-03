package com.social.app.comment.service.impl;

import com.social.app.comment.dto.CommentDTO;
import com.social.app.comment.dto.CommentListDTO;
import com.social.app.comment.dto.CommentListRequestDTO;
import com.social.app.comment.dto.CreateUpdateCommentDTO;
import com.social.app.comment.model.Comment;
import com.social.app.comment.repository.CommentRepository;
import com.social.app.comment.service.CommentService;
import com.social.app.exception.GeneralException;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.model.Post;
import com.social.app.post.service.PostService;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import com.social.app.user.service.UserService;
import com.social.app.util.GeneralUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final UserService userService;
    private final PostService postService;
    private final GeneralService generalService;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(UserService userService, PostService postService, GeneralService generalService, CommentRepository commentRepository) {
        this.userService = userService;
        this.postService = postService;
        this.generalService = generalService;
        this.commentRepository = commentRepository;
    }


    @Override
    public Response createComment(CreateUpdateCommentDTO request, Long postId, String username) {
        log.info("Request to create a comment for post with ID {} by user {}", postId, username);


        // check that text is not null or empty
        if (GeneralUtil.stringIsNullOrEmpty(request.getText())) {
            throw new GeneralException(ResponseCodeAndMessage.INCOMPLETE_PARAMETERS_91.responseCode, "Text cannot be null or empty!");
        }

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Post post = postService.getPost(postId);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setPost(post);
        comment.setApplicationUser(user);

        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        post.setComment(commentList);

        commentRepository.save(comment);
        log.info("successfully saved comment to db");

        PostDTO postDTO = postService.getPostDTO(post, user);

        AppUserDTO appUserDTO = userService.getUserDTO(user);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(request.getText());
        commentDTO.setPost(postDTO);
        commentDTO.setApplicationUser(appUserDTO);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(commentDTO);

        return response;
    }

    @Override
    public Response updateComment(CreateUpdateCommentDTO request, Long commentId, String username) {
        log.info("Request to update a comment with ID {} by user {}", commentId, username);

        //get user
        ApplicationUser user = userService.getUserByUsername(username);

        Comment comment = commentRepository.findById(commentId).get();

        comment.setText(request.getText());

        Post post = postService.getPost(comment.getPost().getId());

        List<Comment> commentList = new ArrayList<>();
        commentList.add(comment);
        post.setComment(commentList);

        commentRepository.save(comment);
        log.info("successfully saved comment to db");

        PostDTO postDTO = postService.getPostDTO(post, user);

        AppUserDTO appUserDTO = userService.getUserDTO(user);

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setText(request.getText());
        commentDTO.setPost(postDTO);
        commentDTO.setApplicationUser(appUserDTO);

        Response response = new Response();
        response.setResponseCode(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode);
        response.setResponseMessage(ResponseCodeAndMessage.SUCCESSFUL_0.responseMessage);
        response.setData(commentDTO);

        return response;
    }

    @Override
    public void deleteComment(Long commentId, String username) {
        log.info("Request to delete a comment with ID {} by user {}", commentId, username);

        Optional<Comment> comment = commentRepository.findById(commentId);

        comment.ifPresent(commentRepository::delete);

    }

    @Override
    public CommentListDTO getCommentList(CommentListRequestDTO requestDTO) {
        log.info("Getting Comment List ");

        Pageable paged = generalService.getPageableObject(requestDTO.getSize(), requestDTO.getPage());
        Page<Comment> commentsPage = commentRepository.findAll(paged);

        return getCommentListDTO(commentsPage);
    }

    private CommentListDTO getCommentListDTO(Page<Comment> commentPage) {
        CommentListDTO listDTO = new CommentListDTO();

        List<Comment> commentList = commentPage.getContent();
        if (!commentPage.getContent().isEmpty()) {
            listDTO.setHasNextRecord(commentPage.hasNext());
            listDTO.setTotalCount((int) commentPage.getTotalElements());
        }

        List<CommentDTO> commentDTOS = convertToCommentDTOList(commentList);
        listDTO.setCommentDTOList(commentDTOS);

        return listDTO;
    }

//    private List<CommentDTO> convertToCommentDTOList(List<Comment> commentList) {
//        log.info("Converting Comment List to Comment DTO List");
//
//        return commentList.stream().map(Comment::getCommentDTO).collect(Collectors.toList());
//    }

    private List<CommentDTO> convertToCommentDTOList(List<Comment> commentList) {
        log.info("Converting Comment List to Comment DTO List");

        return commentList.stream().map(CommentServiceImpl::getCommentDTO).collect(Collectors.toList());
    }


    public static CommentDTO getCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);

        Post post = comment.getPost();
        PostDTO postDTO = getPostDTO(post, post.getUser());

        commentDTO.setPost(postDTO);
        commentDTO.setText(comment.getText());
        commentDTO.setApplicationUser(postDTO.getUserDTO());

        return commentDTO;
    }

    public static PostDTO getPostDTO(Post request, ApplicationUser user) {

        AppUserDTO userDTO = getUserDTO(user);

        PostDTO postDTO = new PostDTO();
        postDTO.setContent(request.getContent());
        postDTO.setUserDTO(userDTO);
        postDTO.setTransactionDate(request.getTransactionDate());
        postDTO.setLikeCount(request.getNumberOfLikes());
        postDTO.setUsersWhoLiked(request.getUsersWhoLiked());

        return postDTO;
    }

    public static AppUserDTO getUserDTO(ApplicationUser request) {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setUsername(request.getUsername());
        appUserDTO.setEmail(request.getEmail());
        appUserDTO.setRole(UserRole.ROLE_USER);
        appUserDTO.setNumberOfFollowers(request.getNumberOfFollowers());

        return appUserDTO;
    }


}
