package com.codewif.framework.ui.main.fragments.tests

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.codewif.framework.R
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.ui.base.RecyclerViewItemClickListener
import kotlinx.android.synthetic.main.cw_tests_list_item.view.*

class TestsAdapter(
    private val clickListener: RecyclerViewItemClickListener,
    private val onSkipTestChangeListener: (rvTestInfo: RecyclerViewTestInfo) -> Unit
) : PagedListAdapter<RecyclerViewTestInfo, TestsViewHolder>(diffCallback) {

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onBindViewHolder(holder: TestsViewHolder, position: Int) {
        val rvTestInfo: RecyclerViewTestInfo? = getItem(position)

        with(holder) {
            bindTo(rvTestInfo)
            rvTestInfo.let {
                if (rvTestInfo != null) {
                    itemView.findViewById<ConstraintLayout>(R.id.constraint_layout_list_item).setOnClickListener {
                        clickListener(rvTestInfo)
                    }
                    itemView.switch_enabled.setOnClickListener {
                        rvTestInfo.skipTest = !(it as Switch).isChecked
                        onSkipTestChangeListener(rvTestInfo)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestsViewHolder =
        TestsViewHolder(parent)

    companion object {
        // This diff callback informs the PagedListAdapter how to compute list differences when new  items arrive.
        private val diffCallback = object : DiffUtil.ItemCallback<RecyclerViewTestInfo>() {
            override fun areItemsTheSame(oldItem: RecyclerViewTestInfo, newItem: RecyclerViewTestInfo): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RecyclerViewTestInfo, newItem: RecyclerViewTestInfo): Boolean =
                oldItem == newItem
        }
    }
}