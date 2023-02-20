package com.work.emmys.utils

/**
 * Created by PaL on 22,January,2023
 */
object Constants {
    const val AUTH_TOKEN="auth"
    const val UID="uid"
    const val APP_ID="app_id"
    const val APP_KEY="app_key"
    const val DARk_MODE="dark"
    const val LANGUAGE="language"
    const val USERNAME="user"
    const val DATA = "data"

    const val Base64Sign="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"
}