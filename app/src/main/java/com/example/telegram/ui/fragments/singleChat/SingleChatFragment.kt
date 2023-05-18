package com.example.telegram.ui.fragments.singleChat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentSingleChatBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.models.User
import com.example.telegram.ui.fragments.BaseFragment
import com.example.telegram.ui.fragments.message_recycle_view.view_holder.AppViewHolder
import com.example.telegram.utilits.*
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleChatFragment(private val contact: CommonModel) : BaseFragment() {
    lateinit var singleChat: FragmentSingleChatBinding
    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var receivengUser: User
    private lateinit var refUser: DatabaseReference
    private lateinit var refMessages: DatabaseReference
    private lateinit var adapter: SingleChatAdapter
    private lateinit var recycleView: RecyclerView
    private lateinit var messagesListener: AppChildValueEventListener
    private var countMessage = 15
    private var isScrolling = false
    private var isSmoothScrollToPosition = true
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecord: AppVoiceRecord

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        singleChat = FragmentSingleChatBinding.inflate(inflater, container, false)
        return singleChat.root
    }
    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        appVoiceRecord = AppVoiceRecord()
        swipeRefreshLayout = singleChat.chatSwipeRefresh
        layoutManager = LinearLayoutManager(this.context)
        singleChat.chatInputMessage.addTextChangedListener(AppTextWeatcher{
            val string = singleChat.chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись") {
                singleChat.chatBtnMessage.visibility = View.GONE
                singleChat.chatBtnAttachMessage.visibility = View.VISIBLE
                singleChat.chatBtnVoiceMessage.visibility = View.VISIBLE
            } else {
                singleChat.chatBtnMessage.visibility = View.VISIBLE
                singleChat.chatBtnAttachMessage.visibility = View.GONE
                singleChat.chatBtnVoiceMessage.visibility = View.GONE
            }
        })
        singleChat.chatBtnAttachMessage.setOnClickListener { attachFile() }

        //Корутины запускает слушатель записи звука
        CoroutineScope(Dispatchers.IO).launch {
            singleChat.chatBtnVoiceMessage.setOnTouchListener { v, event ->
                if (checkPermissions(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //TODO record
                        singleChat.chatInputMessage.setText("Запись")
                        singleChat.chatBtnVoiceMessage.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.material_drawer_primary
                            )
                        )
                        val messageKey = getMessageKey(contact.id)
                        appVoiceRecord.startRecord(messageKey)
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        //TODO stop record
                        singleChat.chatInputMessage.setText("")
                        singleChat.chatBtnVoiceMessage.colorFilter = null
                        appVoiceRecord.stopRecord() { file, messageKey ->
                            uploadFileToStorage(Uri.fromFile(file), messageKey, contact.id, TYPE_MESSAGE_VOICE)
                            isSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    //Функция имеет доступ к файлам телефона
    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(180, 180)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    //Функция создает в базе ноду в которой храняться данные для сообщений
    private fun initRecycleView() {
        recycleView = singleChat.chatRecycleView
        adapter = SingleChatAdapter()
        refMessages = REF_DATA_BASE_ROOT
            .child(NODE_MESSAGES)
            .child(UID)
            .child(contact.id)
        recycleView.adapter = adapter
        recycleView.setHasFixedSize(true)
        recycleView.isNestedScrollingEnabled = false
        recycleView.layoutManager = layoutManager
        messagesListener = AppChildValueEventListener{
            val message = it.getCommonModel()
            if (isSmoothScrollToPosition) {
                adapter.addItemToBottom(AppViewHolder.getView(message)) {
                    recycleView.smoothScrollToPosition(adapter.itemCount)
                }
            } else {
                adapter.addItemToTop(AppViewHolder.getView(message)) {
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        refMessages.limitToLast(countMessage).addChildEventListener(messagesListener)
        //Функция слушает скролл и выполняет логику добавления элементов
        recycleView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })
        // При свайпе обновляются элементы
        swipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    //Функция добавляет 10 элементов при скроллинге вверх
    private fun updateData() {
        isSmoothScrollToPosition = false
        isScrolling = false
        countMessage += 10
        refMessages.removeEventListener(messagesListener)
        refMessages.limitToLast(countMessage).addChildEventListener(messagesListener)
    }

    //Функция для ввода текста в сообщениях
    private fun initToolbar() {
        listenerInfoToolbar = AppValueEventListener {
            receivengUser = it.getUserModel()
            initInfoToolBar()
        }
        refUser = REF_DATA_BASE_ROOT.child(NODE_USERS).child(contact.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        singleChat.chatBtnMessage.setOnClickListener {
            isSmoothScrollToPosition = true
            val message = singleChat.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Необходимо ввести сообщение!")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                singleChat.chatInputMessage.setText("")
            }
        }
    }

    //Инициализация данных тулбара
    private fun initInfoToolBar() {
        if (receivengUser.fullname.isEmpty()) {
            singleChat.contactChatFullname.text = contact.fullname
        } else  singleChat.contactChatFullname.text = receivengUser.fullname
        singleChat.toolbarChatImage.downLoadAndSetImage(receivengUser.photoUrl)
        singleChat.contactChatStatus.text = receivengUser.state
    }

    //Функция принимает слушатель и качает из базы файлы
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK && data != null) {
            val messageKey = getMessageKey(contact.id)
            val uri = CropImage.getActivityResult(data).uri
            uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
            isSmoothScrollToPosition = true
        }
    }

    override fun onPause() {
        super.onPause()
        refUser.removeEventListener(listenerInfoToolbar)
        refMessages.removeEventListener(messagesListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appVoiceRecord.releaseRecord()
    }
}