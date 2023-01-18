package com.work.emmys.models
import android.util.Patterns

class SignInModel {
    private var strEmailAddress: String? = null
    private var strPassword: String? = null

    constructor(EmailAddress: String?, Password: String?) {
        strEmailAddress = EmailAddress
        strPassword = Password
    }

    fun getStrEmailAddress(): String? {
        return strEmailAddress
    }

    fun getStrPassword(): String? {
        return strPassword
    }

    fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(getStrEmailAddress()).matches()
    }


    fun isPasswordLengthGreaterThan5(): Boolean {
        return getStrPassword()!!.length > 5
    }
}