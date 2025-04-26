package com.mycro.micro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mycro.micro.abs.AbsFragment
import com.mycro.micro.camera.Camera2Basic
import com.mycro.micro.camera.CameraXBasic
import com.mycro.micro.utils.LogUtils

/**
 * Author: canyan.zhang
 * Date: 2025/4/6 18:09
 * Description: 主界面
 */

class HomeActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HomeActivity"
    }

    private var _camera2Fragment: Camera2Basic? = null
    private var _cameraXFragment: CameraXBasic? = null
    private lateinit var _viewPager: ViewPager2
    private val _fragmentList = arrayListOf<AbsFragment>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogUtils.d(TAG, "onCreate")

        initData()
        initFragment()
    }

    private fun initData() {

    }

    private fun initFragment() {
        _viewPager = findViewById(R.id.viewPager)
        _camera2Fragment = Camera2Basic.newInstance()
        _cameraXFragment = CameraXBasic.newInstance()
        _camera2Fragment?.let { _fragmentList.add(it) }
        _cameraXFragment?.let { _fragmentList.add(it) }

        // 设置 Adapter
        _viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = _fragmentList.size
            override fun createFragment(position: Int): Fragment = _fragmentList[position]
        }
        _viewPager.offscreenPageLimit = 1

    }

}