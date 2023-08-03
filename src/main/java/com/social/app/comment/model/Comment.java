package com.social.app.comment.model;

import com.social.app.comment.dto.CommentDTO;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.model.Post;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    private ApplicationUser applicationUser;

//    public static CommentDTO getCommentDTO(Comment comment) {
//        CommentDTO commentDTO = new CommentDTO();
//        BeanUtils.copyProperties(comment, commentDTO);
//
//        Post post = comment.getPost();
//        PostDTO postDTO = getPostDTO(post, post.getUser());
//
//        commentDTO.setPost(postDTO);
//        commentDTO.setText(comment.getText());
//        commentDTO.setApplicationUser(postDTO.getUserDTO());
//
//        return commentDTO;
//    }
//
//    public static PostDTO getPostDTO(Post request, ApplicationUser user) {
//
//        AppUserDTO userDTO = getUserDTO(user);
//
//        PostDTO postDTO = new PostDTO();
//        postDTO.setContent(request.getContent());
//        postDTO.setUserDTO(userDTO);
//        postDTO.setTransactionDate(request.getTransactionDate());
//        postDTO.setLikeCount(request.getNumberOfLikes());
//        postDTO.setUsersWhoLiked(request.getUsersWhoLiked());
//
//        return postDTO;
//    }
//
//    public static AppUserDTO getUserDTO(ApplicationUser request) {
//        AppUserDTO appUserDTO = new AppUserDTO();
//        appUserDTO.setUsername(request.getUsername());
//        appUserDTO.setEmail(request.getEmail());
//        appUserDTO.setRole(UserRole.ROLE_USER);
//        appUserDTO.setNumberOfFollowers(request.getNumberOfFollowers());
//
//        return appUserDTO;
//    }

}
