package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentContractGroupBinding
import cn.com.ava.zqproject.ui.createmeeting.adpter.ContractGroupItemAdapter
import cn.com.ava.zqproject.vo.ContractGroup
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils

class ContractGroupFragment : BaseFragment<FragmentContractGroupBinding>() {

    private val mContractGroupViewModel by viewModels<ContractGroupViewModel>()

    private val mCreateMeetingViewModel by viewModels<CreateMeetingViewModel>({ requireParentFragment() })

    private var mContractGroupAdapter by autoCleared<ContractGroupItemAdapter>()

    /**
     * 过滤后用的adapter
     * */
    private var mSearchGroupAdapter by autoCleared<ContractGroupItemAdapter>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_contract_group
    }



    override fun initView() {
        mBinding.refreshLayout.setOnRefreshListener {
            mContractGroupViewModel.getContractGroups()
        }
        mBinding.refreshLayout.autoRefresh()
        if (mContractGroupAdapter == null) {
            mContractGroupAdapter =
                ContractGroupItemAdapter(object : ContractGroupItemAdapter.ContractGroupCallback {
                    override fun onSelectedGroup(
                        user: StatefulView<ContractGroup>,
                        isSelected: Boolean
                    ) {//选择了一个组别
                        if (mCreateMeetingViewModel.addOrDelGroup(user.obj)) {
                            user.isSelected = isSelected
                            mContractGroupAdapter?.changeData(user)
                        }else{
                            ToastUtils.showShort(getString(R.string.toast_create_meeting_max_count))
                        }
                    }
                })
        }
        //搜索添加
        if (mSearchGroupAdapter == null) {
            mSearchGroupAdapter =
                ContractGroupItemAdapter(object : ContractGroupItemAdapter.ContractGroupCallback {
                    override fun onSelectedGroup(
                        user: StatefulView<ContractGroup>,
                        isSelected: Boolean
                    ) {//选择了一个组别
                        if (mCreateMeetingViewModel.addOrDelGroup(user.obj)) {
                            user.isSelected = isSelected
                            mContractGroupAdapter?.changeData(user)
                        }else{
                            ToastUtils.showShort(getString(R.string.toast_create_meeting_max_count))
                        }
                    }
                })
        }
        mBinding.rvGroup.adapter = mContractGroupAdapter
        mBinding.rvGroup.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }


    override fun observeVM() {
        mContractGroupViewModel.contractGroups.observe(viewLifecycleOwner) {
            mContractGroupAdapter?.setDatasWithDiff(it)
        }
        mContractGroupViewModel.refreshState.observe(viewLifecycleOwner) { it ->
            if (it.isRefresh) {
                mBinding.refreshLayout.finishRefresh(!it.hasError)
            }
        }
        mCreateMeetingViewModel.selectedUser.observe(viewLifecycleOwner) {
            mContractGroupViewModel.setSelectedUser(it)
        }
        //搜索添加
        mContractGroupViewModel.searchKey.observe(viewLifecycleOwner) { key ->
            if (TextUtils.isEmpty(key)) {   //用回
                mBinding.rvGroup.adapter = mContractGroupAdapter
            }else{
                mBinding.rvGroup.adapter = mSearchGroupAdapter
            }
        }
        //搜索添加
        mContractGroupViewModel.filterUser.observe(viewLifecycleOwner){
            mSearchGroupAdapter?.setDatasWithDiff(it)
        }
    }

    override fun onDestroyView() {
        mBinding.rvGroup.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBindViewModel2Layout(binding: FragmentContractGroupBinding) {
        binding.contractGroupViewModel = mContractGroupViewModel
    }


}