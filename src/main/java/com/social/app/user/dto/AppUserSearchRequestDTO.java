package com.social.app.user.dto;

import com.social.app.general.dto.PageableRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class AppUserSearchRequestDTO extends PageableRequestDTO {

    private String username;

    private String email;

}
