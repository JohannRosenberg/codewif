package com.codewif.framework.ui.main.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.codewif.framework.R
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.ui.base.BaseFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class BaseTestFragment(layoutResId: Int, menuResId: Int) : BaseFragment(layoutResId, menuResId) {

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        recyclerView = rootView.findViewById(R.id.rv_tests)
        setupRecyclerView()
        return rootView
    }

    open fun setupRecyclerView() {
        // This prevents the recyclerview from flickering when updates are done.
        val animator = recyclerView.itemAnimator

        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_run_tests -> {
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    TestRunner.runTests()
                }
            }
            R.id.menu_stop_testing -> {
                TestRunner.cancelTesting()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
