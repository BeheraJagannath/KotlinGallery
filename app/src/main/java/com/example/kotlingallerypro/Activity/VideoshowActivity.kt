package com.example.kotlingallerypro.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.RecoverableSecurityException
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlingallerypro.Adapters.VideoshowAdapter
import com.example.kotlingallerypro.Interface.VideoClicklistener
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.databinding.ActivityVideoshowBinding
import com.example.kotlingallerypro.modelclass.Videomodel
import com.example.kotlingallerypro.modelclass.VideosliderModel
import java.io.File
import java.util.*

class VideoshowActivity : AppCompatActivity() ,VideoClicklistener {
    companion object{
        lateinit var videobinding: ActivityVideoshowBinding
        lateinit var videoshowAdapter: VideoshowAdapter

        var videoshow: ArrayList<Videomodel> = ArrayList<Videomodel>()
        private var action : Boolean?=false


        private val deletePosition: MutableList<Int?> = java.util.ArrayList()

        private fun getDeletePosition(): List<Int?> {
            return deletePosition
        }


    }





    private var name: String? = null
    private var folderTitle: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videobinding = DataBindingUtil.setContentView(this ,R.layout.activity_videoshow)


        name = intent.getStringExtra("folderName")
        folderTitle = intent.getStringExtra("name")

       videobinding.xTitle.setText(folderTitle)
        videobinding.ivBk.setOnClickListener{
            finish()
        }
        videobinding.mShare.setOnClickListener {
            shareImages()

        }
        videobinding.mDelete.setOnClickListener {
            deleteVideo()
        }
        videobinding.mBack.setOnClickListener {
            onBackPressed()
        }

        loadVideos()
    }




    @SuppressLint("NewApi")
    private fun loadVideos() {
        videoshow = name?.let { getallVideoFromFolder(this, it) }!!
        if (name != null && videoshow.size > 0) {
            videoshowAdapter = VideoshowAdapter(videoshow, this,this)
            //if your recyclerview lagging then just add this line
            videobinding.videoshowRecycler.setHasFixedSize(true)
            videobinding.videoshowRecycler.setItemViewCacheSize(20)
            videobinding.videoshowRecycler.setDrawingCacheEnabled(true)
            videobinding.videoshowRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)
            videobinding.videoshowRecycler.setNestedScrollingEnabled(false)
            videobinding.videoshowRecycler.setAdapter(videoshowAdapter)
            videobinding.videoshowRecycler.setLayoutManager(GridLayoutManager(this, Utils.COLUMN_TYPE))
        } else {
            Toast.makeText(this, "can't find any videos", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun getallVideoFromFolder(
        context: Context, name: String,
    ): java.util.ArrayList<Videomodel>? {
        val list = java.util.ArrayList<Videomodel>()
        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Video.Media.DATE_ADDED
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.RESOLUTION,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATE_ADDED,
            BaseColumns._ID
        )
        val selection = MediaStore.Video.Media.DATA + " like?"
        val selectionArgs = arrayOf("%$name%")
        val cursor =
            context.contentResolver.query(uri, projection, selection, selectionArgs, orderBy)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val path = cursor.getString(1)
                val title = cursor.getString(2)
                val size = cursor.getInt(3)
                val resolution = cursor.getString(4)
                val duration = cursor.getInt(5)
                val disName = cursor.getString(6)
                val bucket_display_name = cursor.getString(7)
                val width_height = cursor.getString(8)
                val mimeType = cursor.getString(9)
                val dateAdded = cursor.getString(10)
                val columnId  = cursor .getString(11)



                //this method convert 1024 in 1MB
                var human_can_read: String? = null
                human_can_read = if (size < 1024) {
                    String.format(context.getString(R.string.size_in_b), size.toDouble())
                } else if (size < Math.pow(1024.0, 2.0)) {
                    String.format(context.getString(R.string.size_in_kb), (size / 1024).toDouble())
                } else if (size < Math.pow(1024.0, 3.0)) {
                    String.format(context.getString(R.string.size_in_mb),
                        size / Math.pow(1024.0, 2.0))
                } else {
                    String.format(context.getString(R.string.size_in_gb),
                        size / Math.pow(1024.0, 3.0))
                }

                //this method convert any random video duration like 1331533132 into 1:21:12
                var duration_formatted: String
                val sec = duration / 1000 % 60
                val min = duration / (1000 * 60) % 60
                val hrs = duration / (1000 * 60 * 60)
                duration_formatted = if (hrs == 0) {
                    min.toString() + ":" + String.format(Locale.UK, "%02d", sec)
                } else {
                    hrs.toString() + ":" + String.format(Locale.UK,
                        "%02d",
                        min) + ":" + String.format(
                        Locale.UK, "%02d", sec)
                }
                val files = Videomodel(id, path, title, human_can_read, resolution, duration_formatted,
                    disName, width_height, resolution, bucket_display_name, width_height, mimeType, dateAdded,columnId)
                if ( name.endsWith(bucket_display_name)) list.add( files )
            }
            cursor.close()
        }
        return list
    }


    override fun OnVideoClick(holder: VideoshowAdapter.ViewViewHolders?, video: List<Videomodel?>?, position: Int, ) {
        if (videoshowAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            val videosliderModel = VideosliderModel(video, position)
            @SuppressLint("NewApi", "LocalSuppress") val intent = Intent(this, VideoSliderActivity::class.java)
            intent.putExtra("key", videosliderModel)
            startActivity(intent)



        }
    }

    override fun OnVideoLongClick(v: View?, file: File?, position: Int) {
        enableActionMode(position)

    }
    private fun enableActionMode(position: Int) {

        videobinding.multiple.visibility = View.VISIBLE

        videobinding.appbar.visibility = View.GONE
        action = true

        toggleSelection(position)
    }
    private fun toggleSelection(position: Int) {
        videoshowAdapter.toggleSelection(position)
        val count: Int = videoshowAdapter.getSelectedItemCount()
        val totalImage = videoshowAdapter.itemCount
        if (count == 0) {
            videobinding.multiple.visibility = View.GONE
            videobinding.appbar.visibility = View.VISIBLE
        } else {
            videobinding.mCount.text = ("$count / $totalImage")

        }
    }
    override fun onBackPressed() {

        if (action!!){
            closeactionmode()
            videoshowAdapter.clearSelections()
            videoshowAdapter.clearList()
        }else{
            finish()
        }
    }

    private fun closeactionmode() {
        videobinding.multiple.visibility = View.GONE

        videobinding.appbar.visibility = View.VISIBLE

    }
    private fun shareImages() {

        val sharePath = videoshowAdapter.getDeleteItems()
        val files = java.util.ArrayList<Uri>()

        for (s in sharePath.values) {
            val file = File(s)
            var uri: Uri
            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.provider", file)
            } else {
                Uri.fromFile(file)
            }
            files.add(uri)
        }
        val intent = Intent()
        intent.action = Intent.ACTION_SEND_MULTIPLE
        intent.type = "image/*"
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        startActivity(Intent.createChooser(intent, "share via"))
        onBackPressed()


    }

    private fun getContentUri(imageUri: Uri): String {
        var id: Long = 0
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val cursor =
            this.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.MediaColumns.DATA + "=?", arrayOf(imageUri.path), null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            }
            cursor.close()
        }
        return id.toString()
    }

    private fun deleteVideo() {
        val dialog = Dialog(this, R.style.Dialog_Theme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        val delete = dialog.findViewById<Button>(R.id.btn_delete)
        delete.setOnClickListener {
            val deletePath: HashMap<Int, String> = videoshowAdapter.getDeleteItems()
            val uriList: MutableList<Uri> = ArrayList()
            for (i in deletePath.keys) {
                val path = deletePath[i]
                val contentUri = getContentUri(Uri.parse(path))
                if (path != null) {
                    uriList.add(Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        contentUri))
                    deletePosition.add(i)
                }
            }
            try {
                deleteMultipleVideo(this, uriList, 158)
            } catch (e: SendIntentException) {
                e.printStackTrace()
            }
            dialog.dismiss()
            onBackPressed()

        }
        val cancel = dialog.findViewById<Button>(R.id.btn_cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
            onBackPressed()

        }
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
    }

    @SuppressLint("NewApi")
    @Throws(SendIntentException::class)
    private fun deleteMultipleVideo(activity: Activity, uriList: List<Uri>, requestCode: Int) {
        val contentResolver = activity.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList)
            activity.startIntentSenderForResult ( pendingIntent.intentSender,
                requestCode, null, 0, 0, 0, null )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                for (uri in uriList) {
                    contentResolver.delete(uri, null, null)
                }
                for (j in getDeletePosition()) {
                    videoshowAdapter.removeData(j!!)
                }
                videoshowAdapter.notifyDataSetChanged()
                deletePosition.clear()
                Toast.makeText(this, "Videos deleted", Toast.LENGTH_SHORT).show()
            } catch ( e: RecoverableSecurityException) {
                val intent = e.userAction.actionIntent.intentSender
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null)
            }
        } else {
            for (uri in uriList) {
                contentResolver.delete(uri, null, null)
            }
            for (k in getDeletePosition()) {
                videoshowAdapter.removeData(k!!)
            }
            videoshowAdapter.notifyDataSetChanged()
            deletePosition.clear()
            Toast.makeText(this, "Videos deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 158) {
            if (resultCode == RESULT_OK) {
                for (i in getDeletePosition()) {
                    videoshowAdapter.removeData(i!!)
                }
                videoshowAdapter.notifyDataSetChanged()
            }
            deletePosition.clear()
        }
    }




}