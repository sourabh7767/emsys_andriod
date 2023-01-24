package com.work.emmys.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.work.emmys.R
import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.databinding.ItemInvoiceRowBinding
import com.work.emmys.utils.Utils

/**
 * Created by PaL on 19,January,2023
 */
class AdapterInvoiceList(var listData: List<InvoiceResponse.Response>) :
    RecyclerView.Adapter<AdapterInvoiceList.Holder>() {

    inner class Holder(val binding: ItemInvoiceRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
     /*   fun bind() {
            binding.invoie = listData[adapterPosition]
        }*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_invoice_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return  listData.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.invoie = listData[position]
        val color = Utils.getRandomColor()
//        holder.binding.tvHead.backgroundTintList =ColorStateList.valueOf(Color.parseColor(color))
        Log.e("cccc","${Utils.generateRandomColor()}")

        holder.binding.civ.setImageDrawable(ColorDrawable(Utils.getRandomColor()))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(response: List<InvoiceResponse.Response>) {
        listData = response
        notifyDataSetChanged()
    }
}