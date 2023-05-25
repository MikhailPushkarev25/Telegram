package com.example.telegram.ui.screense.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.database.*
import com.example.telegram.databinding.FragmentAddContactsBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.AppValueEventListener
import com.example.telegram.utilits.hideKeyBoard

class AddContactsFragment : Fragment() {

    private lateinit var chat: FragmentAddContactsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AddContactsAdapter
    private val refMainList = REF_DATA_BASE_ROOT.child(NODE_MAIN_LIST).child(UID)
    private val refUsers = REF_DATA_BASE_ROOT.child(NODE_USERS)
    private val refMessages = REF_DATA_BASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var listItems = listOf<CommonModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chat = FragmentAddContactsBinding.inflate(inflater, container, false)
        return chat.root
    }
    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Добавить участника"
        APP_ACTIVITY.appDrawer.enableDriver()
        hideKeyBoard()
        initRecycleView()
        chat.addContactsBtnNext.setOnClickListener {
            listContacts.forEach{
                println(it.id)
            }
        }
    }

    //Инициализация обьектов и выполнение запросов по разным нодам для вывода на главный экран
    private fun initRecycleView() {
        recyclerView = chat.addContactsRecycleView
        adapter = AddContactsAdapter()
        //Первый запрос идет по всем детям ноды main_list создает мапу и трансформирует все в модельку и в list
        refMainList.addListenerForSingleValueEvent(AppValueEventListener{ list ->
            listItems = list.children.map { it.getCommonModel() }
            //Идем по листу по всем элементам
            listItems.forEach { model ->
                //Деламем второй запрос к user и его id слушаем один раз и переводим всех детей в модель
                refUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener{ user ->
                    val newModel = user.getCommonModel()
                    //Делаем третий запрос к message к его id и берем самое последнее сообщение
                    refMessages.child(model.id)
                        .limitToLast(1)
                        .addListenerForSingleValueEvent(AppValueEventListener{ messageElem ->
                            val tempList = messageElem.children.map { it.getCommonModel() }
                            if (tempList.isEmpty()) {
                                newModel.lastMessage = "Чат очищен"
                            } else {
                                newModel.lastMessage = tempList[0].text
                            }
                            if (newModel.fullname.isEmpty()) {
                                newModel.fullname = newModel.phone
                            }
                            adapter.updateListItems(newModel)
                        })
                })
            }
        })

        recyclerView.adapter = adapter
    }

    companion object {
        val listContacts = mutableListOf<CommonModel>()
    }

}