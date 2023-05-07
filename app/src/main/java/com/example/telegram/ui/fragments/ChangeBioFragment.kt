package com.example.telegram.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.core.graphics.rotationMatrix
import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChangeBioBinding
import com.example.telegram.utilits.*

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

    override fun change() {
        super.change()
        val newBio = bio.settingsInputChangeBio.text.toString()
        REF_DATA_BASE_ROOT.child(NODE_USERS)
            .child(UID)
            .child(CHILD_BIO)
            .setValue(newBio)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showToast("Описание добавлено!")
                    USER.bio = newBio
                    fragmentManager?.popBackStack()
                }
            }
    }
}