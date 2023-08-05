package com.social.app.post.controller;


import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.post.dto.CreateAndUpdatePostDTO;
import com.social.app.post.dto.PostDTO;
import com.social.app.post.dto.PostListDTO;
import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@RestController
@RequestMapping("api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final GeneralService generalService;

    @PostMapping("/create")
    public Response createPost(@Valid @RequestBody CreateAndUpdatePostDTO request, Principal principal) {
        PostDTO data = postService.createPost(request, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/update/{postId}")
    public Response update(@Valid @PathVariable Long postId, @RequestBody CreateAndUpdatePostDTO request, Principal principal) {
        PostDTO data = postService.updatePost(postId, request, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<String> delete(@Valid @PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return new ResponseEntity<>("Successfully deleted post", HttpStatus.OK);
    }

    @GetMapping()
    public Response getAllPost(@RequestBody PageableRequestDTO requestDTO) {
        PostListDTO data = postService.getAllPost(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @GetMapping("/getAll")
    public Response getAllPostForAUser(@RequestBody PageableRequestDTO requestDTO, Principal principal) {
        PostListDTO data = postService.getAllPostForAUser(requestDTO, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/likeAPost/{postId}")
    public Response likeAPost(@Valid @PathVariable Long postId, Principal principal) {
        PostDTO data = postService.likePost(postId, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/search")
    public Response searchAllPosts(@RequestBody PostSearchRequestDTO requestDTO) {
        PostListDTO data = postService.searchTransactions(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

}
