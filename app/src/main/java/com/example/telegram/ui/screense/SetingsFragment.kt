package com.example.telegram.ui.screense
import android.app.Activity.RESULT_OK
import android.content.Intent

import android.os.Bundle
import android.view.*

import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentSetingsBinding
import com.example.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class SetingsFragment : BaseFragment() {
    lateinit var settings: FragmentSetingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settings = FragmentSetingsBinding.inflate(inflater, container, false)
        return settings.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    //Инициализация всех полей из базы
    private fun initFields() {
        settings.settingsBio.text = USER.bio
        settings.settingsFullname.text = USER.username
        settings.settingsPhoneNumber.text = USER.phone
        settings.tvTextSet.text = USER.state
        settings.tvTextName.text = USER.fullname
        settings.settingsBtnChangeLogin.setOnClickListener { replaceFragment(ChangeUserNameFragment()) }
        settings.settingsBtnChangeBio.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        settings.settingsChangePhoto.setOnClickListener { changePhotoUser() }
        settings.settingsUserPhoto.downLoadAndSetImage(USER.photoUrl)
    }


    //Функция ищет фото в телефоне и обрезает его по параметрам пользователя
    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    //Фуекция создает меню
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_actions_menu, menu)
    }

    //Функция отрабатывает при выходе и меняет статус
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_menu_change_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }

            R.id.settings_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }

    //получение результата установки или изменения фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val paf = REF_STORAGE_ROOT
                .child(FOLDER_PROFILE_IMAGE)
                .child(UID)
            putFileToStorage(uri, paf) {
                getUrlFromStorage(paf) {
                    putUrlToDataBase(it) {
                        settings.settingsUserPhoto.downLoadAndSetImage(it)
                        showToast("Фото успешно добавлено!")
                        USER.photoUrl = it
                        APP_ACTIVITY.appDrawer.updateHeader()
                    }
                }
            }
        }
    }
}