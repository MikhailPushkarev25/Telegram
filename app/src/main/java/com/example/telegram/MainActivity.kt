package com.example.telegram

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.telegram.database.AUTH
import com.example.telegram.database.initFireBase
import com.example.telegram.database.initUser
import com.example.telegram.databinding.ActivityMainBinding
import com.example.telegram.ui.fragments.MainFragment
import com.example.telegram.ui.fragments.register.EnterFoneNumberFragment
import com.example.telegram.ui.objects.AppDrawer
import com.example.telegram.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appDrawer: AppDrawer
    lateinit var toolBar: Toolbar

    //Главный активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY = this
        initFireBase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }

    //При инициализации проверяет авторизован ли пользователь
    private fun initFunc() {
        setSupportActionBar(toolBar)
        if (AUTH.currentUser != null) {
            appDrawer.create()
            replaceFragment(MainFragment(), false)
        } else {
            replaceFragment(EnterFoneNumberFragment(), false)
        }
    }

    //Инициализация тулбара и драйвера
    private fun initFields() {
        toolBar = binding.mainToolbar
        appDrawer = AppDrawer()
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateState(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateState(AppStates.OFFLINE)
    }

    //если контакты совпадают в базе и в телефоне выполняем метод считывания
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACT) == PackageManager.PERMISSION_GRANTED) {
            initContacts()
        }
    }
}