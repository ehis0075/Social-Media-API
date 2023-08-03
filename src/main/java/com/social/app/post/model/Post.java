package com.social.app.post.model;

import com.social.app.comment.model.Comment;
import com.social.app.post.dto.PostDTO;
import com.social.app.user.dto.AppUserDTO;
import com.social.app.user.model.ApplicationUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import reactor.util.annotation.Nullable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate;

    @Nullable
    private int numberOfLikes;

    @OneToMany(mappedBy = "post")
    private List<Comment> comment;

    @ManyToOne
    private ApplicationUser user;

    @OneToMany
    private Set<ApplicationUser> usersWhoLiked;

    public static PostDTO getPostDTO(Post request){

        PostDTO postDTO = new PostDTO();

        BeanUtils.copyProperties(request, postDTO);

        return postDTO;
    }

}
