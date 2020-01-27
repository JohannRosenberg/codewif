package com.codewif.framework.ui.base

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment {

    constructor(@LayoutRes layoutId: Int) {
        this.layoutId = layoutId
    }

    constructor(@LayoutRes layoutId: Int, @MenuRes menuId: Int?) {
        this.layoutId = layoutId
        this.menuId = menuId
    }

    private var fragmentInteractionListener: OnFragmentInteractionListener? = null

    private var layoutId: Int = 0
    private var menuId: Int? = null
    lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(layoutId, container, false)

        if (menuId != null) {
            setHasOptionsMenu(true)
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentInteractionListener = context as OnFragmentInteractionListener
    }


    override fun onDetach() {
        super.onDetach()
        fragmentInteractionListener = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuId != null)
            inflater.inflate(menuId as Int, menu)

    }
}