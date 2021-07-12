package cn.com.ava.lubosdk.control.whiteboard

import cn.com.ava.lubosdk.IControl
import cn.com.ava.lubosdk.manager.LoginManager
import com.blankj.utilcode.util.EncryptUtils

//Map<String, String> mParams = new LinkedHashMap<>();
//mParams.put("action", "6");
//mParams.put("user", mAccount);
//mParams.put("pwsd", mPwdEncrypt);
//mParams.put("command", "1");
//mParams.put("data", BLANKBOARD_START_KEY);
//Logger.d("startBlankBoard http request, params:" + mParams.toString());
//return mService.blankBoardCommonApi(mParams)
//.flatMap(new Function<ResponseBody, ObservableSource<Boolean>>() {
//    @Override
//    public ObservableSource<Boolean> apply(ResponseBody body) throws Exception {
//        final String response = body.string();
//        if (response.startsWith(BLANKBOARD_SWITCH_RET_KEY)) {
//            String state = response.substring(response.indexOf("=") + 1).replace("\r\n", "");
//            if (state.contains("ok")) {
//                return Observable.just(true);
//            }
//        }
//        if(response.contains("error_-88")){
//            return Observable.error(new Exception(Utils.getApp().getString(R.string.blankboard_brainco_conflict)));
//        }else{
//            return Observable.error(new Exception(Utils.getApp().getString(R.string.blankboard_start_answer_failed)));
//        }
//    }
//}).subscribeOn(Schedulers.io());
/**
 * 电子白板开关
 * */
class WhiteBoardSwitchControl(
        val isOn: Boolean,
        override var onResult: (Boolean) -> Unit,
        override var onError: ((Throwable) -> Unit)? = null) : IControl {


    override val name: String
        get() = "TurnOnWhiteBoard"

    override fun getControlParams(): LinkedHashMap<String, String> {
        return linkedMapOf<String, String>().apply {
            this["action"] = "6"
            this["user"] = LoginManager.getLogin()?.username ?: ""
            this["pwsd"] = EncryptUtils.encryptMD5ToString(LoginManager.getLogin()?.password).toLowerCase();

            this["command"] = "1"
            this["data"] = if (isOn) "WhiteBoard_switch_open" else "WhiteBoard_switch_close"
        }
    }

    override fun build(response: String): Boolean {
        if (response.startsWith("WhiteBoardRet_switch")) {
            val state = response.substring(response.indexOf("=") + 1).trim()
            return "ok" in state
        }
        if (response.contains("error_-88")) {
            throw IllegalStateException(if(isOn)"正使用脑电波检测功能，无法使用电子白板" else "关闭失败")
        } else {
            throw IllegalStateException(if(isOn)"开启失败" else "关闭失败")
        }
    }
}