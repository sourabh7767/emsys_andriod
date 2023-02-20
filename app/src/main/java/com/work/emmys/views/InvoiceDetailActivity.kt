package com.work.emmys.views

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.work.emmys.R
import com.work.emmys.adapters.AdapterInvoiceDetails
import com.work.emmys.data.models.InvoiceResponse
import com.work.emmys.databinding.ActivityInvoiceDetailBinding
import com.work.emmys.databinding.LayoutPdfPageBinding
import com.work.emmys.utils.AdapterBinding
import com.work.emmys.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class InvoiceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceDetailBinding
    private var invoice: InvoiceResponse.Response? = null
    private var bitmap:Bitmap?=null
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_detail)

        init()
        setListener()
    }

    private fun init() {
        val dataString = intent?.getStringExtra(Constants.DATA)
        invoice = Gson().fromJson(dataString, InvoiceResponse.Response::class.java)
        binding.data = invoice

        binding.rvInvoiceList.adapter = AdapterInvoiceDetails(invoice?.invoiceDetails ?: emptyList())
    }

    private fun showProgress(){
        try {
            progressDialog = ProgressDialog.show(
                this, "", resources.getString(R.string.please_wait), true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListener() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivPrint.setOnClickListener {
            showProgress()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                createPdf()
            },2000)
//            printPdf()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createPdf() {
        val inflater = LayoutInflater.from(this)
        val binding: LayoutPdfPageBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_pdf_page, null, false)

        binding.data = invoice
        binding.tvInvoiceNo.text ="No ${invoice?.number}"
        AdapterBinding.setAmount(binding.tvCost,invoice?.cost)
        AdapterBinding.setAmount(binding.tvPayment, invoice?.invoiceDetails?.get(0)?.price.toString())
        AdapterBinding.setAmount(binding.tvBalance,invoice?.balance.toString())
//        AdapterBinding.setAmount(binding.tvPrice,invoice?.invoiceDetails?.get(0)?.price.toString())
//        AdapterBinding.setAmount(binding.tvTotal,invoice?.invoiceDetails?.get(0)?.total.toString())
        binding.tvContainer.text = invoice?.container?.name
        binding.tvDate.text = invoice?.date
        binding.tvSender.text = invoice?.sender?.name
        binding.tvSenderAddress.text = "${invoice?.sender?.address?.apartment}\n${invoice?.sender?.address?.address1}\n${invoice?.sender?.address?.city},${invoice?.sender?.address?.state},${invoice?.sender?.address?.zipcode}\n${invoice?.sender?.phone1}"

        binding.tvSender.text = invoice?.receiver?.name
        binding.tvSenderAddress.text = "${invoice?.receiver?.address?.apartment}\n${invoice?.receiver?.address?.address1}\n${invoice?.receiver?.address?.city},${invoice?.receiver?.address?.state}\n${invoice?.receiver?.phone1}"

//        binding.tvDescription.text = invoice?.invoiceDetails?.get(0)?.name ?: ""
//        binding.tvDescription.text = invoice?.invoiceDetails?.get(0)?.name ?: ""
//        binding.tvDescription.text = invoice?.invoiceDetails?.get(0)?.name ?: ""

        binding.rvInvoiceList.adapter = AdapterInvoiceDetails(invoice?.invoiceDetails ?: emptyList())

        val decodedString: ByteArray = Base64.decode(Constants.Base64Sign, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.ivSign.setImageBitmap(decodedByte)

         bitmap =
            createBitmapFromView(binding.root/*context, view, pdfDetails, adapter, activity*/)

        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)

        val byteArray: ByteArray = stream.toByteArray()
        val in1 = Intent(this, WebClientActivity::class.java)
        in1.putExtra("image", byteArray)
        in1.putExtra("invoice",invoice?.number.toString())

        progressDialog.dismiss()
        startActivity(in1)
//        convertBitmapToPdf(bitmap!!, this)
    }

    private fun createBitmapFromView(root: View): Bitmap {
        return createBitmap(this, root, this)
    }

    private fun createBitmap(
        context: Context,
        view: View,
        activity: Activity,
    ): Bitmap {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return Bitmap.createScaledBitmap(bitmap, 640, view.measuredHeight, true)
    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "${invoice?.number}.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        renderPdf(context, filePath)
    }

    private fun printPdf(){
        val pfdDocument = PdfDocument()
        val paint = Paint()
        val pageInfo = PdfDocument.PageInfo.Builder(250,350,1).create()
        val page = pfdDocument.startPage(pageInfo)
        val canvas= page.canvas
        paint.textSize= 15f
        paint.color = Color.rgb(0,50,250)
        canvas.drawText("Invoice no. ${invoice?.number}",70f,20f,paint)

        paint.textSize= 15f
        canvas.drawText("Emsys",0f,0f,paint)

        pfdDocument.finishPage(page)
        val filePath = File(getExternalFilesDir(null), "invoice.pdf")
        pfdDocument.writeTo(FileOutputStream(filePath))
        pfdDocument.close()

        renderPdf(this, filePath)
    }

    private fun renderPdf(context: Context, filePath: File) {
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath)

//        startActivity(Intent(this,WebClientActivity::class.java)
//            .putExtra("Pdf",uri.toString())
//            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION))

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri, "application/pdf")

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
        }


    }

}