package com.xmartlabs.xlpagingbypagenumber

interface ListResponse<T> {
  fun getElements(): List<T>
}

interface ListResponseWithEntityCount<T> : ListResponse<T> {
  fun getEntityCount() : Long
}

interface ListResponseWithPageCount<T> : ListResponse<T> {
  fun getPageCount(): Long
}
