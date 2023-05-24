package com.example.telegram.ui.screense.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.database.AUTH
import com.example.telegram.databinding.FragmentEnterFoneNumberBinding
import com.example.telegram.utilits.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class EnterFoneNumberFragment : Fragment() {
    lateinit var phone: FragmentEnterFoneNumberBinding
    private lateinit var phoneNumber: String
    private lateinit var callBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        phone = FragmentEnterFoneNumberBinding.inflate(layoutInflater)
        return phone.root
    }

    //При старте фрагмента считывает ввод пользователем номер телефона
    override fun onStart() {
        super.onStart()
        callBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener{
                    if (it.isSuccessful) {
                        showToast("Добро пожаловать")
                        restartActivity()
                    } else showToast(it.exception?.message.toString())
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(phoneNumber, id))
            }
        }
        phone.registerBtnNext.setOnClickListener {
            sendCode()
        }
    }

    private fun sendCode() {
        if (phone.registerInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_text))
        } else {
            authUser()
        }
    }

    //Функция дает доступ базе данных на авторизацию по номеру телефона
    private fun authUser() {
        phoneNumber = phone.registerInputPhoneNumber.text.toString()
        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions
                .newBuilder(FirebaseAuth.getInstance())
                .setActivity(APP_ACTIVITY)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callBack)
                .build()
        )
    }
}