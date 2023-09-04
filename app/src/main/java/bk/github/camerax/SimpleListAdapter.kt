package bk.github.camerax

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SimpleListAdapter<T : Any>(
    private val itemId: Int,
    private val onBind: (holder: PermissionViewHolder, item: T) -> Unit
) : ListAdapter<T, SimpleListAdapter.PermissionViewHolder>(DiffUtilItemCallback<T>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PermissionViewHolder(inflater.inflate(itemId, null))
    }

    override fun onBindViewHolder(holder: PermissionViewHolder, position: Int) {
        onBind(holder, getItem(position))
    }

    class PermissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private class DiffUtilItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem === newItem

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }
}