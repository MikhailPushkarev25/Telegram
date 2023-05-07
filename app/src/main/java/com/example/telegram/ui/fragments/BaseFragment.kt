package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.databinding.FragmentBaseBinding

open class BaseFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        if (activity is MainActivity) {
            (activity as MainActivity).appDrawer.disableDriver()
        }
    }

    override fun onStop() {
        super.onStop()
        if (activity is MainActivity) {
            (activity as MainActivity).appDrawer.enableDriver()
        }
    }
}