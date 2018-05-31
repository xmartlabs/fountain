package com.xmartlabs.xlpagingbypagenumber

interface ListResponse<T> {
  fun getElements(): List<T>
}