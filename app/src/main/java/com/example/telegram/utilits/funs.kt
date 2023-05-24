package com.example.telegram.utilits

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.database.updatePhonesToDataBase
import com.example.telegram.models.CommonModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

//Вывод различных тостов
fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

//Создает новый интент и открывает активити а старый закрывает
fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

//Функция Устанавливает фрагмент и выполнякт коммит
fun replaceFragment(fragment: Fragment, atStack: Boolean = true) {
    if (atStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment).commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(R.id.data_container, fragment).commit()
    }
}

//Функция прячет клавиатуру
fun hideKeyBoard() {
    val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

//Функция скачивает картинку из базы данных
fun ImageView.downLoadAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.user)
        .into(this)
}

//Фуекция считывает номера телефонов
@SuppressLint("Range")
fun initContacts() {
    if (checkPermissions(READ_CONTACT)) {
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s, -]"), "")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        updatePhonesToDataBase(arrayContacts)
    }
}

//Функция устанавливает формат времени
fun String.asTime(): String {
    val time = Date(this.toLong())
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(time)
}

//Функция возвращает по uri название файла
@SuppressLint("Range")
fun getFileNameFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
    try {
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        }
    } catch (e: Exception) {
        showToast(e.message.toString())
    } finally {
        cursor?.close()
        return result
    }

}
