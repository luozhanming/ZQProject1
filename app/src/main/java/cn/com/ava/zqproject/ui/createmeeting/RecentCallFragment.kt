package cn.com.ava.zqproject.ui.createmeeting

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentRecentCallBinding
import cn.com.ava.zqproject.ui.createmeeting.adpter.ContractUserItemAdapter
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils

class RecentCallFragment : BaseFragment<FragmentRecentCallBinding>() {

    private val mRecentCallViewModel by viewModels<RecentCallViewModel>()

    private val mCreateMeetingViewModel by viewModels<CreateMeetingViewModel>({ requireParentFragment() })

    private var mContractUserItemAdapter by autoCleared<ContractUserItemAdapter>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_recent_call
    }


    override fun initView() {
        mBinding.refreshLayout.setOnRefreshListener {
            mRecentCallViewModel.getRecentContracts()
        }
        mBinding.refreshLayout.autoRefresh()
        if (mContractUserItemAdapter == null) {
            mContractUserItemAdapter =
                ContractUserItemAdapter(object : ContractUserItemAdapter.ContractUserCallback {
                    override fun onSelectedUser(
                        user: StatefulView<ContractUser>,
                        isSelected: Boolean
                    ) {
                        user?.apply {
                            if (mCreateMeetingViewModel.addOrDelSelectedUser(obj)) {      //成功添加
                                user.isSelected = isSelected
                                mContractUserItemAdapter?.changeData(user)
                            } else {   //添加失败
                                ToastUtils.showShort(getString(R.string.toast_create_meeting_max_count))
                            }
                        }
                    }
                })
        }
        mBinding.rvContractUser.adapter = mContractUserItemAdapter

        mBinding.rvContractUser.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }


    override fun observeVM() {
        mRecentCallViewModel.recentContract.observe(viewLifecycleOwner) { it ->
            mContractUserItemAdapter?.setDatas(it)
        }
        mRecentCallViewModel.refreshState.observe(viewLifecycleOwner) { it ->
            if (it.isRefresh) {
                mBinding.refreshLayout.finishRefresh(!it.hasError)
            }
        }
        mCreateMeetingViewModel.selectedUser.observe(viewLifecycleOwner) {
            mRecentCallViewModel.setSelectedUsers(it)
        }
    }

    override fun onDestroyView() {
        mBinding.rvContractUser.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}