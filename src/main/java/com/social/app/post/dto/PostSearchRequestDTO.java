package com.social.app.post.dto;

import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.user.model.ApplicationUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
public class PostSearchRequestDTO extends PageableRequestDTO {

    private ApplicationUser user;

    private Date transactionDate;
}
