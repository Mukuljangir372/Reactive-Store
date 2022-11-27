# Reactive-Store
Reactive Store library is an easy reactive solution to state management problems in android development.

![alt text](https://github.com/Mukuljangir372/Reactive-Store/blob/master/reactive-store-logo.png)

## Reactive State in 3 Steps
1. Define Reactive Store
```kotlin
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

internal class HomeStore : Store<HomeState, HomeEvent>(
    initialState = HomeState.Idle,
    reducer = HomeReducer(),
)

```
2. Define and Dispatch 
```kotlin
class HomeViewModel() : ViewModel() {
    private val store = getStore(
        default = HomeStore()
    )
    
    fun getUsers() {
       val users = api.getUsers()
       val event = HomeEvent.ChangeUsers(users = users)
       store.dispatch(event)
    }
}    
```
3. Consume State
```kotlin
class HomeActivity: AppCompatActivity() {
     
}
```
## Add Reactive-Store to your project
### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
   repositories {
       ...
       maven { url 'https://jitpack.io' }
   }
}
```
### Step 2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.mukuljangir372:Reactive-Store:v1.4'
}
```
