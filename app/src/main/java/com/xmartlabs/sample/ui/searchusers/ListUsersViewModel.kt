package com.xmartlabs.sample.ui.searchusers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.repository.user.UserRepositoryUsingCoroutines
import com.xmartlabs.sample.repository.user.UserRepositoryUsingRetrofit
import com.xmartlabs.sample.repository.user.UserRepositoryUsingRx
import com.xmartlabs.sample.ui.common.FountainAdapterType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListUsersViewModel @Inject constructor(
    private val coroutineUserRepository: UserRepositoryUsingCoroutines,
    private val retrofitUserRepository: UserRepositoryUsingRetrofit,
    private val rxjavaUserRepository: UserRepositoryUsingRx
) : ViewModel() {
  companion object {
    private val PAGED_LIST_CONFIG: PagedList.Config = PagedList.Config.Builder().setPageSize(10).build()
    fun <T> emptyLiveData(): LiveData<T> {
      val emptyLiveData = MutableLiveData<T>()
      emptyLiveData.value = null
      return emptyLiveData
    }
  }

  // It's just to combine the mode with the username, in a real use case it's not needed
  private val usernameModeMediator = MediatorLiveData<Pair<Mode, String>>()

  private val userRepository
    get() = when (adapterType) {
      FountainAdapterType.COROUTINE -> coroutineUserRepository
      FountainAdapterType.RETROFIT -> retrofitUserRepository
      FountainAdapterType.RX -> rxjavaUserRepository
    }

  var adapterType: FountainAdapterType = FountainAdapterType.RETROFIT

  private val userName = MutableLiveData<String>()
  private val mode = MutableLiveData<Mode>()
  private val usersListing = Transformations.map(usernameModeMediator) {
    when {
      it.second.isBlank() -> null // to avoid empty queries
      it.first == Mode.NETWORK -> userRepository.searchServiceUsers(it.second, PAGED_LIST_CONFIG)
      else -> userRepository.searchServiceAndDbUsers(it.second, PAGED_LIST_CONFIG)
    }
  }

  val users: LiveData<PagedList<User>> = Transformations.switchMap(usersListing) {
    it?.pagedList ?: emptyLiveData()
  }
  val networkState: LiveData<NetworkState> = Transformations.switchMap(usersListing) {
    it?.networkState ?: emptyLiveData()
  }
  val refreshState: LiveData<NetworkState> = Transformations.switchMap(usersListing) {
    it?.refreshState ?: emptyLiveData()
  }

  init {
    mode.value = Mode.NETWORK_AND_DATA_SOURCE
    usernameModeMediator.addSource(mode) {
      val userNameValue = userName.value.orEmpty()
      if (it != null && userNameValue.isNotBlank()) {
        usernameModeMediator.value = it to userNameValue
      }
    }
    usernameModeMediator.addSource(userName) { username ->
      mode.value?.let { modeValue ->
        username?.let { usernameModeMediator.value = modeValue to username }
      }
    }
  }

  fun refresh() {
    usersListing.value?.refresh?.invoke()
  }

  fun showUsers(username: String): Boolean {
    if (userName.value == username) {
      return false
    }
    userName.value = username
    return true
  }

  fun changeMode(mode: Mode) {
    if (this.mode.value != mode) {
      this.mode.value = mode
    }
  }

  fun retry() {
    val listing = usersListing?.value
    listing?.retry?.invoke()
  }

  fun currentMode() = this.mode.value

  fun currentUser(): String? = userName.value
}
