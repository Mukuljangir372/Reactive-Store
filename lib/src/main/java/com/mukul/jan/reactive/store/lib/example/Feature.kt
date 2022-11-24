package com.mukul.jan.reactive.store.lib.example

import androidx.lifecycle.LifecycleOwner
import com.mukul.jan.reactive.store.lib.Event
import com.mukul.jan.reactive.store.lib.Reducer
import com.mukul.jan.reactive.store.lib.State
import com.mukul.jan.reactive.store.lib.Store
import com.mukul.jan.reactive.store.lib.feature.Feature
import com.mukul.jan.reactive.store.lib.feature.LifecycleAwareFeature
import com.mukul.jan.reactive.store.lib.store.LoggerEndConnector
import com.mukul.jan.reactive.store.lib.store.LoggerMiddleware
import com.mukul.jan.reactive.store.lib.store.className
import com.mukul.jan.reactive.store.lib.store.storeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class DeleteUserFeature(
    private val owner: LifecycleOwner? = null,
    private val scope: CoroutineScope,
    private val deleteUsersUsecase: DeleteUserUsecase,
) : Feature<DeleteUserFeature.FeatureState, DeleteUserFeature.FeatureEvent>(
    initialState = FeatureState.idle,
    lifecycleOwner = owner,
    coroutineScope = scope,
    storeKey = storeKey(DeleteUserFeature::class.java),
    reducer = FeatureReducer(),
) {
    sealed class FeatureEvent : Event {
        data class DeleteUser(
            val id: Int,
            val deleted: Boolean,
        ) : FeatureEvent()
    }

    data class FeatureState(
        val loading: Boolean,
        val userDeleted: Boolean,
        val deletedUser: Int
    ) : State {
        companion object {
            val idle = FeatureState(
                loading = false,
                userDeleted = false,
                deletedUser = 0
            )
        }
    }

    class FeatureReducer : Reducer<FeatureState, FeatureEvent> {
        override fun invoke(
            store: Store<FeatureState, FeatureEvent>,
            event: FeatureEvent
        ): FeatureState {
            return when (event) {
                is FeatureEvent.DeleteUser -> {
                    store.state().copy(
                        loading = !event.deleted,
                        deletedUser = event.id,
                        userDeleted = event.deleted
                    )
                }
            }
        }
    }

    operator fun invoke(id: Int) {
        dispatch(
            FeatureEvent.DeleteUser(
                id = id,
                deleted = false
            )
        )
        val deletedUserId = deleteUsersUsecase.invoke(id)
        dispatch(
            FeatureEvent.DeleteUser(
                id = deletedUserId,
                deleted = true
            )
        )
    }
}


internal class GetUsersFeature(
    private val owner: LifecycleOwner? = null,
    private val scope: CoroutineScope,
    private val getUsersUsecase: GetUsersUsecase,
) : Feature<GetUsersFeature.FeatureState, GetUsersFeature.FeatureEvent>(
    initialState = FeatureState.idle,
    lifecycleOwner = owner,
    coroutineScope = scope,
    storeKey = storeKey(GetUsersFeature::class.java),
    reducer = FeatureReducer(),
    middleware = listOf(
        LoggerMiddleware(
            prefix = className(GetUsersFeature::class.java)
        )
    ),
    endConnector = listOf(
        LoggerEndConnector(
            prefix = className(GetUsersFeature::class.java)
        )
    )
), LifecycleAwareFeature {
    sealed class FeatureEvent : Event {
        data class InsertUsers(
            val users: List<User>
        ) : FeatureEvent()

        data class Loading(
            val loading: Boolean
        ) : FeatureEvent()

        object OpenAlertDialog : FeatureEvent()
    }

    data class FeatureState(
        val loading: Boolean,
        val users: List<User>
    ) : State {
        companion object {
            val idle = FeatureState(
                loading = false,
                users = emptyList()
            )
        }
    }

    class FeatureReducer : Reducer<FeatureState, FeatureEvent> {
        override fun invoke(
            store: Store<FeatureState, FeatureEvent>,
            event: FeatureEvent
        ): FeatureState {
            return when (event) {
                is FeatureEvent.InsertUsers -> {
                    store.state().copy(
                        loading = false,
                        users = event.users
                    )
                }
                is FeatureEvent.Loading -> {
                    store.state().copy(
                        loading = event.loading
                    )
                }
                else -> store.state()
            }
        }
    }

    operator fun invoke() = scope.launch {
        for (i in 0..50) {
            delay(1000)
            val state = store().state().loading
            dispatch(FeatureEvent.Loading(loading = !state))
        }
    }

    override fun start() {
        invoke()
    }

    override fun stop() {
        scope.cancel()
        //job?.cancel is recommended
    }
}
