package com.social.app.customSearch;


import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.model.Post;
import com.social.app.user.dto.AppUserSearchRequestDTO;
import com.social.app.user.model.ApplicationUser;
import org.springframework.data.domain.Page;

public interface CustomSearchService {
    Page<Post> searchPost(PostSearchRequestDTO searchMultipleDto);

    Page<ApplicationUser> searchAppUsers(AppUserSearchRequestDTO searchMultipleDto, boolean isDownload);

}
