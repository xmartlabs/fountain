package com.xmartlabs.sample.ui.searchusers

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.xmartlabs.fountain.NetworkState
import com.xmartlabs.sample.R
import com.xmartlabs.sample.model.User
import com.xmartlabs.sample.ui.common.FountainAdapterType
import com.xmartlabs.sample.ui.common.onSearchPerformed
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_list_github_users_activities.*
import javax.inject.Inject

class ListGithubUsersActivity : AppCompatActivity(), HasSupportFragmentInjector {
  companion object {
    const val KEY_ADAPTER_TYPE_NAME = "key.adapterType"
    private const val KEY_USER_NAME = "key.username"
    private const val KEY_MODE_NAME = "key.mode"
    private const val DEFAULT_USER_NAME = ""
    private val DEFAULT_MODE = Mode.NETWORK_AND_DATA_SOURCE
  }

  @Inject
  lateinit var viewModel: ListUsersViewModel
  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

  private val adapterType: FountainAdapterType
    get() =
      intent.extras.getSerializable(KEY_ADAPTER_TYPE_NAME) as? FountainAdapterType ?: FountainAdapterType.RETROFIT

  override fun supportFragmentInjector() = dispatchingAndroidInjector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_list_github_users_activities)

    viewModel.adapterType = adapterType
    val userName = savedInstanceState?.getString(KEY_USER_NAME) ?: DEFAULT_USER_NAME
    viewModel.showUsers(userName)
    initSwitchMode()
    viewModel.changeMode(savedInstanceState?.getSerializable(KEY_MODE_NAME) as? Mode ?: DEFAULT_MODE)

    initAdapter()
    initSwipeToRefresh()
    initSearch()
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
      swipeRefresh.isRefreshing = it is NetworkState.Loading
      if (it is NetworkState.Error) {
        Toast.makeText(this, "Refresh error", Toast.LENGTH_LONG).show()
      }
    })
    swipeRefresh.setOnRefreshListener {
      viewModel.refresh()
    }
  }

  private fun initSearch() {
    input.onSearchPerformed {
      updatedUsernameFromInput()
      hideSoftKeyboard()
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

  private fun Activity.hideSoftKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
  }
}
