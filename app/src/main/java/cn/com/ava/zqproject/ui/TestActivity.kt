package cn.com.ava.zqproject.ui

import android.os.Bundle
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import cn.com.ava.td.terminal.avartspviewer.RtspView
import cn.com.ava.zqproject.R

class TestActivity:AppCompatActivity() {


    private val rtspView by lazy {
        val view = findViewById<RtspView>(R.id.rtspView)
        //耗时操作
        view.getHolder().addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder?) {
                view.initLiveTool(holder?.getSurface())
                val num_streams = 1
                val in_x = intArrayOf(0)
                val in_y = intArrayOf(0)
                val in_width = intArrayOf(0)
                val in_height = intArrayOf(0)
                view.startLiving("rtsp://192.168.21.204:554/stream/1?config.login=web", num_streams, in_x, in_y, in_width, in_height)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }
        }
           )
        view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        rtspView.toString()
    }
}