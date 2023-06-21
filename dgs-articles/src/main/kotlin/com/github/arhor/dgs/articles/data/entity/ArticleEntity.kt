package com.github.arhor.dgs.articles.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Immutable
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(ArticleEntity.TABLE_NAME)
@Immutable
data class ArticleEntity(
    @Id
    @Column("id")
    val id: Long? = null,

    @Column("user_id")
    val userId: String?,

    @Column("name")
    val name: String,

    @Column("content")
    val content: String,

    @Version
    @Column("version")
    val version: Long? = null,

    @CreatedDate
    @Column("created_date_time")
    val createdDateTime: LocalDateTime? = null,

    @LastModifiedDate
    @Column("updated_date_time")
    val updatedDateTime: LocalDateTime? = null,
) {
    companion object {
        const val TABLE_NAME = "articles"
    }
}
