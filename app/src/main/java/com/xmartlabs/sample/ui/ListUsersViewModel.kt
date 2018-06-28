package com.xmartlabs.sample.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.paging.PagedList
import com.xmartlabs.sample.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListUsersViewModel @Inject constructor(userRepository: UserRepository) : ViewModel() {
  companion object {
    val pagedListConfig = PagedList.Config.Builder().setPageSize(10).build()!!
  }

  private val userName = MutableLiveData<String>()
  var mode = Mode.NETWORK_AND_DATA_SOURCE
    set(mode) {
      if (mode != field) {
        field = mode
        if (!userName.value.isNullOrBlank()) {
          userName.value = userName.value
        }
      }
    }
  private val userResult = Transformations.map(userName) {
    if (mode == Mode.NETWORK)
      userRepository.searchServiceUsers(it, pagedListConfig)
    else
      userRepository.searchServiceAndDbUsers(it, pagedListConfig)
  }
  val users = Transformations.switchMap(userResult) { it.pagedList }!!
  val networkState = Transformations.switchMap(userResult) { it.networkState }!!
  val refreshState = Transformations.switchMap(userResult) { it.refreshState }!!

  fun refresh() {
    userResult.value?.refresh?.invoke()
  }

  fun showUsers(username: String): Boolean {
    if (userName.value == username || username.isBlank()) {
      return false
    }
    userName.value = username
    return true
  }

  fun retry() {
    val listing = userResult?.value
    listing?.retry?.invoke()
  }

  fun currentUser(): String? = userName.value
}
