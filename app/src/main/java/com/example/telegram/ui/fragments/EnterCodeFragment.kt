package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.activites.RegisterActivity
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

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        phone.registerInputCode.addTextChangedListener(AppTextWeatcher {
                val string = phone.registerInputCode.text.toString()
                if (string.length == 6) {
                    enterCode()
                }
        })
    }


    private fun enterCode() {
        val code = phone.registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener{
            if (it.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber
                dateMap[CHILD_USERNAME] = uid

                REF_DATA_BASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap).addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать")
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    } else showToast(task.exception?.message.toString())
                }
            } else showToast(it.exception?.message.toString())
        }
    }
}