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
    val pagedListConfig = PagedList.Config.Builder().setPageSize(10).build()
  }

  private val userName = MutableLiveData<String>()
  var mode = Mode.NETWORK_AND_DATA_SOURCE
    set(mode) {
      if (mode != field) {
        field = mode
        if (!userName.value.isNullOrBlank()){
          userName.value = userName.value
        }
      }
    }
  private val repoResult = Transformations.map(userName) {
    if (mode == Mode.NETWORK)
      userRepository.searchServiceUsers(it, pagedListConfig)
    else
      userRepository.searchServiceAndDbUsers(it, pagedListConfig)
  }
  val posts = Transformations.switchMap(repoResult) { it.pagedList }!!
  val networkState = Transformations.switchMap(repoResult) { it.networkState }!!
  val refreshState = Transformations.switchMap(repoResult) { it.refreshState }!!

  fun refresh() {
    repoResult.value?.refresh?.invoke()
  }

  fun showUsers(username: String): Boolean {
    if (userName.value == username) {
      return false
    }
    if (!username.isBlank()){
      userName.value = username
      return true
    }
    return false
  }

  fun retry() {
    val listing = repoResult?.value
    listing?.retry?.invoke()
  }

  private fun changeMode(mode: Mode) {

  }

  fun currentUser(): String? = userName.value
}
