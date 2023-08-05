package com.social.app.comment.service;

import com.social.app.comment.dto.CommentDTO;
import com.social.app.comment.dto.CommentListDTO;
import com.social.app.comment.dto.CommentListRequestDTO;
import com.social.app.comment.dto.CreateUpdateCommentDTO;
import com.social.app.general.dto.PageableRequestDTO;

public interface CommentService {

    CommentDTO createComment(CreateUpdateCommentDTO request, Long postId, String username);

    CommentDTO updateComment(CreateUpdateCommentDTO request, Long commentId, String username);

    void deleteComment(Long commentId, String username);

    CommentListDTO getCommentList(PageableRequestDTO requestDTO);
}
