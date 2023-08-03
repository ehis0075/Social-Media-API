package com.social.app.comment.repository;

import com.social.app.comment.model.Comment;
import com.social.app.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, Long> {

}

