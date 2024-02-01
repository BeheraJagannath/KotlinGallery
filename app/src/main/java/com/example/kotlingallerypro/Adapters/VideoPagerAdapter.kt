package com.example.kotlingallerypro.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.kotlingallerypro.Activity.VideoPlayerActivity
import com.example.kotlingallerypro.Activity.VideoSliderActivity
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.modelclass.Videomodel
class VideoPagerAdapter(val context: Context, val videoList: ArrayList<Videomodel>, val viewPager: ViewPager): PagerAdapter() {

    lateinit var imageView: ImageView

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.video_slider, null)
        imageView = view.findViewById(R.id.iv_video_slider)

        val allVideoModel = videoList [position]
        Glide.with(context).load(allVideoModel.path)
            .apply(RequestOptions().fitCenter()).into(imageView)
        imageView.setOnClickListener {
            val intent = Intent(context, VideoPlayerActivity::class.java)
//                bundle.putSerializable("path", allVideoModel as Serializable)
            intent.putExtra("path", allVideoModel)
            VideoSliderActivity.position = position
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(context, intent, null)

        }

        (container as ViewPager).addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun getCount(): Int {
        return videoList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}
