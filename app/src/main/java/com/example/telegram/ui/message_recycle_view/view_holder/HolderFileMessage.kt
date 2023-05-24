package com.example.telegram.ui.message_recycle_view.view_holder

import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.UID
import com.example.telegram.database.getFileFromStorage
import com.example.telegram.ui.message_recycle_view.views.MessageView
import com.example.telegram.utilits.*
import java.io.File

class HolderFileMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val blockUserfileMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_file_message)
    private val chatUserfileMessageTime: TextView = view.findViewById(R.id.chat_user_file_message_time)


    private val blockReceivingfileMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_file_message)
    private val chatReceivingfileMessageTime: TextView = view.findViewById(R.id.chat_recived_file_message_time)

    private val chatUserFileName: TextView = view.findViewById(R.id.chat_user_filename)
    private val chatUserBtnDoneload: ImageView = view.findViewById(R.id.chat_user_btn_download)
    private val chatUserProgressBar: ProgressBar = view.findViewById(R.id.chat_user_progress_bar)

    private val chatReceivingFileName: TextView = view.findViewById(R.id.chat_recived_filename)
    private val chatReceivingBtnDoneload: ImageView = view.findViewById(R.id.chat_recived_btn_download)
    private val chatReceivingProgressBar: ProgressBar = view.findViewById(R.id.chat_recived_progress_bar)


    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserfileMessage.visibility = View.VISIBLE
            blockReceivingfileMessage.visibility = View.GONE
            chatUserfileMessageTime.text = view.timeStamp.asTime()
            chatUserFileName.text = view.text
        } else {
            blockUserfileMessage.visibility = View.GONE
            blockReceivingfileMessage.visibility = View.VISIBLE
            chatReceivingfileMessageTime.text = view.timeStamp.asTime()
            chatReceivingFileName.text = view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == UID) chatUserBtnDoneload.setOnClickListener { clickToBtnFile(view) }
        else chatReceivingBtnDoneload.setOnClickListener { clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == UID) {
            chatUserBtnDoneload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivingBtnDoneload.visibility = View.INVISIBLE
            chatReceivingProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if (checkPermissions(WRITE_FILES)) {
                file.createNewFile()
                getFileFromStorage(file, view.fileUrl) {
                    if (view.from == UID) {
                        chatUserBtnDoneload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatReceivingBtnDoneload.visibility = View.VISIBLE
                        chatReceivingProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    override fun onDettach() {
        chatUserBtnDoneload.setOnClickListener(null)
        chatReceivingBtnDoneload.setOnClickListener(null)
    }
}