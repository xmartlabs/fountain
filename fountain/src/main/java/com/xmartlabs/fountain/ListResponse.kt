package com.xmartlabs.fountain

/** Represent a list service response. */
interface ListResponse<T> {
  /** Returns the response elements. */
  fun getElements(): List<T>
}

/** Represent a list service response that returns the total entity count. */
interface ListResponseWithEntityCount<T> : ListResponse<T> {
  /** Returns the total entity count from the service. */
  fun getEntityCount(): Long
}

/** Represent a list service response that returns the total page count. */
interface ListResponseWithPageCount<T> : ListResponse<T> {
  /** Returns the total page count from the service. */
  fun getPageCount(): Long
}
