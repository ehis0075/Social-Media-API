package com.social.app.comment.dto;

import com.social.app.post.dto.PostDTO;
import com.social.app.post.model.Post;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import lombok.Data;

@Data
public class CommentDTO {

    private Long id;

    private String text;

    private PostDTO post;

    private AppUserDTO applicationUser;
}
