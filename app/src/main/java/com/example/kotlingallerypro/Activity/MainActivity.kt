package com.example.kotlingallerypro.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.example.kotlingallerypro.Adapters.AlbumFolderAdapter
import com.example.kotlingallerypro.Fragments.Favourite_fragment
import com.example.kotlingallerypro.Fragments.Gallery_fragment
import com.example.kotlingallerypro.Fragments.Video_fragment
import com.example.kotlingallerypro.Preference.PreferenceManager
import com.example.kotlingallerypro.R
import com.example.kotlingallerypro.Utils.Utils
import com.example.kotlingallerypro.databinding.ActivityMainBinding
import com.example.kotlingallerypro.databinding.ColumnTypesBinding
import com.example.kotlingallerypro.databinding.OptionmenuDialogBinding
import com.example.kotlingallerypro.databinding.ViewTypesBinding
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.tabs.TabLayout
import com.greedygame.core.AppConfig
import com.greedygame.core.GreedyGameAds
import com.greedygame.core.app_open_ads.general.AdOrientation
import com.greedygame.core.app_open_ads.general.AppOpenAdsEventsListener
import com.greedygame.core.app_open_ads.general.GGAppOpenAds
import com.greedygame.core.models.general.AdErrors


class MainActivity : AppCompatActivity() {
    companion object{
        lateinit var binding: ActivityMainBinding
        private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
        lateinit var dialog: Dialog
        var albumFolderAdapter : AlbumFolderAdapter? = null
        private lateinit var preferenceManager: PreferenceManager


        lateinit var   minterstitialAd :InterstitialAd
        private val background: ColorDrawable? = null
//        private lateinit var mAd :GGInterstitialAd

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this , R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this,R.color.indicator_color)

        preferenceManager = PreferenceManager(this)






        if (preferenceManager.checkMode()!!) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (preferenceManager.checkMode()!!) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        runtimePermission()
        loadInterstitialAd()

        binding.menu.setOnClickListener {v->
            showOpt()

        }


    }

    private fun showOpt() {
        val popupWindow = PopupWindow(applicationContext)
        val binding: OptionmenuDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(
            applicationContext), R.layout.optionmenu_dialog, null, false)
        popupWindow.contentView = binding.getRoot()

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAsDropDown(this. findViewById(R.id.menu), -40, -40)

        if (preferenceManager.checkMode()!!) {
            preferenceManager.isNightMode(true)
            binding.mNight.visibility = View.GONE
            binding.mday.visibility = View.VISIBLE
            binding.lin1.visibility = View .GONE
        } else {
            preferenceManager.isNightMode(false)
            binding.mNight.visibility = View.VISIBLE
            binding.mday.visibility = View.GONE
            binding.lin1.visibility = View .VISIBLE

        }

        binding.mNight.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            preferenceManager.isNightMode(true)
            popupWindow.dismiss()
        }
        binding.mday.setOnClickListener(View.OnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            preferenceManager.isNightMode(false)
            popupWindow.dismiss()
        })
        binding.allImage.setOnClickListener{
            var intent = Intent(this, AllimageActivity::class.java)
            startActivity(intent)
            popupWindow.dismiss()


        }

        binding.columnTypes.setOnClickListener{
            columnTypes()
            popupWindow.dismiss()
        }
    }

    private fun columnTypes() {
        val popupWindow = PopupWindow(applicationContext)
        val columnbinding: ColumnTypesBinding = DataBindingUtil.inflate(LayoutInflater.from(
            applicationContext), R.layout.column_types, null, false)
        popupWindow.contentView = columnbinding.getRoot()

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAsDropDown(this. findViewById(R.id.menu), -40, -40)
        columnbinding.column2.setOnClickListener{
            Utils.COLUMN_TYPE = 2
            val lbm = LocalBroadcastManager.getInstance(this@MainActivity)
            val localIn = Intent("TAG_REFRESH")
            lbm.sendBroadcast(localIn)
            startApp()
            popupWindow.dismiss()

        }
        columnbinding.column3.setOnClickListener{
            Utils.COLUMN_TYPE = 3
            val lbm = LocalBroadcastManager.getInstance(this@MainActivity)
            val localIn = Intent("TAG_REFRESH")
            lbm.sendBroadcast(localIn)
            startApp()
            popupWindow.dismiss()


        }
        columnbinding.column4.setOnClickListener{
            Utils.COLUMN_TYPE = 4
            val lbm = LocalBroadcastManager.getInstance(this@MainActivity)
            val localIn = Intent("TAG_REFRESH")
            lbm.sendBroadcast(localIn)
            startApp()
            popupWindow.dismiss()

        }


    }

    private fun viewtypes() {
        val popupWindow = PopupWindow(applicationContext)
        val viewbinding: ViewTypesBinding = DataBindingUtil.inflate(LayoutInflater.from(
            applicationContext), R.layout.view_types, null, false)
        popupWindow.contentView = viewbinding.getRoot()

        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.showAsDropDown(this. findViewById(R.id.menu), -40, -40)


    }


    private fun startApp() {
        loadExitDialog()

//        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = MyAdapter(this, supportFragmentManager, binding.tabLayout.tabCount)
        binding.viewPager.adapter = adapter

        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                binding.viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {


            }
            override fun onTabReselected(tab: TabLayout.Tab) {


            }
        })
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(position == 0) {
                    binding.mainText.text = "Gallery"
                }else if (position == 1){
                   binding.mainText.text = "Videos"
                } else if (position == 2){
                    binding.mainText.text = "Favourite"
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })


    }
     @SuppressLint("NewApi")
    private fun runtimePermission() {
         if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                 applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                 MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)

         }
       else if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("""For Know weather forecast you must need to access the Location for performing necessary task . 
                            Please permit the permission through Settings screen.
                
                Select Permissions -> Enable permission
                """.trimIndent())
            builder.setCancelable(false)
            builder.setPositiveButton("Permit Manually") {
                    dialog, which ->
                dialog.dismiss()
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            builder.show()

        }else{
            startApp()

        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startApp()
                }
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                val builder = AlertDialog.Builder(this)
                builder.setMessage("""For Know weather forecast you must need to access the Location for performing necessary task . 
                            Please permit the permission through Settings screen.

                Select Permissions -> Enable permission
                """.trimIndent())
                builder.setCancelable(false)
                builder.setPositiveButton("Permit Manually") {
                        dialog, which ->
                    dialog.dismiss()
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                builder.show()

            }else{
                runtimePermission()

            }
        }
    }

   /* @SuppressLint("NewApi")
    private fun runtimePermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            startApp()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startApp()
                }
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
                } else {

                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("""For Know weather forecast you must need to access the Location for performing necessary task .
                            Please permit the permission through Settings screen.

                Select Permissions -> Enable permission
                """.trimIndent())
                    builder.setCancelable(false)
                    builder.setPositiveButton("Permit Manually") {
                            dialog, which ->
                        dialog.dismiss()
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    builder.setNegativeButton("Cancel", null)
                    builder.show()

                }
            }
        }
    }*/

    class MyAdapter(
        var mContext: Context,
        fragmentManager: FragmentManager?,
        var mTotalTabs: Int,
    ) :
        FragmentPagerAdapter(fragmentManager!!) {
        override fun getItem(position: Int): Fragment {
            Log.d("asasas", position.toString() + "")
            when (position) {
                0 -> {

                    return Gallery_fragment()
                }
                1 -> {
                    return Video_fragment()
                }
                2 -> {

                    return Favourite_fragment()
                }
                else ->{return Gallery_fragment()}
            }
        }

        override fun getCount(): Int {
            return mTotalTabs
        }
    }




    private fun loadExitDialog() {
        dialog = Dialog(this)
        dialog.setCancelable(false)
        val view: View = layoutInflater.inflate(R.layout.exit_dialog, null)
        view.findViewById<View>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<View>(R.id.btn_yes).setOnClickListener {
            if (minterstitialAd != null) {
                minterstitialAd.show(this@MainActivity)
            } else {
                finish()
            }
        }
        showad(view)
        dialog.setContentView(view)
    }
    private fun showad(view: View) {
        val adLoader = AdLoader.Builder(this, getString(R.string.native_ads))
            .forNativeAd { ad : NativeAd ->

                val styles = NativeTemplateStyle.Builder().build()
                val  template : TemplateView = view .findViewById (R.id.my_template)
                template.setStyles(styles)
                template.setNativeAd(ad)
            }

            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onBackPressed() {
        dialog.show()

    }


    private fun loadInterstitialAd() {

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.interstitial), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    minterstitialAd = interstitialAd
                    minterstitialAd.setFullScreenContentCallback(object :
                        FullScreenContentCallback() {
                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            finish()
                            Log.d("Ad test", "Ad failed to show")
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d("Ad test", "Ad showed successfully")
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            finish()
                        }
                    })
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d("gsf", "fhdj")
                    finish()
                    minterstitialAd == null
                }
            })
    }
}

