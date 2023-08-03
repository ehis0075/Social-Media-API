package com.social.app.comment.controller;


import com.social.app.comment.dto.CommentListDTO;
import com.social.app.comment.dto.CommentListRequestDTO;
import com.social.app.comment.dto.CreateUpdateCommentDTO;
import com.social.app.comment.service.CommentService;
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
    public ResponseEntity<Response> createComments(@Valid @RequestBody CreateUpdateCommentDTO request, @PathVariable Long postId, Principal principal) {
        Response data = commentService.createComment(request, postId, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update/{commentId}")
    public ResponseEntity<Response> updateComments(@Valid @RequestBody CreateUpdateCommentDTO request, @PathVariable Long commentId, Principal principal) {
        Response data = commentService.updateComment(request, commentId, principal.getName());
        if (data.getResponseCode().equals(ResponseCodeAndMessage.SUCCESSFUL_0.responseCode))
            return new ResponseEntity<>(data, HttpStatus.CREATED);
        else return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/delete/{commentId}")
    public ResponseEntity<Response> deleteComments(@PathVariable Long commentId, Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping()
    public Response getAllComments(CommentListRequestDTO requestDTO) {
        CommentListDTO data = commentService.getCommentList(requestDTO);
        return generalService.prepareResponse(ResponseCodeAndMessage.SUCCESSFUL_0, data);
    }

}
