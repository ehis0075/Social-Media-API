package com.social.app.util;

import com.social.app.comment.model.Comment;
import com.social.app.post.model.Post;
import com.social.app.user.model.ApplicationUser;
import com.social.app.user.model.UserRole;

public class GeneralTestUtil {

    public static final String USERNAME = "jay";
    public static final String EMAIL = "jeremiah";
    public static final String IMAGE_URL = "jeremiah";
    public static final String PASSWORD = "jeremiah";
    public static final UserRole ROLE_USER = UserRole.ROLE_USER;

    public static Post getPost(Post post) {
        Post newPost = new Post();
        newPost.setContent(post.getContent());
        newPost.setUser(post.getUser());
        newPost.setComment(post.getComment());
        newPost.setUsersWhoLiked(post.getUsersWhoLiked());
        newPost.setTransactionDate(post.getTransactionDate());
        newPost.setNumberOfLikes(post.getNumberOfLikes());
        return newPost;
    }

    public static Comment getComment(Comment comment) {
        Comment newComment = new Comment();
        newComment.setText(comment.getText());
        newComment.setPost(comment.getPost());
        newComment.setApplicationUser(getUser());

        return newComment;
    }

    public static ApplicationUser getUser() {
        ApplicationUser user = new ApplicationUser();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);
        user.setUserRole(ROLE_USER);
        return user;
    }

}
