package com.example.kotlingallerypro.Activity
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.RecoverableSecurityException
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.kotlingallerypro.Adapters.AlbumshowAdapter
import com.example.kotlingallerypro.Interface.SelectedVideoItem

import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.databinding.ActivityImageshowBinding
import com.example.kotlingallerypro.modelclass.AlbumPictureModel
import com.example.kotlingallerypro.modelclass.AlbumImageSliderModel
import java.io.File

class AlbumshowActivity : AppCompatActivity(),SelectedVideoItem {
    companion object{
        lateinit var imageshowbinding : ActivityImageshowBinding

         var folderPath: String? = null
         var folderName: String? = null
         @SuppressLint("StaticFieldLeak")
         lateinit var adapter: AlbumshowAdapter
         lateinit var allPictures: ArrayList<AlbumPictureModel>

          private var actionMode: ActionMode? = null
          var action : Boolean?=false

//         lateinit var actionModeCallback: ActionModeCallback
         lateinit var dialog  : Dialog

              private val deletePosition: MutableList<Int?> = ArrayList()

              private fun getDeletePosition(): List<Int?> {
               return deletePosition
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageshowbinding =DataBindingUtil. setContentView(this , R.layout.activity_imageshow)

//        actionModeCallback = ActionModeCallback(this,contentResolver)

        folderPath = intent.getStringExtra("folderPath")
        folderName = intent.getStringExtra("folderName")

        imageshowbinding.xTitle.text = folderName
        imageshowbinding.ivBk.setOnClickListener {
            finish()
        }
        imageshowbinding.mBack.setOnClickListener {
            onBackPressed()
        }
        imageshowbinding.mShare.setOnClickListener {
            shareImages()

        }
        imageshowbinding.mDelete.setOnClickListener {
            deleteImages()

        }

    }
    override fun onResume() {
        super.onResume()
        allPictures = ArrayList()
        allPictures = getAllImageByFolder(folderPath.toString())
        imageshowbinding.imageshowRecycler.layoutManager = GridLayoutManager(this, Utils.COLUMN_TYPE)
        adapter = AlbumshowAdapter(allPictures, this,this)

        imageshowbinding.imageshowRecycler.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }

    fun getAllImageByFolder(path: String): ArrayList<AlbumPictureModel> {
        var images :ArrayList<AlbumPictureModel> = ArrayList<AlbumPictureModel>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.SIZE,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
            MediaStore.Images.ImageColumns.DATE_MODIFIED)
        val cursor = this.contentResolver.query(uri,
            projection,
            MediaStore.Images.ImageColumns.DATA + " like ?",
            arrayOf(
                "%$path%"),
            null)
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                val pic = AlbumPictureModel()
                pic.pictureId =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID))
                        .toInt()
                pic.pictureName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME))
                pic.picturePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA))
                pic.pictureSize =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE))
                pic.imageHeightWidth =
                    (cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)) + " x "
                            + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)))
                try {
                    pic.dateTaken =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN))
                            .toLong()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

//            pic.setDateModified(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED))));
                images.add(pic)

            }
            cursor.close()

        }
        val reSelection : ArrayList<AlbumPictureModel> = ArrayList<AlbumPictureModel>()
        for (i in images.size - 1 downTo -1 + 1) {
            reSelection.add(images[i])
        }
        images = reSelection
        for (i in images.indices) {
            Log.d("******", """getAllImageByFolder: ${images[i].pictureId}
 ${images[i].pictureId} path : ${images[i].picturePath}""")
        }
        return images
    }

    override fun onPicClicked(holder: AlbumshowAdapter.PictViewHolder?, position: Int, pics: List<AlbumPictureModel?>?) {
        if (adapter.getSelectedItemCount() > 0) {
            enableActionMode(position)
        } else {
            val albumImageSliderModel = AlbumImageSliderModel(pics, position)
            @SuppressLint("NewApi", "LocalSuppress") val intent = Intent(this, AlbumSliderActivity::class.java)
            intent.putExtra("key", albumImageSliderModel)
            startActivity(intent)
        }

    }


    override fun onItemLongClick(view: View?, obj: AlbumPictureModel?, pos: Int) {

        enableActionMode(pos)

    }
    private fun enableActionMode(position: Int) {

            imageshowbinding.multiple.visibility = View.VISIBLE

            imageshowbinding.appbar.visibility = View.GONE
            action = true

        toggleSelection(position)
    }
    private fun toggleSelection(position: Int) {
        adapter.toggleSelection(position)
        val count: Int = adapter.getSelectedItemCount()
        val totalImage = adapter.itemCount
        if (count == 0) {
            imageshowbinding.multiple.visibility = View.GONE
            imageshowbinding.appbar.visibility = View.VISIBLE
        } else {
            imageshowbinding .mCount.text = ("$count / $totalImage")

        }
    }

    override fun onBackPressed() {

        if (action!!){
            closeactionmode()
            adapter.clearSelections()
            adapter.clearList()
        }else{
            finish()
        }
    }

    private fun closeactionmode() {
        imageshowbinding.multiple.visibility = View.GONE

        imageshowbinding.appbar.visibility = View.VISIBLE

    }
    private fun shareImages() {

            val sharePath = adapter.getDeleteItems()
            val files = ArrayList<Uri>()

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
    private fun deleteImages() {

        val dialog = Dialog(this, R.style.Dialog_Theme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)

        val delete = dialog.findViewById<Button>(R.id.btn_delete)
        delete.setOnClickListener {
            val deletePath = adapter.getDeleteItems()
            val uriList: MutableList<Uri> =
                java.util.ArrayList()
            for (i in deletePath.keys) {
                val path = deletePath[i]
                val contentUri: String = getContentUri(Uri.parse(path))
                if (path != null) {
                    uriList.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentUri))
                    deletePosition.add(i)
                }
            }
            try {
                delete(this, uriList, 105, null, 0)
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
    private fun getContentUri(imageUri: Uri): String {
        var id: Long = 0
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val cursor =
            this.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
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
    @SuppressLint("NewApi")
    @Throws(SecurityException::class, SendIntentException::class, IllegalArgumentException::class)
    fun delete(activity: Activity, uriList: List<Uri?>, requestCode: Int, imageId: String?, i: Int ) {
        val resolver = activity.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pendingIntent = MediaStore.createDeleteRequest(resolver, uriList)
            activity.startIntentSenderForResult(pendingIntent.intentSender,
                requestCode, null, 0, 0, 0, null)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            try {
                for (uri in uriList) {
                    resolver.delete(uri!!, null, null)
                }
                for (j in getDeletePosition()) {
                    adapter.removeData(i)
                    adapter.notifyItemRemoved(i)
                }
                adapter.notifyDataSetChanged()
                deletePosition.clear()
                Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
            } catch ( ex: RecoverableSecurityException) {
                val intent = ex.userAction
                    .actionIntent
                    .intentSender
                activity.startIntentSenderForResult(intent, requestCode, null, 0, 0, 0, null)
            }
        } else {
            for (uri in uriList) {
                resolver.delete(uri!!, null, null)
            }
            for (k in getDeletePosition()) {
                adapter.removeData(k!!)
                adapter.notifyItemRemoved(k)
            }
            adapter.notifyDataSetChanged()
            deletePosition.clear()
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 105) {
            if (resultCode == RESULT_OK) {
                for (i in getDeletePosition()) {
                    adapter.removeData(i!!)
                    adapter.notifyItemRemoved(i)
                }
                adapter.notifyDataSetChanged()
                deletePosition.clear()

                onResume()
            } else {
                deletePosition.clear()
                Toast.makeText(this, "Delete Cancel", Toast.LENGTH_SHORT).show()
            }
        }
    }





}



