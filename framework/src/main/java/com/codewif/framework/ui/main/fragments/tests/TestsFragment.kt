package com.codewif.framework.ui.main.fragments.tests

import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.codewif.framework.R
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.testing.TestRunner
import com.codewif.framework.ui.main.CodewifMainActivity
import com.codewif.framework.ui.main.fragments.base.BaseTestFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TestsFragment : BaseTestFragment(R.layout.cw_fragment_tests, R.menu.cw_menu_tests) {

    private lateinit var viewmodel: TestsViewModel
    private val recyclerViewAdapter = TestsAdapter(::onListItemClicked, ::onSkipTestChangeListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        viewmodel = ViewModelProviders.of(this)[TestsViewModel::class.java]

        viewmodel.initializeForAllTests()

        viewmodel.getTests().observe(this, Observer { tests ->
            tests?.let {
                recyclerViewAdapter.submitList(tests)
            }
        })

        viewmodel.onTestingStateChanged.observe(this, Observer {
            ActivityCompat.invalidateOptionsMenu(activity)
        })

        return rootView
    }

    private fun onListItemClicked(rvTestInfo: RecyclerViewTestInfo) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            TestRepository.initializeForSingleTest(rvTestInfo.id)
            TestRunner.runTests()

            withContext(Dispatchers.Main) {
                (activity as CodewifMainActivity).navController.navigate(R.id.test_results_fragment)
            }
        }
    }

    private fun onSkipTestChangeListener(rvTestInfo: RecyclerViewTestInfo) {
        viewmodel.skipTest(rvTestInfo.id, rvTestInfo.skipTest)
    }

    override fun setupRecyclerView() {
        super.setupRecyclerView()
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if (viewmodel.testsAreRunning) {
            menu.findItem(R.id.menu_run_tests).isVisible = false
            menu.findItem(R.id.menu_stop_testing).isVisible = true
        } else {
            menu.findItem(R.id.menu_stop_testing).isVisible = false
            menu.findItem(R.id.menu_run_tests).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val result: Boolean

        when (item.itemId) {
            R.id.menu_run_tests -> {
                TestRepository.initializeForAllTests()
                result = super.onOptionsItemSelected(item)
                (activity as CodewifMainActivity).navController.navigate(R.id.test_results_fragment)
            }
            else -> {
                result = super.onOptionsItemSelected(item)
            }
        }

        return result
    }
}
