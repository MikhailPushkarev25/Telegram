package com.example.telegram.utilits

enum class AppStates(val state: String) {

    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TIPING("печатает");

    companion object {
        //Функция принимает состояние и записывает в базу данных
        fun updateState(appStates: AppStates) {
            if (AUTH.currentUser != null) {
                REF_DATA_BASE_ROOT
                    .child(NODE_USERS)
                    .child(UID)
                    .child(CHILD_STATE)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.state = appStates.state }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}