package com.social.app.post.controller;


import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import com.social.app.post.dto.CreateAndUpdatePostDTO;
import com.social.app.post.dto.PostListDTO;
import com.social.app.post.dto.PostSearchRequestDTO;
import com.social.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<Response> createPost(@Valid @RequestBody CreateAndUpdatePostDTO request, Principal principal) {
        Response data = postService.createPost(request, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/{postId}")
    public ResponseEntity<Response> update(@Valid @PathVariable Long postId, @RequestBody CreateAndUpdatePostDTO request, Principal principal) {
        Response data = postService.updatePost(postId, request, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<Response> delete(@Valid @PathVariable Long postId, Principal principal) {
        postService.deletePost(postId, principal.getName());
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Response> getAllPost(@RequestBody PageableRequestDTO requestDTO, Principal principal) {
        Response data = postService.getAllPost(requestDTO, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/likeAPost/{postId}")
    public ResponseEntity<Response> likeAPost(@Valid @PathVariable Long postId, Principal principal) {
        Response data = postService.likePost(postId, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    
    @PostMapping("/search")
    public Response searchAllPosts(@RequestBody PostSearchRequestDTO requestDTO, Principal principal, @RequestParam(required = false, defaultValue = "false") boolean download, @RequestParam(required = false, defaultValue = "false") boolean sendToMail) {
        PostListDTO data = postService.searchTransactions(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

}
