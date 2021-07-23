package cn.com.ava.zqproject.ui.record

import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentRecordBinding

/**
 * 录课界面
 * <p>
 *     1.轮询录播信息，当录播进入会议跳到会议界面
 *     2.需求中的录播操作
 * </p>
 */
class RecordFragment:BaseFragment<FragmentRecordBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_record
}