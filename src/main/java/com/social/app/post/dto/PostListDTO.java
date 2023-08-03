package com.social.app.post.dto;

import com.social.app.general.dto.PageableResponseDTO;
import com.social.app.post.model.Post;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class PostListDTO extends PageableResponseDTO {

    private List<PostDTO> postList;
}
