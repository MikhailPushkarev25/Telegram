package com.example.telegram.utilits

import android.text.Editable
import android.text.TextWatcher

class AppTextWeatcher(val onSucess:(Editable?) -> Unit): TextWatcher {

    //После авторизации отрабатвает класс для ввода цифр
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        onSucess(p0)
    }
}