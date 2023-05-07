package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChangeNameBinding
import com.example.telegram.databinding.FragmentEnterCodeBinding
import com.example.telegram.utilits.*
import kotlin.concurrent.fixedRateTimer

class ChangeNameFragment : BaseChangeFragment() {
    lateinit var name: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        name = FragmentChangeNameBinding.inflate(inflater, container, false)
        return name.root
    }

    override fun onResume() {
        super.onResume()
        val fullNameList = USER.fullname.split(" ")
        if (fullNameList.size > 1) {
            name.settingsInputChangeName.setText(fullNameList[0])
            name.settingsInputChangeSurname.setText(fullNameList[1])
        } else name.settingsInputChangeName.setText(fullNameList[0])
    }

    override fun change() {
        val names = name.settingsInputChangeName.text.toString()
        val surname = name.settingsInputChangeSurname.text.toString()
        if(names.isEmpty()) {
           showToast(getString(R.string.settings_name_isEmpty))
        } else {
            val fullname = "$names $surname"
            REF_DATA_BASE_ROOT
                .child(NODE_USERS)
                .child(UID)
                .child(CHILD_FULLNAME)
                .setValue(fullname).addOnCompleteListener{
                    if (it.isSuccessful) {
                        showToast(getString(R.string.settings_toast_FIO))
                        USER.fullname = fullname
                        APP_ACTIVITY.appDrawer.updateHeader()
                        fragmentManager?.popBackStack()
                    }
                }
        }
    }
}