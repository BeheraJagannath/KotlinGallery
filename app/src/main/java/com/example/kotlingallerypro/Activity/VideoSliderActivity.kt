package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.IntentSender
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ToxicBakery.viewpager.transforms.DrawerTransformer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlingallerypro.Database.DbHelper
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.databinding.ActivityVideoSliderBinding
import com.example.kotlingallerypro.modelclass.*
import java.io.File
import java.lang.String
import java.text.CharacterIterator
import java.text.Format
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.Date
import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.arrayOf


class VideoSliderActivity : AppCompatActivity() {
    companion object{
        lateinit var videoSliderBinding: ActivityVideoSliderBinding
        var allVideoList = ArrayList<Videomodel>()
        var position = -1
        private var Helper: DbHelper? = null
        lateinit var videoPager: VideoPager

        lateinit var imageView:ImageView

    }
    var mAdCount = 0
    var deletePosition = 0
    private lateinit var imageID: String

    fun getPos(): Int {
        return deletePosition
    }

    fun getImageID(): String {
        return imageID
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        videoSliderBinding = DataBindingUtil.setContentView(this ,R.layout.activity_video_slider)

        Helper = DbHelper(this, null)

        val videosliderModel: VideosliderModel? = intent.getSerializableExtra("key") as VideosliderModel?
        if (videosliderModel != null){
            allVideoList = videosliderModel.allImagesModelList as ArrayList<Videomodel>
            position = videosliderModel.position

        }

        videoslider()

    }
    private fun videoslider() {
        videoPager = VideoPager()
        videoSliderBinding.videoViewpager.adapter =  videoPager
        videoSliderBinding.videoViewpager.setPageTransformer(true, DrawerTransformer() as ViewPager.PageTransformer?)
        videoSliderBinding.videoViewpager.currentItem =  position
        videoSliderBinding.videoViewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            @SuppressLint("NewApi")
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position < videoPager.count - 1 && position < allVideoList.size - 1) {
                    videoSliderBinding.videoTitle.text = allVideoList [position].getTitle()

                    if (Helper!!.getStatuss(String.valueOf(allVideoList [position].id))) {
                        videoSliderBinding.videoLike.setImageResource(R.drawable.ic_liked)
                        videoSliderBinding.videoFav.setImageResource(R.drawable.ic_liked)
                    } else {
                        videoSliderBinding.videoLike.setImageResource(R.drawable.ic_like_24)
                        videoSliderBinding.videoFav .setImageResource(R.drawable.ic_like_24)
                    }

                    videoSliderBinding.videoLike.setOnClickListener {
                        likeVideo(position)
                    }

                    videoSliderBinding.videoFav.setOnClickListener {
                        likeVideo(position)
                    }

                    videoSliderBinding.videoShare.setOnClickListener  {
                        shareVideo(position)
                    }
                    videoSliderBinding.videoDelete.setOnClickListener {
                        val file = File(allVideoList[position].path)
                        val i = position
                        showDeleteDialog(allVideoList[position].path, allVideoList [ position] .columnId, i,
                            String.valueOf(allVideoList[position].id))

                    }
                    videoSliderBinding.videoInfo.setOnClickListener {
                        showInfoDialog ( position )
                    }

                }else{
                    videoSliderBinding.videoTitle.text =   allVideoList [allVideoList.size - 1].title

                    if (Helper!!.getStatuss(String.valueOf(allVideoList [allVideoList.size - 1].getId()))) {
                        videoSliderBinding.videoLike.setImageResource(R.drawable.ic_liked)
                        videoSliderBinding.videoFav.setImageResource(R.drawable.ic_liked)
                    } else {
                        videoSliderBinding.videoLike.setImageResource(R.drawable.ic_like_24)
                        videoSliderBinding.videoFav.setImageResource(R.drawable.ic_like_24)
                    }

                    videoSliderBinding.videoLike.setOnClickListener {
                        likeVideo(allVideoList.size - 1)
                    }

                    videoSliderBinding.videoFav.setOnClickListener {
                        likeVideo(allVideoList.size - 1)
                    }

                    videoSliderBinding.videoShare.setOnClickListener {
                        shareVideo(allVideoList.size - 1)
                    }

                    videoSliderBinding.videoDelete.setOnClickListener {
                        val file = File(allVideoList[allVideoList.size - 1].path)
                        val i = allVideoList.size - 1
                        showDeleteDialog(
                            allVideoList[allVideoList.size - 1].path, allVideoList [allVideoList.size - 1].columnId,
                            i, String.valueOf(allVideoList [allVideoList.size - 1].id))

                    }
                    videoSliderBinding .videoInfo.setOnClickListener {
                        showInfoDialog ( allVideoList.size - 1 )

                    }
                }

                }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })



        videoSliderBinding.backImageView.setOnClickListener {
            finish()
        }

    }


    private fun showInfoDialog(i: Int) {
        val allVideoModel: Videomodel = allVideoList[i]
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.video_details_dialog, null)
        builder.setView(view)
        val alertDialog = builder.create()
        val button: Button = view.findViewById(R.id.btn_details_ok)
        val tvImageSize = view.findViewById<TextView>(R.id.tv_image_size)
        val tvImagePath = view.findViewById<TextView>(R.id.tv_image_path)
        val tvImageName = view.findViewById<TextView>(R.id.tv_image_name)
        val tvDateModified = view.findViewById<TextView>(R.id.tv_date_taken)
        val imageresolution = view.findViewById<TextView>(R.id.iv_image_resolution)
//        tvImageSize.text = humanReadableByteCountSI(allVideoModel.size.toLong())
        tvImagePath.text = allVideoModel.path
        tvImageName.text = allVideoModel.title
        tvDateModified.text = convertTimeDateModified(allVideoModel.getDateAdded().toLong())
        button.setOnClickListener { alertDialog.dismiss() }

        imageresolution .isVisible = false
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }    fun convertTimeDateModified(time: Long): kotlin.String? {
        val date = Date(time * 1000)
        @SuppressLint("SimpleDateFormat") val format: Format =
            SimpleDateFormat("dd.MM.yyyy , HH:mm:aa")
        return format.format(date)
    }
    fun humanReadableByteCountSI(bytes: Long): kotlin.String? {
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

    @SuppressLint("NewApi")
    private fun likeVideo(i: Int) {
        if (Helper!!.getStatuss(String.valueOf(allVideoList[position].id))) {
            videoSliderBinding.videoLike.setImageResource(R.drawable.ic_like_24)
            videoSliderBinding.videoFav.setImageResource(R.drawable.ic_like_24)
            Helper!!.removeFav(String.valueOf(allVideoList[position].id ))
        } else {
            videoSliderBinding.videoLike.setImageResource(R.drawable.ic_liked)
            videoSliderBinding.videoFav.setImageResource(R.drawable.ic_liked)
            Helper!!.setFav(String.valueOf(allVideoList[position].id))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun showDeleteDialog(file: kotlin.String?, columnId: kotlin.String?, j: Int, imageId: kotlin.String) {
        val dialog = Dialog(this, R.style.Dialog_Theme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

        val delete = dialog.findViewById<Button>(R.id.btn_delete)
        delete.setOnClickListener { v: View? ->
            val uris = arrayOf(
                Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    columnId
                )
            )
            try {
                delete(this, uris, 105, imageId, j)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
            }

            dialog.dismiss()
        }
        val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        cancel.setOnClickListener { v: View? -> dialog.dismiss() }
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.P)
    fun delete(activity: Activity, uriList: Array<Uri?>, requestCode: Int, imageId: kotlin.String, i: Int) {
        val resolver = activity.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            this.imageID = imageId
            this.deletePosition = i
            val list: List<Uri> = ArrayList()
//            Collections.addAll(list, *uriList)
            val pendingIntent = MediaStore.createDeleteRequest(resolver, list)
            activity.startIntentSenderForResult(pendingIntent.intentSender, requestCode, null, 0, 0, 0, null
            )
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {
                for (uri in uriList) {
                    resolver.delete(uri!!, null, null)
                }
//                Helper?.removeFav(imageId)
                Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show()
                allVideoList.removeAt(i)
                videoPager.notifyDataSetChanged()
            } catch (ex: RecoverableSecurityException) {
//                this.imageID = imageId
                this.deletePosition = i
                val intent = ex.userAction
                    .actionIntent
                    .intentSender
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null)
            }
        } else {
            for (uri in uriList) {
                resolver.delete(uri!!, null, null)
            }
//            Helper?.removeFav(imageId)
            Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show()
            allVideoList.removeAt(i)
            videoPager.notifyDataSetChanged()
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 105) {
            if (resultCode == RESULT_OK) {
//                Helper?.removeFav(getImageID().toString())
                Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show()
                allVideoList.removeAt(getPos())
                videoPager.notifyDataSetChanged()
            }
        }
    }


    private fun shareVideo(i: Int) {
        val path: kotlin.String
        val file: File
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/jpeg"
        val uri: Uri
        path = allVideoList[position].path
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
    class VideoPager : PagerAdapter() {
        override fun getCount(): Int {
            return allVideoList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                container.context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.video_slider_item, null)
            imageView = view.findViewById(R.id.iv_video_slider)
            val allVideoModel: Videomodel = allVideoList[position]
            //            ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,Integer.parseInt(videoFolderList.get(position).id)
            Glide.with(container.context).load(allVideoModel.getPath())
                .apply(RequestOptions().fitCenter())
                .into(imageView)
//
            imageView.setOnClickListener {

                val intent = Intent(container.context, VideoPlayerActivity::class.java)
//                bundle.putSerializable("path", allVideoModel as Serializable)
                intent.putExtra("path", allVideoModel)
                VideoSliderActivity.position = position
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                ContextCompat.startActivity(container.context, intent, null)
            }

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


