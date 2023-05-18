package com.example.telegram.utilits

import android.media.MediaRecorder
import java.io.File

class AppVoiceRecord {

        private val mediaRecorder = MediaRecorder()
        private lateinit var file: File
        private lateinit var mMessageKey: String

        fun startRecord(messageKey: String) {
            try {
                mMessageKey = messageKey
                createFileForeRecord()
                prepareMediaRecorder()
                mediaRecorder.start()
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }

        private fun prepareMediaRecorder() {
            mediaRecorder.apply {
                reset()
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(file.absolutePath)
                prepare()
            }
        }

        private fun createFileForeRecord() {
            file =  File(APP_ACTIVITY.filesDir, mMessageKey)
            file.createNewFile()
        }

        fun stopRecord(onSuccess: (file: File, messageKey: String) -> Unit) {
            try {
                mediaRecorder.stop()
                onSuccess(file, mMessageKey)
            } catch (e: Exception) {
                showToast(e.message.toString())
                file.delete()
            }
        }

        fun releaseRecord() {
            try {
                mediaRecorder.release()
            } catch (e: Exception) {
                showToast(e.message.toString())
            }
        }
}