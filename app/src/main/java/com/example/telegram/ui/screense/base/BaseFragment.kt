package com.example.telegram.ui.screense.base

import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity

open class BaseFragment : Fragment() {

    //Основной фрагмент от которого будут расширяться другие
    override fun onStart() {
        super.onStart()
        if (activity is MainActivity) {
            (activity as MainActivity).appDrawer.disableDriver()
        }
    }

//    override fun onStop() {
//        super.onStop()
//        if (activity is MainActivity) {
//            (activity as MainActivity).appDrawer.enableDriver()
//        }
//    }
}