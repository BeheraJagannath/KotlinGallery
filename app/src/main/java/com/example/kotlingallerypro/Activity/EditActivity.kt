package com.example.kotlingallerypro.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.MediaScannerConnectionClient
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.kotlingallerypro.Adapters.ImageFilterAdapter
import com.example.kotlingallerypro.Interface.FilterListener
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.databinding.ActivityEditBinding


import com.isseiaoki.simplecropview.CropImageView
import net.alhazmy13.imagefilter.ImageFilter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
class EditActivity : AppCompatActivity() , FilterListener {
    lateinit var editBinding: ActivityEditBinding
    private var msConn: MediaScannerConnection? = null
    private var wasDrawCanvasPositioned = false
    var uri: Uri? = null
    lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       editBinding = DataBindingUtil. setContentView(this ,R.layout.activity_edit)
        val uri : Uri


        val imgPath = intent.getStringExtra("img")

        uri = Uri.fromFile(File(imgPath))

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        setupAspectRatioButtons()

        editBinding.cropimageView.setImageBitmap(bitmap)
        editBinding.editBack.setOnClickListener {
            finish()
        }
        editBinding.crop.setOnClickListener {

//            editBinding.cropOpLayout.visibility = View.VISIBLE
            editBinding.croplay .visibility = View.VISIBLE
            editBinding.linearRecycler.visibility = View. GONE
            editBinding.imageDrawer.visibility =View .GONE

        }
        editBinding.similarImage.setOnClickListener {
            editBinding.croplay.visibility = View.GONE
            editBinding.cropOpLayout.visibility = View.GONE
            editBinding.linearRecycler.visibility = View. VISIBLE
            editBinding. imageDrawer.visibility = View.GONE

            setFilterRecycler()

        }

    }

    override fun onResume() {
        super.onResume()
        setFilterRecycler()
    }

    private fun setFilterRecycler() {
        val allImageFilter: MutableList<ImageFilter.Filter?> = ArrayList()
        allImageFilter.add(null)
        allImageFilter.add(ImageFilter.Filter.GRAY)
        allImageFilter.add(ImageFilter.Filter.RELIEF)
        allImageFilter.add(ImageFilter.Filter.AVERAGE_BLUR)
        allImageFilter.add(ImageFilter.Filter.OIL)
        allImageFilter.add(ImageFilter.Filter.NEON)
        allImageFilter.add(ImageFilter.Filter.PIXELATE)
        allImageFilter.add(ImageFilter.Filter.TV)
        allImageFilter.add(ImageFilter.Filter.INVERT)
        allImageFilter.add(ImageFilter.Filter.BLOCK)
        allImageFilter.add(ImageFilter.Filter.OLD)
        allImageFilter.add(ImageFilter.Filter.SHARPEN)
        allImageFilter.add(ImageFilter.Filter.LIGHT)
        allImageFilter.add(ImageFilter.Filter.LOMO)
        allImageFilter.add(ImageFilter.Filter.HDR)
        allImageFilter.add(ImageFilter.Filter.GAUSSIAN_BLUR)
        allImageFilter.add(ImageFilter.Filter.SOFT_GLOW)
        allImageFilter.add(ImageFilter.Filter.SKETCH)
        allImageFilter.add(ImageFilter.Filter.MOTION_BLUR)
        allImageFilter.add(ImageFilter.Filter.GOTHAM)

        val horizontalLayoutManagaer =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        editBinding.filterRecycler.setLayoutManager(horizontalLayoutManagaer)

        val imageFilterAdapter = ImageFilterAdapter(this, allImageFilter, bitmap,this)
        editBinding.filterRecycler.adapter = imageFilterAdapter

        bitmap = getResizedBitmap(bitmap, 200)

    }
    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun setupAspectRatioButtons() {

        editBinding.ivCrop.setOnClickListener {
            if (editBinding.cropOpLayout.visibility== View.VISIBLE) {
                editBinding.cropOpLayout.visibility = View.GONE
                editBinding.ivCrop.setColorFilter(ContextCompat.getColor(this@EditActivity, R.color.textcolor),
                    PorterDuff.Mode.SRC_IN)
            } else {
                editBinding.cropOpLayout.visibility = View.VISIBLE
                editBinding.ivCrop.setColorFilter(ContextCompat.getColor(this@EditActivity, R.color.teal_200),
                    PorterDuff.Mode.SRC_IN)
            }

        }
        editBinding.ivCustomCrop.setOnClickListener {
            editBinding.cropimageView.setCropMode(CropImageView.CropMode.CUSTOM)


        }
        editBinding.ivRoate.setOnClickListener {
            editBinding . cropimageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);

        }
        editBinding.ivLeftRight.setOnClickListener {

            val drawable =   editBinding.cropimageView.drawable as BitmapDrawable
            val bitmap = drawable.bitmap

            val matrix = Matrix()
            matrix.preScale(-1.0f, 1.0f)
            val bOutput = Bitmap.createBitmap(bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true)
            editBinding.cropimageView.imageBitmap = bOutput

        }
        editBinding.ivTopBottom.setOnClickListener {
            val drawable = editBinding.cropimageView.drawable as BitmapDrawable

            val bitmap = drawable.bitmap

            val matrix = Matrix()
            matrix.preScale(1.0f, -1.0f)
            val bOutput = Bitmap.createBitmap(bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true)
            editBinding.cropimageView.imageBitmap = bOutput

        }
        editBinding.tvCropFree.setOnClickListener{
            editBinding.tvCropFree.background = resources.getDrawable(R.drawable.crop_image_item)
            editBinding.tvCropFree.setTextColor(ContextCompat.getColor(this@EditActivity,
                R.color.white))
            editBinding.tvCrop11.background = null
            editBinding.tvCrop11.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop43.background = null
            editBinding.tvCrop43.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop169.background = null
            editBinding.tvCrop169.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCropOther.background = null
            editBinding.tvCropOther.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))


            editBinding.cropimageView.setCropMode(CropImageView.CropMode.FREE)

        }
        editBinding.tvCrop11.setOnClickListener{
            editBinding.tvCropFree.background = null
            editBinding.tvCropFree.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop11.background = resources.getDrawable(R.drawable.crop_image_item)
            editBinding.tvCrop11.setTextColor(ContextCompat.getColor(this@EditActivity,
                R.color.white))
            editBinding.tvCrop43.background = null
            editBinding.tvCrop43.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop169.background = null
            editBinding.tvCrop169.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCropOther.background = null
            editBinding.tvCropOther.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))


            editBinding.cropimageView.setCropMode(CropImageView.CropMode.SQUARE)

        }
        editBinding.tvCrop43.setOnClickListener{
            editBinding.tvCropFree.background = null
            editBinding.tvCropFree.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop11.background = null
            editBinding.tvCrop11.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop43.background = resources.getDrawable(R.drawable.crop_image_item)
            editBinding.tvCrop43.setTextColor(ContextCompat.getColor(this@EditActivity,
                R.color.white))
            editBinding.tvCrop169.background = null
            editBinding.tvCrop169.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCropOther.background = null
            editBinding.tvCropOther.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))


            editBinding.cropimageView.setCropMode(CropImageView.CropMode.RATIO_4_3)

        }
        editBinding.tvCrop169.setOnClickListener {
            editBinding.tvCropFree.background = null
            editBinding.tvCropFree.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop11.background = null
            editBinding.tvCrop11.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop43.background = null
            editBinding.tvCrop43.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop169.background = resources.getDrawable(R.drawable.crop_image_item)
            editBinding.tvCrop169.setTextColor(ContextCompat.getColor(this@EditActivity,
                R.color.white))
            editBinding.tvCropOther.background = null
            editBinding.tvCropOther.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))


            editBinding.cropimageView.setCropMode(CropImageView.CropMode.RATIO_16_9)


        }
        editBinding.tvCropOther.setOnClickListener {
            editBinding.tvCropFree.background = null
            editBinding.tvCropFree.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop11.background = null
            editBinding.tvCrop11.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop43.background = null
            editBinding.tvCrop43.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCrop169.background = null
            editBinding.tvCrop169.setTextColor(ContextCompat.getColor(this@EditActivity, R.color.textcolor))
            editBinding.tvCropOther.background = resources.getDrawable(R.drawable.crop_image_item)
            editBinding.tvCropOther.setTextColor(ContextCompat.getColor(this@EditActivity,
                R.color.white))
            slideShowPopUp()

            editBinding.cropimageView.setCropMode(CropImageView.CropMode.CUSTOM)

        }
        editBinding.done.setOnClickListener{

            saveimgae(bitmap = editBinding.cropimageView.croppedBitmap)

        }

    }
    private fun slideShowPopUp() {
        val radio1 = arrayOf(resources.getString(R.string.one_one))
        val radio2 = arrayOf(resources.getString(R.string.two_two))
        val radio3 = arrayOf(resources.getString(R.string.three_three))
        val radio4 = arrayOf(resources.getString(R.string.four_four))
        val radio5 = arrayOf(resources.getString(R.string.five_five))

        val dial = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dial.requestWindowFeature(1)
//        dial.setContentView(R.layout.slideshow_dialog)
        dial.window!!.setBackgroundDrawable(ColorDrawable(0))
        dial.setCanceledOnTouchOutside(true)
    }

    private fun saveimgae(bitmap: Bitmap?) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File(root + "/" + getString(R.string.app_name))
        val mySubDir = File(myDir.absolutePath + "/.temp")
        val file1 = File(mySubDir, "image_temp.jpeg")
        if (!myDir.exists()) Log.d("Data", "data: myDir " + myDir.mkdirs())
        if (file1.exists()) file1.delete()
        val file = File(myDir, System.currentTimeMillis().toString() + ".jpeg")
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)

            out.flush()
            out.close()
            scanPhoto(file.toString())
            Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    fun scanPhoto(imageFileName: String?) {
        msConn = MediaScannerConnection(this, object : MediaScannerConnectionClient {
            override fun onMediaScannerConnected() {
                msConn?.scanFile(imageFileName, null)
                Log.i("msClient obj", "connection established")
            }

            override fun onScanCompleted(path: String, uri: Uri) {
                msConn?.disconnect()
                Log.i("msClient obj", "scan completed")
            }
        })
        this.msConn!!.connect()
    }

    override fun onFilterClick(filter: ImageFilter.Filter?) {
        editBinding.cropimageView.setImageBitmap(null)
        if (filter == null) {
            editBinding.cropimageView.setImageBitmap(bitmap)
        } else {
            editBinding.cropimageView.setImageBitmap(ImageFilter.applyFilter(bitmap, filter))
        }
    }
}
