package com.social.app.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.social.app.general.dto.PageableResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class UserListDTO extends PageableResponseDTO {

    @JsonProperty("users")
    private List<AppUserDTO> userDTOList;
}