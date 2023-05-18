package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentChangeUserNameBinding
import com.example.telegram.utilits.*
import java.util.*

class ChangeUserNameFragment : BaseChangeFragment() {
    lateinit var usernames: FragmentChangeUserNameBinding
    lateinit var newUserName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        usernames = FragmentChangeUserNameBinding.inflate(layoutInflater, container, false)
        return usernames.root
    }

    override fun onResume() {
        super.onResume()
        usernames.settingsInputChangeUsername.setText(USER.username)
    }

    //Функция для изменения имени и фамилии в тулбаре
    override fun change() {
       newUserName = usernames.settingsInputChangeUsername.text.toString().toLowerCase(Locale.getDefault())
        if (newUserName.isEmpty()) {
            showToast("Введите данные")
        } else {
            REF_DATA_BASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener{
                    if (it.hasChild(newUserName)) {
                        showToast("Такой пользователь уже существует!")
                    } else {
                        changeUserName(newUserName)
                    }
                })
        }
    }
}