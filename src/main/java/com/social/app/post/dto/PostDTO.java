package com.social.app.post.dto;

import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class PostDTO {

    private String content;

    private AppUserDTO userDTO;

    private int likeCount;

    private Date transactionDate;

    private Set<ApplicationUser> usersWhoLiked;
}