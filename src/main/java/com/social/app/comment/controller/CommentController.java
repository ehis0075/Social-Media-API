package com.social.app.comment.controller;


import com.social.app.comment.dto.CommentDTO;
import com.social.app.comment.dto.CommentListDTO;
import com.social.app.comment.dto.CommentListRequestDTO;
import com.social.app.comment.dto.CreateUpdateCommentDTO;
import com.social.app.comment.service.CommentService;
import com.social.app.general.dto.PageableRequestDTO;
import com.social.app.general.dto.Response;
import com.social.app.general.enums.ResponseCodeAndMessage;
import com.social.app.general.service.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@RestController
@RequestMapping("api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final GeneralService generalService;

    @PostMapping("/create/{postId}")
    public Response createComments(@Valid @RequestBody CreateUpdateCommentDTO request, @PathVariable Long postId, Principal principal) {
        CommentDTO data = commentService.createComment(request, postId, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/update/{commentId}")
    public Response updateComments(@Valid @RequestBody CreateUpdateCommentDTO request, @PathVariable Long commentId, Principal principal) {
        CommentDTO data = commentService.updateComment(request, commentId, principal.getName());
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    @PostMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteComments(@PathVariable Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return new ResponseEntity<>("Successfully deleted comment", HttpStatus.CREATED);
    }

    @PostMapping()
    public Response getAllComments(@RequestBody PageableRequestDTO requestDTO) {
        CommentListDTO data = commentService.getCommentList(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

    //TODO get all comments on a post for a particular user

}
