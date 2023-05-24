package com.example.telegram.ui.message_recycle_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.UID
import com.example.telegram.ui.message_recycle_view.views.MessageView
import com.example.telegram.utilits.AppVoicePlayer
import com.example.telegram.utilits.asTime

class HolderVoiceMessage(view: View): RecyclerView.ViewHolder(view), MessageHolder {

    private val appVoicePlayer = AppVoicePlayer()

    private val blockUserVoiceMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_voice_message)
    private val chatUserVoiceMessageTime: TextView = view.findViewById(R.id.chat_user_voice_message_time)
    private val chatUserBtnPlay: ImageView = view.findViewById(R.id.chat_user_btn_play)
    private val chatUserBtnStop: ImageView = view.findViewById(R.id.chat_user_btn_stop)


    private val blockReceivingVoiceMessage: ConstraintLayout = view.findViewById(R.id.bloc_receiving_voice_message)
    private val chatReceivingVoiceMessageTime: TextView = view.findViewById(R.id.chat_recived_voice_message_time)
    private val chatReceivingBtnPlay: ImageView = view.findViewById(R.id.chat_recived_btn_play)
    private val chatReceivingBtnStop: ImageView = view.findViewById(R.id.chat_recived_btn_stop)


    //Функция отрисовывает звук
    override fun drawMessage(view: MessageView) {
        if (view.from == UID) {
            blockUserVoiceMessage.visibility = View.VISIBLE
            blockReceivingVoiceMessage.visibility = View.GONE
            chatUserVoiceMessageTime.text = view.timeStamp.asTime()
        } else {
            blockUserVoiceMessage.visibility = View.GONE
            blockReceivingVoiceMessage.visibility = View.VISIBLE
            chatReceivingVoiceMessageTime.text = view.timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        appVoicePlayer.init()
        if (view.from == UID) {
            chatUserBtnPlay.setOnClickListener {
                chatUserBtnPlay.visibility = View.GONE
                chatUserBtnStop.visibility = View.VISIBLE
                chatUserBtnStop.setOnClickListener {
                    stop {
                        chatUserBtnStop.setOnClickListener(null)
                        chatUserBtnPlay.visibility = View.VISIBLE
                        chatUserBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatUserBtnPlay.visibility = View.VISIBLE
                    chatUserBtnStop.visibility = View.GONE
                }
            }
        } else {
            chatReceivingBtnPlay.setOnClickListener {
                chatReceivingBtnPlay.visibility = View.GONE
                chatReceivingBtnStop.visibility = View.VISIBLE
                chatReceivingBtnStop.setOnClickListener {
                    stop {
                        chatReceivingBtnStop.setOnClickListener(null)
                        chatReceivingBtnPlay.visibility = View.VISIBLE
                        chatReceivingBtnStop.visibility = View.GONE
                    }
                }
                play(view) {
                    chatReceivingBtnPlay.visibility = View.VISIBLE
                    chatReceivingBtnStop.visibility = View.GONE
                }
            }
        }
    }

    private fun play(view: MessageView, function: () -> Unit) {
        appVoicePlayer.play(view.id, view.fileUrl){
            function()
        }
    }

    fun stop(function: () -> Unit) {
        appVoicePlayer.stop {
                function()
        }
    }

    override fun onDettach() {
        chatUserBtnPlay.setOnClickListener(null)
        chatReceivingBtnPlay.setOnClickListener(null)
        appVoicePlayer.release()
    }
}