package com.example.telegram.ui.fragments

import android.view.*
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.utilits.hideKeyBoard

open class BaseChangeFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        if (activity is MainActivity) {
            (activity as MainActivity).appDrawer.disableDriver()
        }
        hideKeyBoard()

    }

    override fun onStop() {
        super.onStop()
        if (activity is MainActivity) {
            (activity as MainActivity).appDrawer.enableDriver()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_confirm_change -> change()
        }
        return true
    }

    open fun change() {

    }

}