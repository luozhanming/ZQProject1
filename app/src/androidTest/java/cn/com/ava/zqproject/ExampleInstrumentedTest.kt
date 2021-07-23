package cn.com.ava.zqproject

import androidx.test.ext.junit.runners.AndroidJUnit4
import cn.com.ava.common.util.loge
import cn.com.ava.lubosdk.util.GsonUtil
import cn.com.ava.zqproject.common.CommandKeyHelper
import cn.com.ava.zqproject.vo.CommandButton
import cn.com.ava.zqproject.vo.LayoutButton
import cn.com.ava.zqproject.vo.VideoPresetButton
import cn.com.ava.zqproject.vo.VideoWindowButton
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val buttons = arrayListOf<CommandButton>()
        buttons.add(LayoutButton(1))
        buttons.add(VideoWindowButton(2))
        buttons.add(VideoPresetButton(2,2))
        CommandKeyHelper.setSelectedCommandKeys(buttons)
        val selectedCommandKeys = CommandKeyHelper.getSelectedCommandKeys()
        selectedCommandKeys.toString()
    }
}