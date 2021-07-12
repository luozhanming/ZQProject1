package cn.com.ava.zqproject.vo

import androidx.annotation.IntDef


const val TYPE_COMPOSITE = 1
const val TYPE_VIDEO = 2
const val TYPE_VIDEO_LAYOUT = 3
const val TYPE_PRE_STAGE = 4

@IntDef(TYPE_COMPOSITE, TYPE_VIDEO, TYPE_VIDEO_LAYOUT, TYPE_PRE_STAGE)
annotation class CommandButtonType


interface CommandButton : StatefulView {

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
    override var isSelected: Boolean = false
        get() = field
        set(value) {
            field = value
        }
    override var isChecked: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}

data class LayoutButton(val layoutIndex: Int) : CommandButton {
    override val type: Int
        get() = TYPE_VIDEO_LAYOUT
    override var isSelected: Boolean = false
        get() = field
        set(value) {
            field = value
        }
    override var isChecked: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}


data class VideoWindowButton(val windowIndex: Int) : CommandButton {
    override val type: Int
        get() = TYPE_VIDEO
    override var isSelected: Boolean = false
        get() = field
        set(value) {
            field = value
        }
    override var isChecked: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}

data class VideoPresetButton(
    val videoWindowIndex: Int,
    val presetIndex: Int
) :CommandButton{
    override val type: Int
        get() = TYPE_PRE_STAGE
    override var isSelected: Boolean = false
        get() = field
        set(value) {
            field = value
        }
    override var isChecked: Boolean = false
        get() = field
        set(value) {
            field = value
        }
}


