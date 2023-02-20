package com.work.emmys.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.work.emmys.R
import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.databinding.ItemInvoiceDetailBinding
import com.work.emmys.utils.AdapterBinding

/**
 * Created by PaL on 15,February,2023
 */
class AdapterInvoiceDetails(private val list : List<InvoiceResponse.Response.InvoiceDetail>):RecyclerView.Adapter<AdapterInvoiceDetails.Holder>(){

    class Holder(val binding:ItemInvoiceDetailBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_invoice_detail,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
//        holder.binding.invoice = list[position]
        AdapterBinding.setAmount(holder.binding.tvPrice,list[position].price.toString())
        AdapterBinding.setAmount(holder.binding.tvTotal,list[position].total.toString())
                holder.binding.tvDescription.text = list[position].name ?: ""

    }

}
