package com.example.telegram.ui.screense.settings

import android.os.Bundle
import android.view.*
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentChangeBioBinding
import com.example.telegram.ui.screense.base.BaseChangeFragment

class ChangeBioFragment : BaseChangeFragment() {
    lateinit var bio: FragmentChangeBioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bio = FragmentChangeBioBinding.inflate(inflater, container, false)
        return bio.root
    }

    override fun onResume() {
        super.onResume()
        bio.settingsInputChangeBio.setText(USER.bio)
    }

    //Функция для ввода текста в описании
    override fun change() {
        super.change()
        val newBio = bio.settingsInputChangeBio.text.toString()
        setBioToDataBase(newBio)
    }
}