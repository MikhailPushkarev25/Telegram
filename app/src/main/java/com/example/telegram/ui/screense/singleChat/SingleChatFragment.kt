package com.example.telegram.ui.screense.singleChat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
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
import com.example.telegram.ui.screense.base.BaseFragment
import com.example.telegram.ui.message_recycle_view.view_holder.AppViewFactory
import com.example.telegram.ui.screense.main_list.MainListFragment
import com.example.telegram.utilits.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

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
        setHasOptionsMenu(true)
        bottomSheetBehavior = BottomSheetBehavior.from(singleChat.cheet.bottomSheetChoice)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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
        singleChat.chatBtnAttachMessage.setOnClickListener { attach() }

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

    private fun attach() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        singleChat.cheet.btnAttachFile.setOnClickListener { attachFile() }
        singleChat.cheet.btnAttachImage.setOnClickListener { attachImage() }
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE   )
    }

    //Функция имеет доступ к файлам телефона
    private fun attachImage() {
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
                adapter.addItemToBottom(AppViewFactory.getView(message)) {
                    recycleView.smoothScrollToPosition(adapter.itemCount)
                }
            } else {
                adapter.addItemToTop(AppViewFactory.getView(message)) {
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
                    //Когда очищаем чат что бы прогрузка при обновлении не крутилась убираем свайпом
                    swipeRefreshLayout.isRefreshing = false
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
            } else sendMessage(message, contact.id, TYPE_MESSAGE_TEXT) {
                saveToMainList(contact.id, TYPE_CHAT)
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

        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val messageKey = getMessageKey(contact.id)
                    val uri = CropImage.getActivityResult(data).uri
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    isSmoothScrollToPosition = true
                }

                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessageKey(contact.id)
                    val filename = getFileNameFromUri(uri!!)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_FILE, filename)
                    isSmoothScrollToPosition = true
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        refUser.removeEventListener(listenerInfoToolbar)
        refMessages.removeEventListener(messagesListener)
    }
    //Функция удаляет view после закрытия
    override fun onDestroyView() {
        super.onDestroyView()
        appVoiceRecord.releaseRecord()
        adapter.destroy()
    }

    //Функция создает выпадющее меню
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_actions_menu, menu)
    }

    //Функция отрабатывает при выходе и меняет статус слушает пункты меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_clear_chat -> clearChat(contact.id) {
                showToast("Чат очищен!")
                replaceFragment(MainListFragment())
            }
            R.id.menu_delete_chat -> deleteChat(contact.id) {
                showToast("Чат удален!")
                replaceFragment(MainListFragment())
            }
        }
        return true
    }
}