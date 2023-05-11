package com.example.telegram.utilits

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.example.telegram.models.CommonModel
import com.example.telegram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var UID: String
lateinit var REF_DATA_BASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User

const val FOLDER_PROFILE_IMAGE = "profile_image"

const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

fun initFireBase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATA_BASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDataBase(url: String, crossinline function: () -> Unit) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .child(CHILD_PHOTO_URL)
        .setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(paf: StorageReference, crossinline function: (uri: String) -> Unit) {
    paf.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, paf: StorageReference, crossinline function: () -> Unit) {
    paf.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATA_BASE_ROOT
        .child(NODE_USERS)
        .child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener{
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

fun DataSnapshot.getCommonModel(): CommonModel = this.getValue(CommonModel::class.java) ?: CommonModel()

fun DataSnapshot.getUserModel(): User = this.getValue(User::class.java) ?: User()
