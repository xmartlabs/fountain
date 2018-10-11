package com.xmartlabs.sample.repository.user

import android.arch.paging.PagedList
import com.xmartlabs.fountain.Listing
import com.xmartlabs.sample.model.User

interface UserRepository {
  fun searchServiceUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User>

  fun searchServiceAndDbUsers(userName: String, pagedListConfig: PagedList.Config): Listing<User>
}
