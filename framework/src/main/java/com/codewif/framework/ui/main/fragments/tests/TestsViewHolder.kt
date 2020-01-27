package com.codewif.framework.ui.main.fragments.tests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewif.framework.R
import com.codewif.framework.models.RecyclerViewTestInfo

class TestsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.cw_tests_list_item, parent, false)
) {
    private val tvTestName = itemView.findViewById<TextView>(R.id.tv_test_name)
    private val tvSource = itemView.findViewById<TextView>(R.id.tv_source)
    private val switchEnabled = itemView.findViewById<Switch>(R.id.switch_enabled)
    private val ivSynchronous = itemView.findViewById<ImageView>(R.id.iv_synchronous)

    fun bindTo(rvTestInfo: RecyclerViewTestInfo?) {

        if (rvTestInfo != null) {
            if (rvTestInfo.runSynchronously) {
                ivSynchronous.visibility = View.VISIBLE
            } else {
                ivSynchronous.visibility = View.INVISIBLE
            }

            tvTestName.text = rvTestInfo.testName
            tvSource.text = rvTestInfo.testSource
            switchEnabled.visibility = View.VISIBLE
            switchEnabled.isChecked = !rvTestInfo.skipTest

        } else {
            tvTestName.text = ""
            tvSource.text = ""
            switchEnabled.visibility = View.INVISIBLE
            ivSynchronous.visibility = View.INVISIBLE
        }
    }
}