package com.example.telegram.database

import android.net.Uri
import android.provider.Contacts.Intents.UI
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.models.User
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.AppValueEventListener
import com.example.telegram.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.HashMap

// Инициализация самой базы данных
fun initFireBase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATA_BASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

// Функциональный метод, который добавляет вместо картинки ссыллку на нее
inline fun putUrlToDataBase(url: String, crossinline function: () -> Unit) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Функциональный метод который забирает ссылку картинки, для вывода пользователю
inline fun getUrlFromStorage(paf: StorageReference, crossinline function: (uri: String) -> Unit) {
    paf.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Функциональный метод, который берет картинку, превращает ее в ссылку и добавляет
inline fun putFileToStorage(uri: Uri, paf: StorageReference, crossinline function: () -> Unit) {
    paf.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Функциональный метод, инициализирует user
inline fun initUser(crossinline function: () -> Unit) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()) {
                USER.username = UID
            }
            function()
        })
}

// Функция добавляет номер телефона и id в базу данных
fun updatePhonesToDataBase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATA_BASE_ROOT.child(NODE_PHONES)
            .addListenerForSingleValueEvent(AppValueEventListener { snapshot ->
                snapshot.children.forEach { dataSnapshot ->
                    arrayContacts.forEach { contact ->
                        if (dataSnapshot.key == contact.phone) {
                            REF_DATA_BASE_ROOT
                                .child(NODE_PHONES_CONTACTS)
                                .child(UID)
                                .child(dataSnapshot.value.toString())
                                .child(CHILD_ID)
                                .setValue(dataSnapshot.value.toString())
                                .addOnFailureListener { showToast(it.message.toString()) }

                            REF_DATA_BASE_ROOT
                                .child(NODE_PHONES_CONTACTS)
                                .child(UID)
                                .child(dataSnapshot.value.toString())
                                .child(CHILD_FULLNAME)
                                .setValue(contact.fullname)
                                .addOnFailureListener { showToast(it.message.toString()) }

                        }
                    }
                }
            })
    }
}

// Функция берет данные из базы и передает пользователю
fun DataSnapshot.getCommonModel(): CommonModel = this.getValue(CommonModel::class.java) ?: CommonModel()

// Функция берет данные из базы и передает пользователю
fun DataSnapshot.getUserModel(): User = this.getValue(User::class.java) ?: User()

// Функция создает ноды в базе для хранения сообщений, эти данные кладутся в мапу, далее по ключу выводятся пользователю
fun sendMessage(message: String, receivingUserId: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$UID"
    val messageKey = REF_DATA_BASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATA_BASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

//Изменение имени в настройках
fun changeUserName(newUserName: String) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERNAMES)
        .child(newUserName)
        .setValue(UID)
        .addOnSuccessListener {
            updateCurrentUsername(newUserName)
        }
}

//Изменение текущего имени в настройках
fun updateCurrentUsername(newUserName: String) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnSuccessListener {
            showToast("Имя изменено!")
            deleteOldUsername(newUserName)
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

//Затирание старого имени в базе
fun deleteOldUsername(newUserName: String) {
    REF_DATA_BASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
            showToast("Имя изменено!")
            APP_ACTIVITY.supportFragmentManager.popBackStack()
            USER.username = newUserName
        }
        .addOnFailureListener {
            showToast(it.message.toString())
        }
}

// Функция добавляет описание в настроках
fun setBioToDataBase(newBio: String) {
    REF_DATA_BASE_ROOT.child(NODE_USERS)
        .child(UID)
        .child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener{
                showToast("Описание добавлено!")
                USER.bio = newBio
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

//Функция добавляет имя в тулбаре
fun setNameToDataBase(fullname: String) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener{
                showToast(APP_ACTIVITY.getString(R.string.settings_toast_FIO))
                USER.fullname = fullname
                APP_ACTIVITY.appDrawer.updateHeader()
                APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

//Функция берет ссылку из базы и скачивает пользователю в чат
fun sendMessageAsFile(receivingUserId: String, fileUrl: String, messageKey: String, typeMessage: String, filename: String) {
    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserId"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserId/$UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATA_BASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

//Функция возвращает ключ ноды
fun getMessageKey(id: String) = REF_DATA_BASE_ROOT.child(NODE_MESSAGES).child(UID)
    .child(id).push().key.toString()

// Функция сохраняет аудио файл по окончании записи
fun uploadFileToStorage(uri: Uri, messageKey: String, receivingId: String, typeMessage: String, filename: String = "") {
    val paf = REF_STORAGE_ROOT
        .child(FOLDER_FILES)
        .child(messageKey)
    putFileToStorage(uri, paf) {
        getUrlFromStorage(paf) {
            sendMessageAsFile(receivingId, it, messageKey, typeMessage, filename)
        }
    }
}

//Функция берет файл из базы данных
fun getFileFromStorage(file: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(file)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

//Функция выводит чат на главный экран активити
fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_MAIN_LIST/$UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = UID
    mapReceived[CHILD_TYPE] = type

    val commonMap = hashMapOf<String, Any>()

    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATA_BASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

//Функция Удаляет из базы данных ноды собеседника и свой чат
fun deleteChat(id: String, function: () -> Unit) {
    //Ищем ноду с id и удаляем в main_list
    REF_DATA_BASE_ROOT
        .child(NODE_MAIN_LIST)
        .child(UID)
        .child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener { function() }
}
//Функция очищает из базы данных ноды собеседника и свой чат
fun clearChat(id: String, function: () -> Unit) {

    // Сначала удаляем из своего чата
    REF_DATA_BASE_ROOT
        .child(NODE_MESSAGES)
        .child(UID)
        .child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        //Здесь удаляем зеркально из чата собеседника
        .addOnSuccessListener {
            REF_DATA_BASE_ROOT
            .child(NODE_MESSAGES)
            .child(id).child(UID)
                .removeValue()
                .addOnSuccessListener { function() }}
            .addOnFailureListener { showToast(it.message.toString()) }
}

//Функция создает ноды в базе данных и отправляет данные пользователю
fun createGroupToDB(
    nameGroup: String,
    uri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val keyGroup = REF_DATA_BASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATA_BASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathImage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"

    val mapMembers = hashMapOf<String, Any>()
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER
    }
    mapMembers[UID] = USER_CREATOR

    mapData[NODE_MEMBERS] = mapMembers

    path.updateChildren(mapData)
        .addOnSuccessListener {
            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathImage) {
                    getUrlFromStorage(pathImage) { it ->
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupsToMainList(mapData, listContacts) {
                            function()
                        }
                    }
                }
            } else {
                addGroupsToMainList(mapData, listContacts) {
                    function()
                }
            }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}
//Функция добавляет всю информацию о группах в главный лист
fun addGroupsToMainList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
  val path = REF_DATA_BASE_ROOT.child(NODE_MAIN_LIST)
    val map = hashMapOf<String, Any>()
    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    path.child(UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Функция создает ноды в базе для хранения групп, эти данные кладутся в мапу, далее по ключу выводятся пользователю
fun sendMessageToGroup(message: String, groupId: String, typeText: String, function: () -> Unit) {

    val refMessages = "$NODE_GROUPS/$groupId/$NODE_MESSAGES"
    val messageKey = REF_DATA_BASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    REF_DATA_BASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

// Функция сохраняет аудио файл по окончании записи
fun uploadFileToStorageToGroup(uri: Uri, messageKey: String, receivingId: String, typeGroups: String, filename: String = "") {
    val paf = REF_STORAGE_ROOT
        .child(FOLDER_FILES)
        .child(messageKey)
    putFileToStorage(uri, paf) {
        getUrlFromStorage(paf) {
            sendMessageAsFileToGroups(receivingId, it, messageKey, typeGroups, filename)
        }
    }
}

//Функция берет ссылку из базы и скачивает пользователю в группу
fun sendMessageAsFileToGroups(receivingUserId: String, fileUrl: String, messageKey: String, typeGroups: String, filename: String) {
    val refDialogUser = "$NODE_GROUPS/$receivingUserId/$NODE_MESSAGES"
    val messageKey = REF_DATA_BASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeGroups
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    REF_DATA_BASE_ROOT.child(refDialogUser).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnFailureListener { showToast(it.message.toString()) }
}

//Функция возвращает ключ ноды
fun getGroupKey(id: String) = REF_DATA_BASE_ROOT.child(NODE_GROUPS).child(UID)
    .child(id).push().key.toString()