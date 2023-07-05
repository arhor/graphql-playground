package com.github.arhor.dgs.users.api.graphql.datafetcher

import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserResult
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserResult
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserResult
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class UserFetcher(private val userService: UserService) {

    /* Queries */

    @DgsQuery
    fun user(@InputArgument id: Long): User =
        userService.getUserById(id)

    @DgsQuery
    fun users(@InputArgument input: UsersLookupInput): List<User> =
        userService.getAllUsers(input)

    /* Mutations */

    @DgsMutation
    fun createUser(@InputArgument input: CreateUserInput): CreateUserResult =
        CreateUserResult(
            user = userService.createUser(input)
        )

    @DgsMutation
    fun updateUser(@InputArgument input: UpdateUserInput): UpdateUserResult =
        UpdateUserResult(
            user = userService.updateUser(input)
        )

    @DgsMutation
    fun deleteUser(@InputArgument input: DeleteUserInput): DeleteUserResult =
        DeleteUserResult(
            success = userService.deleteUser(input)
        )
}
