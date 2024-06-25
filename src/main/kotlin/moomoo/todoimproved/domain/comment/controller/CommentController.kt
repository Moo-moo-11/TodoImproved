package moomoo.todoimproved.domain.comment.controller

import moomoo.todoimproved.domain.comment.dto.CommentResponse
import moomoo.todoimproved.domain.comment.dto.CreateCommentRequest
import moomoo.todoimproved.domain.comment.dto.UpdateCommentRequest
import moomoo.todoimproved.domain.comment.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("todos/{todoId}/comments")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/{commentId}")
    fun getComment(@PathVariable todoId: Long, @PathVariable commentId: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity
            .ok(commentService.getCommentList(todoId, commentId))
    }

    @PostMapping
    fun createComment(
        @PathVariable todoId: Long,
        @RequestBody request: CreateCommentRequest
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(commentService.createComment(todoId, request))
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable todoId: Long, @PathVariable commentId: Long,
        @RequestBody request: UpdateCommentRequest
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity
            .ok(commentService.updateComment(todoId, commentId, request))
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable todoId: Long, @PathVariable commentId: Long): ResponseEntity<Unit> {
        commentService.deleteComment(todoId, commentId)
        return ResponseEntity.ok().build()
    }

}