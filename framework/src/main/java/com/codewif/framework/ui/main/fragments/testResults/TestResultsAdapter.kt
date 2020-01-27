package com.codewif.framework.ui.main.fragments.testResults

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.ui.base.RecyclerViewItemClickListener

class TestResultsAdapter(
    private val clickListener: RecyclerViewItemClickListener
) : PagedListAdapter<RecyclerViewTestInfo, TestResultsViewHolder>(diffCallback) {

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onBindViewHolder(holder: TestResultsViewHolder, position: Int) {
        val rvTestInfo: RecyclerViewTestInfo? = getItem(position)

        with(holder) {
            bindTo(rvTestInfo)
            rvTestInfo.let {
                if (rvTestInfo != null) {
                    itemView.setOnClickListener {
                        clickListener(rvTestInfo)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestResultsViewHolder =
        TestResultsViewHolder(parent)

    companion object {
        // This diff callback informs the PagedListAdapter how to compute list differences when new  items arrive.
        private val diffCallback = object : DiffUtil.ItemCallback<RecyclerViewTestInfo>() {
            override fun areItemsTheSame(oldItem: RecyclerViewTestInfo, newItem: RecyclerViewTestInfo): Boolean =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RecyclerViewTestInfo, newItem: RecyclerViewTestInfo): Boolean {

                return when {
                    oldItem.testIsRunning && newItem.testIsRunning -> true
                    oldItem.testIsRunning != newItem.testIsRunning -> false
                    else -> oldItem.testSucceeded == newItem.testSucceeded
                }
            }
        }
    }
}