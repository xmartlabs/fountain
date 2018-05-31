package com.xmartlabs.sample.ui

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xmartlabs.sample.R
import com.xmartlabs.template.model.User
import com.xmartlabs.xlpagingbypagenumber.NetworkState

class ListUsersAdapter(private val retryCallback: () -> Unit)
  : PagedListAdapter<User, RecyclerView.ViewHolder>(USER_COMPARATOR) {
  private var networkState: NetworkState? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      R.layout.item_user -> UserViewHolder.create(parent)
      R.layout.item_network_state -> NetworkStateItemViewHolder.create(parent, retryCallback)
      else -> throw IllegalArgumentException("unknown view type $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (getItemViewType(position)) {
      R.layout.item_user -> (holder as UserViewHolder).bindTo(getItem(position))
      R.layout.item_network_state -> (holder as NetworkStateItemViewHolder).bindTo(networkState)
    }
  }

  private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

  override fun getItemViewType(position: Int): Int {
    return if (hasExtraRow() && position == itemCount - 1) {
      R.layout.item_network_state
    } else {
      R.layout.item_user
    }
  }

  override fun getItemCount(): Int {
    return super.getItemCount() + if (hasExtraRow()) 1 else 0
  }

  fun setNetworkState(newNetworkState: NetworkState?) {
    val previousState = this.networkState
    val hadExtraRow = hasExtraRow()
    this.networkState = newNetworkState
    val hasExtraRow = hasExtraRow()
    if (hadExtraRow != hasExtraRow) {
      if (hadExtraRow) {
        notifyItemRemoved(super.getItemCount())
      } else {
        notifyItemInserted(super.getItemCount())
      }
    } else if (hasExtraRow && previousState != newNetworkState) {
      notifyItemChanged(itemCount - 1)
    }
  }

  companion object {
    val USER_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
      override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
          oldItem == newItem

      override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
          oldItem.name == newItem.name
    }
  }

  class UserViewHolder(val view: View)
    : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.name)
    private val image: ImageView = view.findViewById(R.id.image)

    companion object {
      fun create(parent: ViewGroup): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
      }
    }

    fun bindTo(user: User?) {
      name.text = user?.name
      user?.avatarUrl.let { url ->
        Picasso.get()
            .load(url)
            .into(image)
      }
    }
  }
}
