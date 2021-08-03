package cn.com.ava.zqproject.vo

import androidx.annotation.IntDef


const val TYPE_COMPOSITE = 1
const val TYPE_VIDEO = 2
const val TYPE_VIDEO_LAYOUT = 3
const val TYPE_PRE_STAGE = 4

@IntDef(TYPE_COMPOSITE, TYPE_VIDEO, TYPE_VIDEO_LAYOUT, TYPE_PRE_STAGE)
annotation class CommandButtonType


interface CommandButton {

    @CommandButtonType
    val type: Int
}

data class CompositeButton(
    val keyId: Int,
    val keyName: String,
    val bmpPath: String,
    val commandButtons: Array<CommandButton>
) : CommandButton {
    override val type: Int
        get() = TYPE_COMPOSITE

}

data class LayoutButton(val layoutIndex: Int,var layoutDrawable:Int = -1,var layoutCmd:String = "") : CommandButton {
    override val type: Int
        get() = TYPE_VIDEO_LAYOUT

    fun isCustom():Boolean = layoutCmd.contains("VC")

    fun customIndex():Int{
        if(isCustom()){
           return layoutCmd.replace("VC","").toInt()
        }else{
            return 0
        }
    }

}


data class VideoWindowButton(val windowIndex: Int,var windowName:String = "视频${windowIndex+1}") : CommandButton {

    override val type: Int
        get() = TYPE_VIDEO

}

data class VideoPresetButton(
    val videoWindowIndex: Int,
    val presetIndex: Int
) : CommandButton {
    override val type: Int
        get() = TYPE_PRE_STAGE

}


