package com.example.telegram.database

import com.example.telegram.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

// Переменные которые ссылаются на обьекты к базе данных
lateinit var AUTH: FirebaseAuth
lateinit var UID: String
lateinit var REF_DATA_BASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User

// Тип отправленного сообщения
const val TYPE_TEXT = "text"
const val TYPE_MESSAGE_IMAGE = "image"
const val TYPE_MESSAGE_VOICE = "voice"
const val TYPE_MESSAGE_FILE = "file"

// ссылка на картинку
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSAGE_IMAGE = "message_image"
const val FOLDER_FILES = "files"

// ноды которые создаются в виде таблиц в базе данных
const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"

// Дети нод ввиде столбцов в базе данных
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

//Message
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"

