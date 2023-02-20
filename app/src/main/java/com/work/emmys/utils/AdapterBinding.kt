package com.work.emmys.utils

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.work.emmys.data.models.InvoiceResponse

/**
 * Created by PaL on 22,January,2023
 */
object AdapterBinding {

    @SuppressLint("SetTextI18n")
    @JvmStatic
    @BindingAdapter("setAmount")
    fun setAmount(textView: TextView,amount:String?){
        textView.text= "$$amount"
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("setSenderAddress")
    fun setSenderAddress(textView: TextView,invoice: InvoiceResponse.Response?){
        textView.text= "${invoice?.sender?.address?.apartment}\n${invoice?.sender?.address?.address1}\n${invoice?.sender?.address?.city},${invoice?.sender?.address?.state},${invoice?.sender?.address?.zipcode}\n${invoice?.sender?.phone1}"
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("setReceiverAddress")
    fun setReceiverAddress(textView: TextView,invoice: InvoiceResponse.Response?){
        textView.text= "${invoice?.receiver?.address?.apartment}\n${invoice?.receiver?.address?.address1}\n${invoice?.receiver?.address?.city},${invoice?.receiver?.address?.state}\n${invoice?.receiver?.phone1}"
    }

}