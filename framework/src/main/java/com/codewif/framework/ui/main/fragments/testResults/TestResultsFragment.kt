package com.codewif.framework.ui.main.fragments.testResults

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.codewif.framework.R
import com.codewif.framework.da.local.TestRepository
import com.codewif.framework.models.RecyclerViewTestInfo
import com.codewif.framework.ui.main.fragments.base.BaseTestFragment
import com.codewif.framework.utils.formatToDuration


class TestResultsFragment : BaseTestFragment(R.layout.cw_fragment_test_results, R.menu.cw_menu_test_results) {

    private lateinit var viewmodel: TestResultsViewModel
    private val recyclerViewAdapter = TestResultsAdapter(::onListItemClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        viewmodel = ViewModelProviders.of(this)[TestResultsViewModel::class.java]

        viewmodel.getTestResults().observe(this, Observer { tests ->
            tests?.let {
                recyclerViewAdapter.submitList(tests)
            }
        })

        viewmodel.onTestingStateChanged.observe(this, Observer { isTesting ->

            ActivityCompat.invalidateOptionsMenu(activity)
            val headerView = activity?.findViewById<ConstraintLayout>(R.id.constraint_layout_testing_complete_header)

            if (TestRepository.testingEndTime != null) {
                // Display the header that shows the test results.
                if (headerView?.visibility == View.GONE) {
                    val testResultsSummary = viewmodel.getTestResultsSummary()

                    rootView.findViewById<TextView>(R.id.tv_total_tested).text = testResultsSummary.totalTested.toString()
                    rootView.findViewById<TextView>(R.id.tv_total_succeeded).text = testResultsSummary.totalSucceeded.toString()
                    rootView.findViewById<TextView>(R.id.tv_total_failed).text = testResultsSummary.totalFailed.toString()
                    rootView.findViewById<TextView>(R.id.tv_total_skipped).text = testResultsSummary.totalSkipped.toString()
                    rootView.findViewById<TextView>(R.id.tv_git_branch).text = testResultsSummary.gitBranchName
                    rootView.findViewById<TextView>(R.id.tv_duration).text =
                        testResultsSummary.duration.formatToDuration()


                    val slideDown = AnimationUtils.loadAnimation(activity, R.anim.cw_slide_down)
                    headerView.visibility = View.VISIBLE
                    headerView.startAnimation(slideDown)
                }
            } else {
                headerView?.visibility = View.GONE
            }
        })

        viewmodel.onFinalTestResultsUpdated.observe(this, Observer {
            viewmodel.refreshDatasource()

            recyclerView.postDelayed({
                recyclerView.layoutManager?.scrollToPosition(0)
            }, 400)
        })

        return rootView
    }

    private fun onListItemClicked(rvTestInfo: RecyclerViewTestInfo) {
        if (rvTestInfo.testSucceeded != null) {
            findNavController().navigate(TestResultsFragmentDirections.actionTestResultsToTestDetails(rvTestInfo.id))
        }
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
        return super.onOptionsItemSelected(item)
    }
}
