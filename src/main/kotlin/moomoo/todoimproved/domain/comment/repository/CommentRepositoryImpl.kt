package moomoo.todoimproved.domain.comment.repository

import moomoo.todoimproved.infra.querydsl.QueryDslSupport
import org.springframework.stereotype.Repository

@Repository
class CommentRepositoryImpl : CustomCommentRepository, QueryDslSupport()