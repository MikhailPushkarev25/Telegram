package com.example.telegram.ui.screense.settings

import android.os.Bundle
import android.view.*
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentChangeNameBinding
import com.example.telegram.ui.screense.base.BaseChangeFragment
import com.example.telegram.utilits.*

class ChangeNameFragment : BaseChangeFragment() {
    lateinit var name: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        name = FragmentChangeNameBinding.inflate(inflater, container, false)
        return name.root
    }

    //Метод делит имя фамилию
    override fun onResume() {
        super.onResume()
        val fullNameList = USER.fullname.split(" ")
        if (fullNameList.size > 1) {
            name.settingsInputChangeName.setText(fullNameList[0])
            name.settingsInputChangeSurname.setText(fullNameList[1])
        } else name.settingsInputChangeName.setText(fullNameList[0])
    }

    //Метод для изменения имени и фамилии
    override fun change() {
        val names = name.settingsInputChangeName.text.toString()
        val surname = name.settingsInputChangeSurname.text.toString()
        if(names.isEmpty()) {
           showToast(getString(R.string.settings_name_isEmpty))
        } else {
            val fullname = "$names $surname"
            setNameToDataBase(fullname)
        }
    }
}