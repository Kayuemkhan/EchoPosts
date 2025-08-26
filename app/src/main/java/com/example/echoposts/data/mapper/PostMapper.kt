package com.example.echoposts.data.mapper

import com.example.echoposts.data.remote.dto.PostDto

import com.example.echoposts.data.local.entity.Post as PostEntity
import com.example.echoposts.domain.model.Post as DomainPost

fun PostDto.toDomainModel(): DomainPost {
    return DomainPost(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavourite = false
    )
}

fun PostDto.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavourite = false,
        cachedAt = System.currentTimeMillis()
    )
}

fun PostEntity.toDomainModel(): DomainPost {
    return DomainPost(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavourite = this.isFavourite
    )
}

fun DomainPost.toEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        userId = this.userId,
        title = this.title,
        body = this.body,
        isFavourite = this.isFavourite
    )
}