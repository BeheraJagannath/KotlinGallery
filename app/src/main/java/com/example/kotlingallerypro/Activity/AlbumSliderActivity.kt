package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
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
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ToxicBakery.viewpager.transforms.DrawerTransformer
import com.bumptech.glide.Glide
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.Preference.PreferenceManager
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.AlbumImageSliderModel
import com.example.kotlingallerypro.modelclass.AlbumPictureModel
import java.io.File
import java.text.CharacterIterator
import java.text.Format
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*

class AlbumSliderActivity : AppCompatActivity() {
    companion object{


        lateinit var allImage: ArrayList < AlbumPictureModel >
        lateinit var  list: ArrayList<Uri>
        var position = 0
        var preferenceManager: PreferenceManager? = null
        lateinit var  imagesPager :ImagePager
        private var dHelper: DbHelper? = null

        private val imageIds: ArrayList<Int> = ArrayList()
         lateinit var imageView: ImageView
        lateinit var imageback: ImageView
        lateinit var image_title: TextView
        lateinit var image_like: ImageView
        lateinit var image_info: ImageView
        lateinit var image_fav: ImageView
        lateinit var image_edit: ImageView
        lateinit var image_delete: ImageView
        lateinit var image_share: ImageView

        //delete
        var deletePosition = 0

        var imageID: String? = null

        fun getPos(): Int {
            return deletePosition
        }

        @JvmName("getImageID1")
        fun getImageID(): String? {
            return imageID
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

    lateinit var viewPager: ViewPager
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_slider)
        viewPager = findViewById(R.id.albumViewPager)
        imageback = findViewById(R.id.image_back)
        image_title = findViewById(R.id.image_title)
        image_like = findViewById(R.id.image_like)
        image_info = findViewById(R.id.image_info)
        image_fav = findViewById(R.id.image_fav)
        image_edit = findViewById(R.id.image_edit)
        image_delete = findViewById(R.id.image_delete)
        image_share = findViewById(R.id.image_share)



        val albumImageSliderModel: AlbumImageSliderModel? = intent.getSerializableExtra("key") as AlbumImageSliderModel?
        allImage = albumImageSliderModel!!.albumPictureModelList as ArrayList<AlbumPictureModel>
        position = albumImageSliderModel.position

        dHelper = DbHelper(this, null)


        imageback.setOnClickListener{
            finish()
        }
        slider()

    }


    private fun slider() {
        imagesPager = ImagePager()
        viewPager.adapter = imagesPager
        viewPager.setPageTransformer(true, DrawerTransformer() as ViewPager.PageTransformer?)
        viewPager.currentItem = position
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            @SuppressLint("NewApi")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                if (position < imagesPager.count - 1 && position < allImage.size - 1) {
                    image_title . text =  allImage [position].pictureName

                    if (dHelper!!.getStatuss(java.lang.String.valueOf( allImage[position].pictureId ))) {
                        image_like.setImageResource(R.drawable.ic_liked)
                        image_fav.setImageResource(R.drawable.ic_liked)
                    } else {
                        image_like.setImageResource( R.drawable.ic_like_24 )
                        image_fav.setImageResource( R.drawable.ic_like_24 )
                    }

                    image_like.setOnClickListener(View.OnClickListener {
                        likealbum(position)

                    })

                    image_fav.setOnClickListener(View.OnClickListener {
                        likealbum(position)

                    })

                    image_info.setOnClickListener(View.OnClickListener {
                        showImageInfo(allImage[position])
                    })

                    image_delete.setOnClickListener(View.OnClickListener {

                        val file = File(allImage[position].picturePath)
                        val i = position
                        showDeleteDialog(file, i)

                    })

                    image_share.setOnClickListener(View.OnClickListener {

                        val i = position
                        shareImage( i)
                    })

                    image_edit.setOnClickListener {
                        val intent = Intent(this@AlbumSliderActivity, EditActivity::class.java)
                        intent.putExtra("img", allImage[position].picturePath)
                        startActivity(intent)

                    }
                }else{

                    image_title.text = allImage.get(allImage.size - 1).pictureName
                    if (dHelper!!.getStatuss(java.lang.String.valueOf(allImage[allImage.size - 1].pictureId))) {
                        image_like.setImageResource(R.drawable.ic_liked)
                        image_fav.setImageResource(R.drawable.ic_liked)
                    } else {
                        image_like.setImageResource(R.drawable.ic_like_24)
                        image_fav.setImageResource(R.drawable.ic_like_24)
                    }


                    image_like.setOnClickListener(View.OnClickListener {
                        likealbum(allImage.size-1)
                    })

                    image_fav.setOnClickListener(View.OnClickListener {
                        likealbum(allImage.size-1)
                    })

                    image_info.setOnClickListener(View.OnClickListener {
                        showImageInfo(allImage[allImage.size-1])
                    })
                    image_delete.setOnClickListener(View.OnClickListener {

                        val file = File(allImage[allImage.size - 1].picturePath)
                        val i = allImage.size - 1
                        showDeleteDialog(file, i)

                    })
                    image_share.setOnClickListener(View.OnClickListener {
                        val i = allImage.size-1
                        shareImage( i)
                    })
                    image_edit.setOnClickListener {
                        val intent = Intent(this@AlbumSliderActivity, EditActivity::class.java)
                        intent.putExtra("img", allImage[allImage.size - 1].picturePath)

                        startActivity(intent)

                    }

                }


            }
            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


    }

    @SuppressLint("NewApi")
    private fun likealbum(position: Int) {
        if ( dHelper!!.getStatuss(java.lang.String.valueOf ( allImage[position].pictureId ))) {
            image_like.setImageResource( R.drawable.ic_like_24 )
            image_fav.setImageResource( R.drawable.ic_like_24 )
            dHelper!!.removeFav ( java.lang.String.valueOf( allImage[position].pictureId ))
        } else {
            image_like.setImageResource( R.drawable.ic_liked )
            image_fav.setImageResource( R.drawable.ic_liked )
            dHelper!!.setFav(java.lang.String.valueOf( allImage[position].pictureId ))
        }

    }

    private fun showDeleteDialog(file: File, i: Int) {
        val dialog = Dialog(this, R.style.Dialog_Theme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
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
                    Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
                    allImage.removeAt(i)
                    this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(file)))
                    imagesPager.notifyDataSetChanged()
                    if (allImage.isEmpty()) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    //                    callBroadCast();
                } else {
                    Toast.makeText(this, "Image Not Deleted", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }




        private fun shareImage( i: Int) {
            val path: String
            val file: File
            val share = Intent(Intent.ACTION_SEND)
            share.type = "image/jpeg"
            val uri: Uri
            path = allImage[position].picturePath
            file = File(path)
            uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "share via"))



        }




    private fun showImageInfo(albumPictureModel : AlbumPictureModel) {

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
            albumPictureModel.pictureSize.toLong())
        tvImagePath.text = albumPictureModel.picturePath
        tvImageName.text = albumPictureModel.pictureName
        tvImageResolution.text = albumPictureModel.imageHeightWidth
        tvDateModified.text = convertTimeDateModified(albumPictureModel.dateModified)
        tvDateTaken.text = convertTimeDateTaken(albumPictureModel.dateTaken)
        Log.d("showImageInfo", """
     showImageInfo: ${albumPictureModel.dateTaken}
     ${convertTimeDateModified(albumPictureModel.dateModified)}
     ${convertTimeDateTaken(albumPictureModel.dateTaken)}
     """.trimIndent())

        button.setOnClickListener { alertDialog.dismiss() }

        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()

    }


    class ImagePager : PagerAdapter() {
        override fun getCount(): Int {
            return allImage!!.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = layoutInflater.inflate(R.layout.image_slider, null)
            imageView = view.findViewById<ImageView>(R.id.Image_slider)
            val albumPictureModel: AlbumPictureModel = allImage.get(position)
                        Glide.with(container.getContext()).load(albumPictureModel.getPicturePath()).into(imageView)
//            Picasso.get().load(File(albumPictureModel.picturePath))
//                .into(imageView)


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



}




