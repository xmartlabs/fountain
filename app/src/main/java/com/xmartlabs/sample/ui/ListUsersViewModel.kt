package com.xmartlabs.sample.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.repository.UserRepositoryUsingCoroutines
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListUsersViewModel @Inject constructor(userRepository: UserRepositoryUsingCoroutines) : ViewModel() {
  companion object {
    private val PAGED_LIST_CONFIG: PagedList.Config = PagedList.Config.Builder().setPageSize(10).build()
  }

  // It's just to combine the mode with the username, in a real use case it's not needed
  private val usernameModeMediator = MediatorLiveData<Pair<Mode, String>>()

  private val userName = MutableLiveData<String>()
  private val mode = MutableLiveData<Mode>()
  private val usersListing = Transformations.map(usernameModeMediator) {
    if (it.first == Mode.NETWORK)
      userRepository.searchServiceUsers(it.second, PAGED_LIST_CONFIG)
    else
      userRepository.searchServiceAndDbUsers(it.second, PAGED_LIST_CONFIG)
  }

  val users: LiveData<PagedList<User>> = Transformations.switchMap(usersListing) { it.pagedList }
  val networkState: LiveData<NetworkState> = Transformations.switchMap(usersListing) { it.networkState }
  val refreshState: LiveData<NetworkState> = Transformations.switchMap(usersListing) { it.refreshState }

  init {
    mode.value = Mode.NETWORK_AND_DATA_SOURCE
    usernameModeMediator.addSource(mode) {
      val userNameValue = userName.value.orEmpty()
      if (it != null && userNameValue.isNotBlank()) {
        usernameModeMediator.value = Pair(it, userNameValue)
      }
    }
    usernameModeMediator.addSource(userName) { username ->
      username?.let { usernameModeMediator.value = Pair(mode.value!!, username) }
    }
  }

  fun refresh() {
    usersListing.value?.refresh?.invoke()
  }

  fun showUsers(username: String): Boolean {
    if (userName.value == username || username.isBlank()) {
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
