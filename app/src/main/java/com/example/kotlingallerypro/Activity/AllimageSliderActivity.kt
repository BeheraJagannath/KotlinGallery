package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ToxicBakery.viewpager.transforms.DrawerTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.Preference.PreferenceManager
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.databinding.ActivityAllimageSliderBinding
import com.example.kotlingallerypro.modelclass.AllImagesModel
import com.example.kotlingallerypro.modelclass.ImageSliderModel
import java.io.File
import java.text.CharacterIterator
import java.text.Format
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*
import kotlin.collections.ArrayList

class AllimageSliderActivity : AppCompatActivity() {
    companion object{
        lateinit var imageSliderBinding: ActivityAllimageSliderBinding
        lateinit var allImage: ArrayList <AllImagesModel>
        var preferenceManager: PreferenceManager? = null
        lateinit var dHelper: DbHelper



        private var position = 0

        lateinit var  imagesPager : ImagesPager

        private val imageIds: List<Int> = ArrayList()
        lateinit var imageView: ImageView

    }


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageSliderBinding=DataBindingUtil. setContentView(this ,R.layout.activity_allimage_slider)



        val imageSliderMode: ImageSliderModel? = intent.getSerializableExtra("key") as ImageSliderModel?

        allImage = imageSliderMode!!.allImagesModelList as ArrayList<AllImagesModel>
        position = imageSliderMode.position

        imageSliderBinding.imageBack.setOnClickListener{
            finish()
        }




        slider()
        dHelper = DbHelper(this,null)


    }

    private fun slider() {
        imagesPager = ImagesPager()
        imageSliderBinding . imageViewPager.adapter = imagesPager
        imageSliderBinding.imageViewPager.setPageTransformer(true, DrawerTransformer() as ViewPager.PageTransformer?)
        imageSliderBinding.imageViewPager.currentItem = position
        imageSliderBinding.imageViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("NewApi")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position < imagesPager.count - 1 && position < allImage.size - 1) {
                    imageSliderBinding.imageTitle.text = allImage[position].name

                    if (dHelper.getStatuss(java.lang.String.valueOf(allImage[position].id))) {
                        imageSliderBinding.imageLike.setImageResource(R.drawable.ic_liked)
                        imageSliderBinding.imageFav.setImageResource(R.drawable.ic_liked)
                    } else {
                        imageSliderBinding.imageLike.setImageResource( R.drawable.ic_like_24 )
                        imageSliderBinding.imageFav.setImageResource( R.drawable.ic_like_24 )
                    }
                    imageSliderBinding.imageLike.setOnClickListener {
                        likeimage(position)

                    }
                    imageSliderBinding.imageFav.setOnClickListener {
                        likeimage(position)

                    }


                    imageSliderBinding.imageDelete.setOnClickListener {
                        val file = File(allImage[position].path)
                        Log.d("Image File Path", "onClick: " + file.path)
                        showDeleteDialog(file, position)
                        imagesPager.notifyDataSetChanged()

                    }
                    imageSliderBinding.imageShare.setOnClickListener {
                        shareImage(position)

                    }
                    imageSliderBinding.imageInfo.setOnClickListener(View.OnClickListener {
                        showImageInfo(allImage[position])
                    })
                    imageSliderBinding.imageEdit.setOnClickListener {
                        val intent = Intent(this@AllimageSliderActivity, EditActivity::class.java)
                        intent.putExtra("img", allImage[position].path)
                        startActivity(intent)
                    }

                }else{
                    imageSliderBinding.imageTitle.text = allImage[allImage.size-1].name

                    if (dHelper .getStatuss(java.lang.String.valueOf(allImage[allImage.size - 1].getId()))) {
                        imageSliderBinding.imageLike.setImageResource(R.drawable.ic_liked)
                        imageSliderBinding.imageFav.setImageResource(R.drawable.ic_liked)
                    } else {
                        imageSliderBinding.imageLike.setImageResource(R.drawable.ic_like_24)
                        imageSliderBinding.imageFav.setImageResource(R.drawable.ic_like_24)
                    }
//
                    imageSliderBinding.imageLike.setOnClickListener {
                        likeimage(allImage.size - 1)
                    }

                    imageSliderBinding.imageFav.setOnClickListener {
                        likeimage(allImage.size - 1)
                    }

                    imageSliderBinding.imageDelete.setOnClickListener {
                        val file = File(allImage[allImage.size - 1].path)
                        Log.d("Image File Path", "onClick: " + file.path)
                        showDeleteDialog(file, allImage.size - 1)
                    }
                    imageSliderBinding.imageShare.setOnClickListener {
                        
                        shareImage(allImage.size - 1)

                    }
                    imageSliderBinding.imageInfo.setOnClickListener {
                        showImageInfo(allImage[allImage.size - 1])

                    }
                    imageSliderBinding.imageEdit.setOnClickListener {
                        val intent = Intent(this@AllimageSliderActivity, EditActivity::class.java)
                        intent.putExtra("img", allImage [allImage.size-1] .path )
                        startActivity(intent)
                    }

                }


            }
            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private fun showImageInfo(allImagesModel: AllImagesModel) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.details_dialog, null)
        builder.setView(view)

        val alertDialog = builder.create()
        val button = view.findViewById<Button>(R.id.btn_details_ok)
        val tvImageSize = view.findViewById<TextView>(R.id.tv_image_size)
        val tvImagePath = view.findViewById<TextView>(R.id.tv_image_path)
        val tvImageName = view.findViewById<TextView>(R.id.tv_image_name)
        val tvImageResolution = view.findViewById<TextView>(R.id.iv_image_resolution)
        val tvDateTaken = view.findViewById<TextView>(R.id.tv_date_taken)
        val tvDateModified = view.findViewById<TextView>(R.id.tv_date_modified)

        tvImageSize.text = humanReadableByteCountSI(
            allImagesModel.pictureSize.toLong())
        tvImagePath.text = allImagesModel.path
        tvImageName.text = allImagesModel.name
        tvImageResolution.text = allImagesModel.imageHeightWidth
        tvDateModified.text =
            convertTimeDateModified(allImagesModel.dateModified)
        tvDateTaken.text = convertTimeDateTaken(allImagesModel.dateTaken)
        Log.d("showImageInfo", """
     showImageInfo: ${allImagesModel.dateTaken}
     ${convertTimeDateModified(allImagesModel.dateModified)}
     ${convertTimeDateTaken(allImagesModel.dateTaken)}
     """.trimIndent())

        button.setOnClickListener { alertDialog.dismiss() }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()


    }

    @SuppressLint("NewApi")
    private fun likeimage(position: Int) {
        if (dHelper .getStatuss(java.lang.String.valueOf ( allImage[position].id  ))) {
            imageSliderBinding.imageLike.setImageResource( R.drawable.ic_like_24 )
            imageSliderBinding.imageFav.setImageResource( R.drawable.ic_like_24 )
            dHelper .removeFav(java.lang.String.valueOf( allImage[position].id ) )
        } else {
            imageSliderBinding.imageLike.setImageResource( R.drawable.ic_liked )
            imageSliderBinding.imageFav.setImageResource( R.drawable.ic_liked )
            dHelper .setFav(java.lang.String.valueOf( allImage[position].id ))
        }

    }



    private fun shareImage(position: Int) {
        val path: String
        val uri: Uri
        val file: File
        path = allImage[position].path
        file = File(path)

        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "$packageName.provider", file)
        } else {
            Uri.fromFile(file)
        }


        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "share via"))

    }

    private fun showDeleteDialog(file: File, i: Int) {
        val dialog = Dialog(this, R.style.Dialog_Theme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window !!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        val delete = dialog.findViewById<TextView>(R.id.btn_delete)
        delete.setOnClickListener {
            deletePhoto(file, i)
            dialog.dismiss()
        }
        val cancel = dialog.findViewById<TextView>(R.id.btn_cancel)
        cancel.setOnClickListener { dialog.dismiss() }


        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }

        dialog.show()


    }

    private fun deletePhoto(file: File, i: Int) {
        if (file.exists()) {
            try {
                val delete = file.delete()
                if (delete) {
                    Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT)
                        .show()
                    allImage.removeAt(i)
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                    imagesPager.notifyDataSetChanged()
                    if (allImage.isEmpty()) {
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Image Not Deleted", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

    }


    class ImagesPager : PagerAdapter() {
        override fun getCount(): Int {
            return allImage.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = layoutInflater.inflate(R.layout.image_slider, null)
            imageView = view.findViewById(R.id.Image_slider)


//            final AlbumPictureModel albumPictureModel = allImage.get(position);
            val allImagesModel = allImage[position]
            Glide.with(container.context).load(allImagesModel.path)
                .apply(RequestOptions().fitCenter()).into(imageView)
            //            Picasso.get().load(new File(allImagesModel.getPath())).placeholder(R.drawable.no_image_bg).into(zoomageView);
            (container as ViewPager).addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }
    }
    fun humanReadableByteCountSI(bytes: Long): String? {
        var bytes = bytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    }

    fun convertTimeDateModified(time: Long): String {
        val date = Date(time * 1000)
        @SuppressLint("SimpleDateFormat") val format: Format =
            SimpleDateFormat("dd.MM.yyyy , HH:mm:aa")
        return format.format(date)
    }

    fun convertTimeDateTaken(time: Long): String {
        val date = Date(time)
        @SuppressLint("SimpleDateFormat") val format: Format =
            SimpleDateFormat("dd.MM.yyyy , HH:mm:aa")
        return format.format(date)
    }



}