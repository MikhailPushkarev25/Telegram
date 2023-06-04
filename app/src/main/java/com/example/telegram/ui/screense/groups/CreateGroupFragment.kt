package com.example.telegram.ui.screense.groups

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentCreateGroupBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screense.base.BaseFragment
import com.example.telegram.ui.screense.main_list.MainListFragment
import com.example.telegram.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class CreateGroupFragment(private var listContacts: List<CommonModel>) : BaseFragment()  {

    private lateinit var group: FragmentCreateGroupBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter
    private var uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        group = FragmentCreateGroupBinding.inflate(inflater, container, false)
        return group.root
    }
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Создать группу"
        hideKeyBoard()
        initRecycleView()
        group.createGroupPhoto.setOnClickListener { addPhoto() }
        group.createGroupBtnOk.setOnClickListener {
            val nameGroup = group.createGroupInput.text.toString()
            if (nameGroup.isEmpty()) {
                showToast("Введите название группы")
            } else {
                createGroupToDB(nameGroup, uri, listContacts) {
                    replaceFragment(MainListFragment())
                }
            }
        }
        group.createGroupInput.requestFocus()
        group.createGroupAccount.text = getPlurals(listContacts.size)
    }

    //Функция добавляет фото в группу
    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecycleView() {
        recyclerView = group.createGroupRecycleView
        adapter = AddContactsAdapter()
        recyclerView.adapter = adapter
        listContacts.forEach {  adapter.updateListItems(it) }
    }

    //получение результата установки или изменения фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK && data != null) {
            uri = CropImage.getActivityResult(data).uri
            group.createGroupPhoto.setImageURI(uri)
        }
    }
}