package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserMapper
import org.springframework.stereotype.Component

@Component
class UserMapperImpl : UserMapper {

    override fun mapToEntity(input: CreateUserInput) = UserEntity(
        username = input.username,
        password = input.password,
    )

    override fun mapToDTO(entity: UserEntity): User = User(
        id = entity.id ?: throw IllegalArgumentException("Entity must be persisted with assigned id!"),
        username = entity.username
    )
}
