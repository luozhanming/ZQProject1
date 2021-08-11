package cn.com.ava.zqproject.ui.createmeeting

import android.text.TextUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.ava.base.ui.BaseFragment
import cn.com.ava.common.extension.autoCleared
import cn.com.ava.zqproject.R
import cn.com.ava.zqproject.databinding.FragmentCreateGroupBinding
import cn.com.ava.zqproject.net.PlatformApi
import cn.com.ava.zqproject.ui.createmeeting.adpter.ContractUserItemAdapter
import cn.com.ava.zqproject.ui.createmeeting.dialog.CreateGroupConfirmDialog
import cn.com.ava.zqproject.vo.ContractUser
import cn.com.ava.zqproject.vo.StatefulView

class CreateGroupFragment : BaseFragment<FragmentCreateGroupBinding>() {
    /**
     * 常规用的adapter
     * */
    private var mContractUserItemAdapter by autoCleared<ContractUserItemAdapter>()

    private val mCreateGroupViewModel by viewModels<CreateGroupViewModel>()

    /**
     * 过滤后用的adapter
     * */
    private var mSearchUserItemAdapter by autoCleared<ContractUserItemAdapter>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_create_group
    }

    override fun initView() {
        mBinding.rvContractUser.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        if (mContractUserItemAdapter == null) {
            mContractUserItemAdapter =
                ContractUserItemAdapter(object : ContractUserItemAdapter.ContractUserCallback {
                    override fun onSelectedUser(
                        user: StatefulView<ContractUser>,
                        isSelected: Boolean
                    ) {
                        mCreateGroupViewModel.addOrDelContractUser(user.obj)
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
                        mCreateGroupViewModel.addOrDelContractUser(user.obj)
                    }
                })
        }
        mBinding.rvContractUser.adapter = mContractUserItemAdapter
        mCreateGroupViewModel.getContractUser()
        mBinding.ivClose.setOnClickListener {
            findNavController().popBackStack()
        }
        mBinding.tvComplete.setOnClickListener {
            val dialog = CreateGroupConfirmDialog{name->  //创建群组
                mCreateGroupViewModel.createMeeting(name)
            }
            dialog.show(childFragmentManager,"")
        }
    }


    override fun observeVM() {
        mCreateGroupViewModel.contractUsers.observe(viewLifecycleOwner) { users ->
            mContractUserItemAdapter?.setDatasWithDiff(users)
        }
        mCreateGroupViewModel.searchKey.observe(viewLifecycleOwner) { key ->
            if (TextUtils.isEmpty(key)) {   //用回
                mBinding.rvContractUser.adapter = mContractUserItemAdapter
            }else{
                mBinding.rvContractUser.adapter = mSearchUserItemAdapter
            }
        }
        mCreateGroupViewModel.filterUser.observe(viewLifecycleOwner){users->
            mSearchUserItemAdapter?.setDatasWithDiff(users)
        }
        mCreateGroupViewModel.createdGroup.observe(viewLifecycleOwner){it->
            //跳回到上一层
            findNavController().popBackStack()
        }
    }

    override fun onBindViewModel2Layout(binding: FragmentCreateGroupBinding) {
        binding.createGroupViewModel = mCreateGroupViewModel
    }

    override fun onDestroyView() {
        mBinding.rvContractUser.adapter = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}