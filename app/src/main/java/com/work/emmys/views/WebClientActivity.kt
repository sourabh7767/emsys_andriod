package com.work.emmys.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.work.emmys.R
import com.work.emmys.databinding.ActivityWebClientBinding
import java.io.File
import java.io.FileOutputStream


class WebClientActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWebClientBinding
    private var bitmap:Bitmap? = null
    private var pdfName:String=""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_client)

//        val pdfUrl = intent?.getStringExtra("Pdf")

        val byteArray = intent.getByteArrayExtra("image")
        pdfName= intent.getStringExtra("invoice") ?: ""
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

        binding.image.setImageBitmap(bitmap!!)

//        Log.e("pdfff","bbbbb$pdfUrl")

        val printManager = getSystemService(PRINT_SERVICE) as PrintManager
        val printAdapter: PrintDocumentAdapter = if (Build.VERSION.SDK_INT >= 21) {
            binding.webClient.createPrintDocumentAdapter("invoice")
        } else {
            binding.webClient.createPrintDocumentAdapter()
        }
        val builder = PrintAttributes.Builder()
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//        val filePdf: File = File(pdfUrl)
//        printManager.print(filePdf.name, printAdapter, builder.build())

        binding.webClient.settings.javaScriptEnabled = true
//        binding.webClient.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url="+Uri.parse(pdfUrl).path.toString())

        binding.btnShare.setOnClickListener {
            var pdfUri:Uri? =null
            val intentShareFile = Intent(Intent.ACTION_SEND)
            val uri= convertBitmapToPdf(bitmap!!,this)
//            val fileWithinMyDir: File = File(uri.path)
            val filePath = File(getExternalFilesDir(null), "$pdfName.pdf")
//            val uri = FileProvider.getUriForFile(
//                this,applicationContext.packageName + ".provider",
//                filePath)

            pdfUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, this.packageName + ".provider", filePath)
            } else {
                Uri.fromFile(filePath)
            }
            if (filePath.exists()) {
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri)
                intentShareFile.type = "application/pdf"

//                intentShareFile.setDataAndType(pdfUri, "application/pdf")

                intentShareFile.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Sharing File..."
                )
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                startActivity(Intent.createChooser(intentShareFile, "Share File"))
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    private fun convertBitmapToPdf(bitmap: Bitmap, context: Context):Uri {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0F, 0F, null)
        pdfDocument.finishPage(page)
        val filePath = File(context.getExternalFilesDir(null), "$pdfName.pdf")
        pdfDocument.writeTo(FileOutputStream(filePath))
        pdfDocument.close()
        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            filePath)
//        renderPdf(context, filePath)

        return uri
    }

}