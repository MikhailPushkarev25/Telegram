package com.example.telegram.ui.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.telegram.R
import com.example.telegram.ui.screense.contacts.ContactsFragment
import com.example.telegram.ui.screense.settings.SetingsFragment
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.database.USER
import com.example.telegram.ui.screense.groups.AddContactsFragment
import com.example.telegram.utilits.downLoadAndSetImage
import com.example.telegram.utilits.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

class AppDrawer {

    private lateinit var driver: Drawer
    private lateinit var header: AccountHeader
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var currentProfile: ProfileDrawerItem

    fun create() {
        initLoader()
        createHeader()
        createDriver()
        drawerLayout = driver.drawerLayout
    }

    //Функция определяет закрытие меню и вместо гамбургера выводит стрелку
    fun disableDriver() {
        driver.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.toolBar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    //Функция определяет открытие меню и вместо стрелки выводит гамбургер
    fun enableDriver() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        driver.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.toolBar.setNavigationOnClickListener {
            driver.openDrawer()
        }
    }

    //Все параметры в выпадющем блоке
    private fun createDriver() {
        driver = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.toolBar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(header)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(100)
                    .withIconTintingEnabled(true)
                    .withName("Создать группу")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_people_24),
                PrimaryDrawerItem().withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withName("Контакты")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_person_24),
                PrimaryDrawerItem().withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withIcon(R.drawable.baseline_settings_applications_24)
            ).withOnDrawerItemClickListener(object: Drawer.OnDrawerItemClickListener{
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)
                    return false
                }

            })
            .build()
    }

    //Проверка по id по клику на параметр
    private fun clickToItem(position: Int) {
        when(position) {
            1 -> replaceFragment(AddContactsFragment())
            3 -> replaceFragment(SetingsFragment())
            2 -> replaceFragment(ContactsFragment())
        }
    }

    //Функция отображает данные в выпадающей строке
    private fun createHeader() {
        currentProfile = ProfileDrawerItem()
            .withName(USER.fullname)
            //.withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)
        header = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.drawable.header)
            .addProfiles(
                currentProfile
            ).build()
    }

    //Функция меняет в реальном времени данные в выпадающем блоке
    fun updateHeader() {
        currentProfile
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        header.updateProfile(currentProfile)
    }

    //Функция устанавливает фото с помощью библиотеки
    private fun initLoader() {
        DrawerImageLoader.init(object: AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downLoadAndSetImage(uri.toString())
            }
        })
    }
}