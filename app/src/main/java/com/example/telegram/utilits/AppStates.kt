package com.example.telegram.utilits

enum class AppStates(val state: String) {

    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TIPING("печатает");

    companion object {
        fun updateState(appStates: AppStates) {
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