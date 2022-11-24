package com.mukul.jan.reactive.store.lib.example

import kotlinx.coroutines.delay

internal abstract class BaseUsecase

internal data class User(
    val id: Int,
    val name: String,
)

internal class GetUsersUsecase: BaseUsecase() {
    suspend operator fun invoke(): List<User> {
        delay(1000)
        return listOf(User(id = 1, name = "m"), User(id = 2,"k"))
    }
}

internal class DeleteUserUsecase: BaseUsecase() {
    operator fun invoke(id: Int): Int {
        //io or network call
        return id
    }
}