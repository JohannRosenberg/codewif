package com.codewif.framework.ui.main.fragments.testResults

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewif.framework.R
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.shared.App.Companion.ctx

class TestResultsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.cw_test_results_list_item, parent, false)
) {
    private val tvTestName = itemView.findViewById<TextView>(R.id.tv_test_name)
    private val tvSource = itemView.findViewById<TextView>(R.id.tv_source)
    private val ivState = itemView.findViewById<ImageView>(R.id.iv_state)
    private val ivSynchronous = itemView.findViewById<ImageView>(R.id.iv_synchronous)

    fun bindTo(rvTestInfo: RecyclerViewTestInfo?) {

        if (rvTestInfo != null) {
            tvTestName.text = rvTestInfo.testName
            tvSource.text = rvTestInfo.testSource

            if (rvTestInfo.runSynchronously) {
                ivSynchronous.visibility = View.VISIBLE
            } else {
                ivSynchronous.visibility = View.INVISIBLE
            }

            if (rvTestInfo.skipTest && !TestRepository.useSingleTest) {
                tvTestName.setTextColor(ctx.resources.getColor(R.color.cw_tests_screen_title_disabled))
                tvSource.setTextColor(ctx.resources.getColor(R.color.cw_tests_screen_sub_title_disabled))
                ivState.visibility = View.INVISIBLE
            } else {
                tvTestName.setTextColor(ctx.resources.getColor(R.color.cw_tests_screen_title))
                tvSource.setTextColor(ctx.resources.getColor(R.color.cw_tests_screen_sub_title))
                ivState.visibility = View.VISIBLE

                when {
                    rvTestInfo.testSucceeded != null -> {
                        if (rvTestInfo.testSucceeded == true) {
                            ivState.setImageResource(R.drawable.cw_ic_checkmark)
                        } else {
                            ivState.setImageResource(R.drawable.cw_ic_fail)
                        }
                    }
                    rvTestInfo.testIsRunning -> ivState.setImageResource(R.drawable.cw_ic_running)
                    else -> ivState.visibility = View.INVISIBLE
                }
            }
        } else {
            tvTestName.text = ""
            tvSource.text = ""
            ivState.visibility = View.INVISIBLE
            ivSynchronous.visibility = View.INVISIBLE
        }
    }
}