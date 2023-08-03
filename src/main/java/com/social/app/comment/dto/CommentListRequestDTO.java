package com.social.app.comment.dto;

import com.social.app.general.dto.PageableRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentListRequestDTO extends PageableRequestDTO {

    private String text;

}