package com.codewif.framework.ui.main.fragments.testResultsDetails

import android.os.Bundle
import android.view.*
import android.view.View.LAYER_TYPE_HARDWARE
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.codewif.framework.R
import com.codewif.framework.models.TestInfo
import com.codewif.framework.ui.base.BaseFragment
import com.codewif.framework.ui.utils.Notifications
import com.codewif.framework.utils.formatToDuration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.cw_fragment_test_results_details.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class TestResultsDetailsFragment : BaseFragment(R.layout.cw_fragment_test_results_details) {

    private lateinit var viewmodel: TestResultsDetailsViewModel
    private lateinit var testInfo: TestInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        viewmodel = ViewModelProviders.of(this)[TestResultsDetailsViewModel::class.java]

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: TestResultsDetailsFragmentArgs by navArgs()
        testInfo = viewmodel.getTestInfo(args.testId)

        if (testInfo.testResults?.succeeded == false)
            setHasOptionsMenu(true)

        tv_test_name.text = testInfo.testName
        tv_duration.text = testInfo.testResults?.duration.formatToDuration()

        if (testInfo.testResults?.details.isNullOrEmpty()) {
            cardview_details.visibility = View.GONE
        } else {
            tv_details.text = testInfo.testResults?.details
        }

        // See https://stackoverflow.com/questions/37601314/setting-view-alpha-in-runtime-is-slow-how-to-speed-it-up
        ViewCompat.setLayerType(iv_snapshot_previous, LAYER_TYPE_HARDWARE, null)

        // Setup up seekbar to allow the user to change the alpha on the previous image.
        seekbar_alpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                iv_snapshot_previous.alpha = p1.toFloat() / 100f
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        if (testInfo.testResults?.details == null) {
            cardview_details.visibility = View.GONE
        }

        rootView.post {
            if (testInfo.isUITest) {
                if (testInfo.testResults?.uiTestInfoCurrent?.snapshotUrl != null) {
                    if (testInfo.testResults?.uiTestInfoCurrent?.snapshotUrl?.startsWith("/") == true) {
                        iv_snapshot_current.load(File(testInfo.testResults?.uiTestInfoCurrent?.snapshotUrl))
                    } else {
                        iv_snapshot_current.load(testInfo.testResults?.uiTestInfoCurrent?.snapshotUrl)
                    }
                }

                if ((testInfo.testResults?.succeeded == true) || (testInfo.uiTestInfoPrevious == null)) {
                    constraint_layout_images_footer.visibility = View.GONE
                    iv_snapshot_current.layoutParams.height = rootView.height - 60
                    iv_snapshot_current.requestLayout()

                } else {
                    if (testInfo.uiTestInfoPrevious?.snapshotUrl != null) {
                        testInfo.uiTestInfoPrevious?.snapshotUrl?.let {
                            if (it.startsWith("/")) {
                                iv_snapshot_previous.load(File(it))
                            } else {
                                iv_snapshot_previous.load(it)
                            }
                        }
                    } else {
                        constraint_layout_images_footer.visibility = View.GONE
                    }

                    val h = rootView.height - constraint_layout_images_footer.height - 80
                    iv_snapshot_current.layoutParams.height = h
                    iv_snapshot_current.requestLayout()
                    iv_snapshot_previous.layoutParams.height = h
                    iv_snapshot_previous.requestLayout()
                }
            } else {
                constraint_layout_images.visibility = View.GONE
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (testInfo.testResults?.succeeded == false)
            inflater.inflate(R.menu.cw_menu_test_results_details, menu)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        if (testInfo.isUITest && (testInfo.testResults?.succeeded == true)) {
            menu.findItem(R.id.menu_update_snapshot_image).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_update_snapshot_image -> {
                val alertDialog: AlertDialog
                val builder = AlertDialog.Builder(context!!)

                builder.setMessage(getString(R.string.cw_replace_previous_snapshot))
                    .setPositiveButton(R.string.cw_ok) { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            viewmodel.storeUITest(testInfo)

                            withContext(Dispatchers.Main) {
                                val notifications = Notifications()
                                notifications.showInfoSnackbar(R.string.cw_snapshot_updated, Snackbar.LENGTH_INDEFINITE)
                            }
                        }
                    }
                    .setNegativeButton(R.string.cw_cancel) { _, _ ->
                    }

                alertDialog = builder.show()
                alertDialog.setCancelable(false)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
