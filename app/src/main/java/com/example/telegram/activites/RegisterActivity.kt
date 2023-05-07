package com.example.telegram.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.telegram.R
import com.example.telegram.databinding.ActivityRegisterBinding
import com.example.telegram.ui.fragments.EnterFoneNumberFragment
import com.example.telegram.utilits.initFireBase
import com.example.telegram.utilits.replaceFragment

class RegisterActivity : AppCompatActivity() {
    private lateinit var toolBar: Toolbar
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFireBase()
    }

    override fun onStart() {
        super.onStart()
        toolBar = binding.registerToolBar
        setSupportActionBar(toolBar)
        title = getString(R.string.register_title_phone)
        replaceFragment(EnterFoneNumberFragment())
    }
}