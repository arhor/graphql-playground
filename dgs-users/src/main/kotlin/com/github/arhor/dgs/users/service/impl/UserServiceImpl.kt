package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.lib.event.UserEvent
import com.github.arhor.dgs.lib.exception.EntityDuplicateException
import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserResult
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserResult
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserResult
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.dgs.users.service.events.UserEventEmitter
import com.github.arhor.dgs.users.service.mapping.UserMapper
import com.github.arhor.dgs.users.service.UserService
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val userEventEmitter: UserEventEmitter,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    @Transactional(readOnly = true)
    override fun getUserById(id: Long): User {
        return userRepository.findByIdOrNull(id)?.let { userMapper.mapToResult(it) }
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $id",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(input: UsersLookupInput): List<User> {
        return userRepository
            .findAll(PageRequest.of(input.page, input.size))
            .map(userMapper::mapToResult)
            .toList()
    }

    @Transactional
    override fun createUser(input: CreateUserInput): CreateUserResult {
        if (userRepository.existsByUsername(input.username)) {
            throw EntityDuplicateException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Username} = ${input.username}",
                operation = Operation.CREATE,
            )
        }
        val user = input.copy(password = passwordEncoder.encode(input.password))
            .let { userMapper.mapToEntity(it) }
            .let { userRepository.save(it) }
            .let { userMapper.mapToResult(it) }

        return CreateUserResult(user)
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateUser(input: UpdateUserInput): UpdateUserResult {
        val initialState = userRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = USER.TYPE_NAME,
            condition = "${USER.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        val currentState = initialState.copy(
            password = input.password?.let(passwordEncoder::encode) ?: initialState.password
        )
        val user = userMapper.mapToResult(
            entity = when (currentState != initialState) {
                true -> userRepository.save(currentState)
                else -> initialState
            }
        )
        return UpdateUserResult(user)
    }

    @Transactional
    override fun deleteUser(input: DeleteUserInput): DeleteUserResult {
        return DeleteUserResult(
            success = when (val user = userRepository.findByIdOrNull(input.id)) {
                null -> false
                else -> {
                    userRepository.delete(user)
                    userEventEmitter.emit(UserEvent.Deleted(id = user.id!!))
                    true
                }
            }
        )
    }
}
