package com.avengers.nibobnebob.presentation.ui.main.mypage.wishlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avengers.nibobnebob.presentation.ui.main.mypage.model.UiMyWishData
import com.avengers.presentation.R
import com.avengers.presentation.databinding.ItemWishListBinding

class MyWishAdapter(
    private val deleteMyWish: (Int) -> Unit,
    private val showDetail: (Int) -> Unit,
    private val addItem: (UiMyWishData) -> Unit
) : ListAdapter<UiMyWishData, MyWishViewHolder>(diffCallback) {
    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<UiMyWishData>() {
            override fun areItemsTheSame(
                oldItem: UiMyWishData,
                newItem: UiMyWishData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UiMyWishData,
                newItem: UiMyWishData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWishViewHolder {
        return MyWishViewHolder(
            ItemWishListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyWishViewHolder, position: Int) {
        holder.bind(getItem(position), deleteMyWish, showDetail, addItem)
    }

}

class MyWishViewHolder(
    private val binding: ItemWishListBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: UiMyWishData,
        deleteMyWish: (Int) -> Unit,
        showDetail: (Int) -> Unit,
        addItem: (UiMyWishData) -> Unit
    ) = with(binding) {
        this.item = item
        ivMore.setOnClickListener { listMenu(item, showDetail, addItem) }
        ivStar.setOnClickListener { deleteMyWish(item.id) }
        executePendingBindings()

    }

    private fun listMenu(
        item: UiMyWishData,
        showDetail: (Int) -> Unit,
        addItem: (UiMyWishData) -> Unit
    ) {
        PopupMenu(binding.root.context, binding.ivMore).apply {
            menuInflater.inflate(R.menu.my_page_wish_list_menu, menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_detail -> showDetail(item.id)
                    R.id.menu_add -> addItem(item)
                }
                true
            }
            show()
        }

    }
}