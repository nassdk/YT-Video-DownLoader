package com.nassdk.ytvideodownloader


import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.content.pm.PackageManager
import android.inputmethodservice.Keyboard
import android.net.Uri

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Environment
import android.text.TextUtils
import android.util.SparseArray
import android.view.View
import at.huber.youtubeExtractor.YouTubeUriExtractor
import at.huber.youtubeExtractor.YtFile
import kotlinx.android.synthetic.main.toolbar_layout.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var downloadManager: DownloadManager

    private lateinit var uri: String

    private var downId: Long? = null

    private val PERMISSION_STORAGE_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolBar)
        supportActionBar?.title = "YT Video Downloader"

        buttonDownload.setOnClickListener(this)
        buttonClear.setOnClickListener(this)


    }


    private fun download(uri: String) {
        val yTex = @SuppressLint("StaticFieldLeak")
        object : YouTubeUriExtractor(this) {
            override fun onUrisAvailable(videoId: String, videoTitle: String?, ytFiles: SparseArray<YtFile>?) {
                if (ytFiles != null) {
                    val itag = 22
                    // Here you can get download url
                    val downloadUrl = ytFiles.get(itag).url

                    downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    val request = DownloadManager.Request(Uri.parse(downloadUrl))


                    request.setAllowedOverRoaming(true)

                    request.setTitle("Download")
                    request.setDescription("Downloading video...")

                    request.allowScanningByMediaScanner()

                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "Video " + System.currentTimeMillis()
                    )

                    downId = downloadManager.enqueue(request)
                }
            }
        }

        yTex.execute(uri)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {

                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L)
                if (id != downId) {
                    Toast.makeText(
                        this@MainActivity,
                        "NO Message",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Видео успешно загружено. Вы можете найти его в папке загрузок вашего телефона!",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download(uri)
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonDownload -> {
                uri = edUrl.text.toString().trim()
                if (TextUtils.isEmpty(uri)) {
                    Toast.makeText(
                        applicationContext,
                        "Пожалуйста введите ссылку видео",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    if (isNetworkAvailable(this)) {
                        Toast.makeText(
                            applicationContext,
                            "Загрузка вашего видео началась. Отследить состояние загрузки можно в Панели загрузок",
                            Toast.LENGTH_LONG
                        ).show()



                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                val permission: Array<String> =
                                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                requestPermissions(permission, PERMISSION_STORAGE_CODE)
                            } else {
                                download(uri)

                            }
                        } else {
                            download(uri)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Проверьте ваще подключение к интернету и попробуйте позже",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                }
            }

            R.id.buttonClear -> {
                edUrl.text = null
            }
        }
    }


}
