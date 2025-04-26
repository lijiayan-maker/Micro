package com.mycro.micro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycro.micro.camera.Camera2Basic

/**
 * Author: canyan.zhang
 * Date: 2025/4/6 18:09
 * Description: 主界面
 */

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (null == savedInstanceState) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, Camera2Basic.newInstance())
                .commit()
        }
    }

}