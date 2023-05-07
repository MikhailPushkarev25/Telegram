package com.example.telegram.ui.fragments
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*

import com.example.telegram.R
import com.example.telegram.activites.RegisterActivity
import com.example.telegram.databinding.FragmentSetingsBinding
import com.example.telegram.utilits.*
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
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


    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.settings_actions_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_menu_change_exit -> {
                AUTH.signOut()
                (APP_ACTIVITY).replaceActivity(RegisterActivity())
            }

            R.id.settings_menu_change_name -> {
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val paf = REF_STORAGE_ROOT
                .child(FOLDER_PROFILE_IMAGE)
                .child(UID)
            putImageToStorage(uri, paf) {
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