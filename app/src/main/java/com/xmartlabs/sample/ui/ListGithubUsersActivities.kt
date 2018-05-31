package com.xmartlabs.sample.ui

import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import com.xmartlabs.sample.R
import com.xmartlabs.template.model.User
import com.xmartlabs.xlpagingbypagenumber.NetworkState
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_list_github_users_activities.*
import javax.inject.Inject

class ListGithubUsersActivities : AppCompatActivity(), HasSupportFragmentInjector {
  @Inject
  lateinit var model: ListUsersViewModel
  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

  override fun supportFragmentInjector() = dispatchingAndroidInjector

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_list_github_users_activities)
    initAdapter()
    initSwipeToRefresh()
    initSearch()
  }

  private fun initAdapter() {
    val adapter = ListUsersAdapter { model.retry() }
    list.adapter = adapter
    model.posts.observe(this, Observer<PagedList<User>> {
      adapter.submitList(it)
    })
    model.networkState.observe(this, Observer {
      adapter.setNetworkState(it)
    })
  }

  private fun initSwipeToRefresh() {
    model.refreshState.observe(this, Observer {
      swipe_refresh.isRefreshing = it == NetworkState.LOADING
    })
    swipe_refresh.setOnRefreshListener {
      model.refresh()
    }
  }

  private fun initSearch() {
    input.setOnEditorActionListener({ _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_GO) {
        updatedUsernameFromInput()
        true
      } else {
        false
      }
    })
    input.setOnKeyListener({ _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        updatedUsernameFromInput()
        true
      } else {
        false
      }
    })
  }

  private fun updatedUsernameFromInput() {
    input.text.trim().toString().let {
      if (it.isNotEmpty()) {
        if (model.showUsers(it)) {
          list.scrollToPosition(0)
          (list.adapter as? ListUsersAdapter)?.submitList(null)
        }
      }
    }
  }
}
