package com.social.app.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.app.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class CommentListDTO extends PageableResponseDTO {

    @JsonProperty("comments")
    private List<CommentDTO> commentDTOList;
}
