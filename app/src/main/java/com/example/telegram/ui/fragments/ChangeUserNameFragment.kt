package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
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
                        changeUserName()
                    }
                })
        }
    }

    private fun changeUserName() {
        REF_DATA_BASE_ROOT
            .child(NODE_USERNAMES)
            .child(newUserName)
            .setValue(UID)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    updateCurrentUsername()
                }
            }
    }

    private fun updateCurrentUsername() {
        REF_DATA_BASE_ROOT
            .child(NODE_USERS)
            .child(UID)
            .child(CHILD_USERNAME)
            .setValue(newUserName)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Имя изменено!")
                    deleteOldUsername()
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }

    private fun deleteOldUsername() {
        REF_DATA_BASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Имя изменено!")
                    fragmentManager?.popBackStack()
                    USER.username = newUserName
                } else {
                    showToast(it.exception?.message.toString())
                }
            }
    }
}