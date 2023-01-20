package com.work.emmys.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.work.emmys.R
import com.work.emmys.databinding.ItemInvoiceRowBinding

/**
 * Created by PaL on 19,January,2023
 */
class AdapterInvoiceList : RecyclerView.Adapter<AdapterInvoiceList.Holder>() {

    class Holder(binding:ItemInvoiceRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_invoice_row,parent,false))
    }

    override fun getItemCount(): Int {
        return 5
//        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        TODO("Not yet implemented")
    }
}