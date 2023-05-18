package com.example.telegram.ui.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentEnterCodeBinding
import com.example.telegram.utilits.*
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment(val phoneNumber: String, val id: String) : Fragment() {
    lateinit var phone: FragmentEnterCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        phone = FragmentEnterCodeBinding.inflate(inflater, container, false)
        return phone.root
    }

    //Функция при старте, определяет ввод 6 цифр из базы данных
    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        phone.registerInputCode.addTextChangedListener(AppTextWeatcher {
                val string = phone.registerInputCode.text.toString()
                if (string.length == 6) {
                    enterCode()
                }
        })
    }


    //Функция считывает из базы данных ввод цифр пользователем
    private fun enterCode() {
        val code = phone.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener{ auth ->
            if (auth.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber
                dateMap[CHILD_USERNAME] = uid
                REF_DATA_BASE_ROOT.child(NODE_USERS).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListener{
                        if (!it.hasChild(CHILD_USERNAME)) {
                            dateMap[CHILD_USERNAME] = uid
                        } else {
                            REF_DATA_BASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                                .addOnFailureListener { showToast(it.message.toString()) }
                                .addOnSuccessListener {
                                    REF_DATA_BASE_ROOT.child(NODE_USERS).child(uid)
                                        .updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            showToast("Добро пожаловать")
                                            restartActivity()
                                        }
                                        .addOnFailureListener { showToast(it.message.toString()) }
                                }
                        }
                    })
            } else showToast(auth.exception?.message.toString())
        }
    }
}