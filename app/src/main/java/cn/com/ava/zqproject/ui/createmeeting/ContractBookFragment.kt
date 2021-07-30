package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentContractBookBinding
import cn.com.ava.zqproject.ui.createmeeting.adpter.ContractUserItemAdapter
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView
import com.blankj.utilcode.util.ToastUtils

class ContractBookFragment : BaseFragment<FragmentContractBookBinding>() {


    private val mContractBookViewModel by viewModels<ContractBookViewModel>()


    private var mContractUserItemAdapter by autoCleared<ContractUserItemAdapter>()

    private var mSearchUserItemAdapter by autoCleared<ContractUserItemAdapter>()



    private val mCreateMeetingViewModel by viewModels<CreateMeetingViewModel>({ requireParentFragment() })

    override fun getLayoutId(): Int {
        return R.layout.fragment_contract_book
    }

    override fun initView() {
        super.initView()
        mBinding.refreshLayout.setOnRefreshListener {
            mContractBookViewModel.getContractUserList()
        }
        mBinding.refreshLayout.autoRefresh()
        if (mContractUserItemAdapter == null) {
            mContractUserItemAdapter =
                ContractUserItemAdapter(object : ContractUserItemAdapter.ContractUserCallback {
                    override fun onSelectedUser(
                        user: StatefulView<ContractUser>,
                        isSelected: Boolean
                    ) {
                        user.apply {
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
        if (mSearchUserItemAdapter == null) {
            mSearchUserItemAdapter =
                ContractUserItemAdapter(object : ContractUserItemAdapter.ContractUserCallback {
                    override fun onSelectedUser(
                        user: StatefulView<ContractUser>,
                        isSelected: Boolean
                    ) {
                        user.apply {
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
        mContractBookViewModel.contractUsers.observe(viewLifecycleOwner) { it ->
            mContractUserItemAdapter?.setDatas(it)
        }
        mContractBookViewModel.refreshState.observe(viewLifecycleOwner) { it ->
            if (it.isRefresh) {
                mBinding.refreshLayout.finishRefresh(!it.hasError)
            }
        }
        mCreateMeetingViewModel.selectedUser.observe(viewLifecycleOwner) {
            mContractBookViewModel.setSelectedUsers(it)
        }
        mContractBookViewModel.searchKey.observe(viewLifecycleOwner) { key ->
            if (TextUtils.isEmpty(key)) {   //用回
                mBinding.rvContractUser.adapter = mContractUserItemAdapter
            }else{
                mBinding.rvContractUser.adapter = mSearchUserItemAdapter
            }
        }
        mContractBookViewModel.filterUser.observe(viewLifecycleOwner){users->
            mSearchUserItemAdapter?.setDatasWithDiff(users)
        }
    }

    override fun onDestroy() {
        mBinding.rvContractUser.adapter = null
        super.onDestroy()
    }

    override fun onBindViewModel2Layout(binding: FragmentContractBookBinding) {
        binding.contractBookViewModel = mContractBookViewModel
    }
}