package com.xmartlabs.sample.ui

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.fountain.Status
import com.xmartlabs.sample.R
import com.xmartlabs.sample.model.User
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_list_github_users_activities.*
import javax.inject.Inject

class ListGithubUsersActivities : AppCompatActivity(), HasSupportFragmentInjector {
  companion object {
    const val KEY_USER_NAME = "key.username"
    const val KEY_MODE_NAME = "key.mode"
    const val DEFAULT_USER_NAME = ""
    val DEFAULT_MODE = Mode.NETWORK_AND_DATA_SOURCE
  }

  @Inject
  lateinit var viewModel: ListUsersViewModel
  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector() = dispatchingAndroidInjector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list_github_users_activities)
    initAdapter()
    initSwipeToRefresh()
    initSearch()

    val userName = savedInstanceState?.getString(KEY_USER_NAME) ?: DEFAULT_USER_NAME
    viewModel.showUsers(userName)
    initSwitchMode()
    viewModel.changeMode(savedInstanceState?.getSerializable(KEY_MODE_NAME) as? Mode ?: DEFAULT_MODE)
  }

  private fun initSwitchMode() {
    networkAndDataSourceModeSwitch.setOnCheckedChangeListener { _, isChecked ->
      list.adapter = ListUsersAdapter { viewModel.retry() }
      viewModel.changeMode(if (isChecked) Mode.NETWORK_AND_DATA_SOURCE else Mode.NETWORK)
    }
  }

  private fun initAdapter() {
    list.adapter = ListUsersAdapter { viewModel.retry() }
    viewModel.users.observe(this, Observer<PagedList<User>> {
      (list.adapter as ListUsersAdapter).submitList(it)
    })
    viewModel.networkState.observe(this, Observer {
      (list.adapter as ListUsersAdapter).setNetworkState(it)
    })
  }

  private fun initSwipeToRefresh() {
    viewModel.refreshState.observe(this, Observer {
      swipeRefresh.isRefreshing = it == NetworkState.LOADING
      if (it?.status == Status.FAILED) {
        Toast.makeText(this, "Refresh error", Toast.LENGTH_LONG).show()
      }
    })
    swipeRefresh.setOnRefreshListener {
      viewModel.refresh()
    }
  }

  private fun initSearch() {
    input.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_GO) {
        updatedUsernameFromInput()
        true
      } else {
        false
      }
    }
    input.setOnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        updatedUsernameFromInput()
        true
      } else {
        false
      }
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(KEY_USER_NAME, viewModel.currentUser())
    outState.putSerializable(KEY_MODE_NAME, viewModel.currentMode())
  }

  private fun updatedUsernameFromInput() {
    input.text.trim().toString().let {
      if (it.isNotEmpty()) {
        if (viewModel.showUsers(it)) {
          list.scrollToPosition(0)
          (list.adapter as? ListUsersAdapter)?.submitList(null)
        }
      }
    }
  }
}
