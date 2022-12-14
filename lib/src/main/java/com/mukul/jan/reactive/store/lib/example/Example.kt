package com.mukul.jan.reactive.store.lib.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mukul.jan.reactive.store.lib.Event
import com.mukul.jan.reactive.store.lib.Reducer
import com.mukul.jan.reactive.store.lib.State
import com.mukul.jan.reactive.store.lib.Store
import com.mukul.jan.reactive.store.lib.store.*

internal interface BaseHomeInteractor {
    fun deleteUserClicked()
    fun fetchUsersClicked()
}

internal class HomeInteractor(
    private val viewModel: HomeViewModel
) : BaseHomeInteractor {
    override fun deleteUserClicked() {
        viewModel.deleteUser(id = 1)
    }

    override fun fetchUsersClicked() {
        viewModel.insertUser()
    }
}

internal class HomeStore : Store<HomeState, HomeEvent>(
    initialState = HomeState.Idle,
    reducer = HomeReducer(),
    middleware = listOf(
        LoggerMiddleware(
            prefix = className(HomeState::class.java)
        )
    ),
    endConnector = listOf(
        LoggerEndConnector(
            prefix = className(HomeState::class.java)
        )
    )
)

internal interface HomeEvent : Event {
    data class InsertUsers(val users: List<User>) : HomeEvent
    data class RemoveUser(val id: Int) : HomeEvent
    data class ChangeName(val name: String) : HomeEvent
}

internal data class HomeState(
    val isLoading: Boolean,
    val users: List<User>
) : State {
    companion object {
        val Idle = HomeState(
            isLoading = false,
            users = listOf()
        )
    }
}

internal class HomeReducer : Reducer<HomeState, HomeEvent> {
    override fun invoke(store: Store<HomeState, HomeEvent>, event: HomeEvent): HomeState {
        return when (event) {
            is HomeEvent.InsertUsers -> {
                val users = store.state().users
                store.state().copy(users = users + event.users)
            }
            is HomeEvent.RemoveUser -> {
                val users = store.state().users.toMutableList()
                users.removeIf { it.id == event.id }
                store.state().copy(users = users)
            }
            is HomeEvent.ChangeName -> {
                store.state().copy(
                    users = listOf(
                        User(
                            id = 1,
                            name = event.name
                        )
                    )
                )
            }
            else -> store.state()
        }
    }
}

internal class HomeViewModel() : ViewModel() {

    private val store = getStore(
        key = storeKey(HomeStore::class.java),
        default = HomeStore()
    )

    fun insertUser() {
        GetUsersFeature(
            scope = viewModelScope,
            getUsersUsecase = GetUsersUsecase()
        ).consumeState {
            if (!it.loading && it.users.isNotEmpty()) {
                store.dispatch(HomeEvent.InsertUsers(users = it.users))
            }
        }.invoke()
    }

    fun deleteUser(id: Int) {
        DeleteUserFeature(
            scope = viewModelScope,
            deleteUsersUsecase = DeleteUserUsecase()
        ).consumeState {
            if (it.userDeleted) {
                store.dispatch(HomeEvent.RemoveUser(id = it.deletedUser))
            }
        }.invoke(id = 1)
    }


}














