package com.mycro.micro.half

/**
 * Author: canyan.zhang
 * Date: 2025/4/25 21:23
 * Description:
 */

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mycro.micro.View.helper.PullUpDrawerHelper
import com.mycro.micro.R
import com.mycro.micro.View.MyNestedScrollView

abstract class ScrollViewFragment : Fragment() {

    protected var mPullUpDrawerHelper: PullUpDrawerHelper? = null
    protected var mNestedScrollView: MyNestedScrollView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNestedScrollView = view.findViewById(R.id.nested_scrollview)
        if (mNestedScrollView != null) {
            setPullUpDrawerHelper(mPullUpDrawerHelper)
        }
    }

    fun setPullUpDrawerHelper(helper: PullUpDrawerHelper?) {
        if (mPullUpDrawerHelper == null) {
            mPullUpDrawerHelper = helper
        }
        mNestedScrollView?.setPullUpDrawerHelper(helper)
    }
}
