package com.xmartlabs.fountain

import android.annotation.SuppressLint

/**
 * A structure to handle the network states.
 *
 * @param page The requested page number.
 * @param pageSize The requested page size.
 * @param isFirstPage Indicates if the requested page is the first page.
 * @param isLastPage Indicates if the requested page is the last page.
 */
sealed class NetworkState(
    open val page: Int,
    open val pageSize: Int,
    open val isFirstPage: Boolean,
    open val isLastPage: Boolean
) {
  /**
   * Represents that the request is processing.
   *
   * @param page The requested page number.
   * @param pageSize The requested page size.
   * @param isFirstPage Indicates if the requested page is the first page.
   * @param isLastPage Indicates if the requested page is the last page.
   */
  @SuppressLint("SyntheticAccessor")
  data class Loading(
      override val page: Int,
      override val pageSize: Int,
      override val isFirstPage: Boolean,
      override val isLastPage: Boolean
  ) : NetworkState(page = page, pageSize = pageSize, isFirstPage = isFirstPage, isLastPage = isLastPage)

  /**
   * Represents that the request was made successfully.
   *
   * @param page The requested page number.
   * @param pageSize The requested page size.
   * @param isFirstPage Indicates if the requested page is the first page.
   * @param isLastPage Indicates if the requested page is the last page.
   */
  @SuppressLint("SyntheticAccessor")
  data class Loaded(
      override val page: Int,
      override val pageSize: Int,
      override val isFirstPage: Boolean,
      override val isLastPage: Boolean
  ) : NetworkState(page = page, pageSize = pageSize, isFirstPage = isFirstPage, isLastPage = isLastPage)

  /**
   * Represents that the request had an error.
   *
   * @param exception The exception that cosed the error.
   * @param page The requested page number.
   * @param pageSize The requested page size.
   * @param isFirstPage Indicates if the requested page is the first page.
   * @param isLastPage Indicates if the requested page is the last page.
   */
  @SuppressLint("SyntheticAccessor")
  data class Error(
      val exception: Throwable,
      override val page: Int,
      override val pageSize: Int,
      override val isFirstPage: Boolean,
      override val isLastPage: Boolean
  ) : NetworkState(page = page, pageSize = pageSize, isFirstPage = isFirstPage, isLastPage = isLastPage)

  override fun toString(): String {
    return when (this) {
      is Loading -> "Loading[page=$page, pageSize=$pageSize, isFirstPage=$isFirstPage, isLastPage=$isLastPage]"
      is Loaded -> "Success[page=$page, pageSize=$pageSize, isFirstPage=$isFirstPage, isLastPage=$isLastPage]"
      is Error -> "Error[exception=$exception, page=$page, pageSize=$pageSize, isFirstPage=$isFirstPage, " +
          "isLastPage=$isLastPage]]"
    }
  }
}
