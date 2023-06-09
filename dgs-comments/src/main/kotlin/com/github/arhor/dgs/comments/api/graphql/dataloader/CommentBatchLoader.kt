package com.github.arhor.dgs.comments.api.graphql.dataloader

import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.service.CommentService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Function

sealed class CommentBatchLoader(
    private val executor: Executor,
    private val function: Function<Set<Long>, Map<Long, List<Comment>>>,
) : MappedBatchLoader<Long, List<Comment>> {

    override fun load(keys: Set<Long>): CompletableFuture<Map<Long, List<Comment>>> {
        return CompletableFuture.supplyAsync({ function.apply(keys) }, executor)
    }

    @DgsDataLoader(maxBatchSize = 50)
    class ForUser(asyncExecutor: Executor, commentService: CommentService) : CommentBatchLoader(
        executor = asyncExecutor,
        function = commentService::getCommentsByUserIds
    )

    @DgsDataLoader(maxBatchSize = 50)
    class ForPost(asyncExecutor: Executor, commentService: CommentService) : CommentBatchLoader(
        executor = asyncExecutor,
        function = commentService::getCommentsByPostIds
    )
}
