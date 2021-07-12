package cn.com.ava.lubosdk.query

import android.text.TextUtils
import android.util.SparseArray
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.PreviewVideoWindow
import cn.com.ava.lubosdk.entity.QueryResult
import java.util.*
/**
 * 窗口信息
 * */
class PreviewWindowInfoQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListWrapper<PreviewVideoWindow>> {

    companion object {
        val windowName: SparseArray<String> = SparseArray()
        val windowNamecArray: SparseArray<Map<String, List<String>>> = SparseArray()
        val nameMap = mutableMapOf<String, String>()
        val window2NameMap = mutableMapOf<String, String>()
        var ptzEnable: Boolean = false
        var ptzNum: Int = 0
        val windowLabels = mutableListOf<String>()
        val windowAlias = mutableListOf<String>()
        var curPreviewWindow: String? = null
        var isNeutral: Boolean = false
        var canImageMat1: Boolean = false
        var canImageMat2: Boolean = false
        var isImageMatting1: Boolean = false
        var isImageMatting2: Boolean = false
        val keylocate = intArrayOf(1, 2)

        init {
            window2NameMap["(1)"] = "教师特写"
            window2NameMap["(2)"] = "学生特写"
            window2NameMap["(3)"] = "教师全景"
            window2NameMap["(4)"] = "学生全景"
            window2NameMap["(5)"] = "板书特写"
            window2NameMap["(6)"] = "板书全景"
            window2NameMap["(7)"] = "高拍仪"
            window2NameMap["(8)"] = "教师电脑"
            window2NameMap["(9)"] = "片头"
            window2NameMap["(10)"] = "片尾"
            window2NameMap["(11)"] = "彩条"
            window2NameMap["(12)"] = "检测信号"
            window2NameMap["(22)"] = "远程1"
            window2NameMap["(23)"] = "远程2"
            window2NameMap["(24)"] = "远程3"
        }


        fun reset() {
            windowName.clear()
            windowNamecArray.clear()
            nameMap.clear()
            ptzEnable = false
            ptzNum = 0
            windowLabels.clear()
            windowAlias.clear()
            curPreviewWindow = null
            isNeutral = false
            canImageMat1 = false
            canImageMat2 = false
            isImageMatting1 = false
            isImageMatting2 = false
            keylocate[0] = 1
            keylocate[0] = 2
        }
    }


    override val name: String
        get() = "PreviewWindow"
    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.CUE_NAMEC0, AVATable.CUE_NAMEC1,
            AVATable.CUE_NAMEC2, AVATable.CUE_NAMEC3,
            AVATable.CUE_NAMEC4, AVATable.CUE_NAMEC5,
            AVATable.CUE_NAMEC6, AVATable.CUE_NAMEC7,
            AVATable.CUE_NAMEC8, AVATable.CUE_NAMEC9,
            AVATable.UI_KEYINGENABLE, AVATable.GUI_KEYINGENABLE,
            AVATable.UI_ELECPTZENABLE, AVATable.UI_PTZNUM,
            AVATable.CUE_WINDOW01, AVATable.CUE_WINDOW02,
            AVATable.CUE_WINDOW03, AVATable.CUE_WINDOW04,
            AVATable.CUE_WINDOW05, AVATable.CUE_WINDOW06,
            AVATable.CUE_WINDOW07, AVATable.CUE_WINDOW08,
            AVATable.CUE_WINDOW09, AVATable.CUE_WINDOW10,
            AVATable.CUE_WINDOW11, AVATable.CUE_WINDOW12,
            AVATable.AT_COMPUTERMODE, AVATable.UI_CUEWINDOWS,
            AVATable.UI_LAYOUTSELECT, AVATable.GUI_SIGNALNAME01,
            AVATable.GUI_SIGNALNAME02, AVATable.GUI_SIGNALNAME03,
            AVATable.GUI_SIGNALNAME04, AVATable.GUI_SIGNALNAME05,
            AVATable.GUI_SIGNALNAME06, AVATable.GUI_SIGNALNAME07,
            AVATable.GUI_SIGNALNAME08, AVATable.GUI_SIGNALNAME09,
            AVATable.GUI_SIGNALNAME10, AVATable.GUI_SIGNALNAME11,
            AVATable.UI_WINDOWNAME,
            AVATable.UI_KEYINGENABLE, AVATable.GUI_KEYINGENABLE,
            AVATable.UI_MACHINEMODEL, AVATable.UI_KEYINGSDI2,
            AVATable.BLEND_MODE, AVATable.CUE_NAMEC0,
            AVATable.CUE_NAMEC1,
            AVATable.CUE_NAMEC2, AVATable.CUE_NAMEC3,
            AVATable.CUE_NAMEC4, AVATable.CUE_NAMEC5,
            AVATable.CUE_NAMEC6, AVATable.CUE_NAMEC7,
            AVATable.CUE_NAMEC8, AVATable.CUE_NAMEC9,
            AVATable.UI_KEYWINDOWLOCATE,

            AVATable.UI_NWINDOWNAME, AVATable.UI_NEUTRAL
        )
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    override fun build(totalList: List<String>): ListWrapper<PreviewVideoWindow> {
        reset()
        var split: List<String> = totalList.subList(0, 14)
        val isNeutral = "1" == totalList[totalList.lastIndex]
        var i = 0
        val len: Int = split.size - 4
        while (i < len) {
            val s: String = split.get(i)
            val map: MutableMap<String, List<String>> =
                LinkedHashMap()
            if (TextUtils.isEmpty(s)) {
                i++
                continue
            }
            if (s.contains(",")) {
                val split1 = s.split(",".toRegex()).toTypedArray()
                for (s1 in split1) {
                    val split2 =
                        s1.split("=".toRegex()).toTypedArray()
                    val strs: MutableList<String> =
                        ArrayList()
                    if (split2[1].contains("/")) {
                        val split3 =
                            split2[1].split("/".toRegex()).toTypedArray()
                        for (s2 in split3) {
                            strs.add(s2)
                        }
                        map[split2[0]] = strs
                    } else {
                        strs.add(split2[1])
                        map[split2[0]] = strs
                    }
                }
            } else {
                val split1 = s.split("=".toRegex()).toTypedArray()
                val strs: MutableList<String> =
                    ArrayList()
                if (split1[1].contains("/")) {
                    val split2 =
                        split1[1].split("/".toRegex()).toTypedArray()
                    for (s1 in split2) {
                        strs.add(s1)
                    }
                } else {
                    strs.add(split1[1])
                }
                map[split1[0]] = strs
            }
            windowNamecArray.put(i + 1, map)
            i++
        }
        ptzEnable = split[12] == "enable"
        if (TextUtils.isDigitsOnly(split[13])) {
            ptzNum = split[13].toInt()
            if (ptzNum > 6) {
                ptzNum = 6
            }
        }

        split = totalList.subList(14, 14 + 43)

        //抠像
        canImageMat1 = !TextUtils.isEmpty(split[27]) && split[27] == "enable"
        canImageMat2 = canImageMat1 && split[29].contains("x8s") || split[29]
            .contains("u8") && split[30] == "enable"
        if (!TextUtils.isEmpty(split[28])) {
            val s = split[28].split(" ".toRegex()).toTypedArray()
            isImageMatting1 = s[0] == "0" && s[1] == "enable"
            isImageMatting2 = s[0] == "1" && s[1] == "enable"
        } else {
            isImageMatting1 = false
            isImageMatting2 = false
        }
        //抠像序号调整
        val keylocateString = split[42]
        if (!TextUtils.isEmpty(keylocateString) && keylocateString.contains("/")) {
            val keylocateSplit =
                keylocateString.split("/".toRegex()).toTypedArray()
            val s1 = keylocateSplit[0]
            val s2 = keylocateSplit[1]
            if (!TextUtils.isEmpty(s1)) {
                keylocate[0] = s1.toInt()
            }
            if (!TextUtils.isEmpty(s2)) {
                keylocate[1] = s2.toInt()
            }
        } else {
            keylocate[0] = 1
            keylocate[1] = 2
        }
        if (keylocate[0] > keylocate[1]) {
            val tempCan = canImageMat1
            canImageMat1 = canImageMat2
            canImageMat2 = tempCan
            val tempIs = isImageMatting1
            isImageMatting1 = isImageMatting2
            isImageMatting2 = tempIs
        }
        curPreviewWindow = split[31]
        val cueNum = split[13].toInt()
        for (i in 0 until cueNum) {
            var i = 0
            val len: Int = cueNum
            while (i < len) {
                if (!TextUtils.isEmpty(split[i])) {
                    windowName.put(i + 1, split[i])
                } else {
                    val s = split[32 + i]
                    val split1 = s.split("=".toRegex()).toTypedArray()
                    windowName.put(i + 1, split1[0])
                }
                i++
            }
        }
        //解析窗口自定义标签
        //解析窗口自定义标签
        if (windowLabels.size == 0) {
            var i = 15
            while (i < 15 + cueNum) {
                val labelName = split[i]
                if (TextUtils.isEmpty(labelName)) {
                    windowLabels.add("")
                } else {
                    val split1 =
                        labelName.split(",".toRegex()).toTypedArray()
                    if (split1[1] == "1") {
                        windowLabels.add(split1[0])
                    } else {
                        windowLabels.add("")
                    }
                }
                i++
            }
        }
        //解析窗口别名
        if (windowAlias.size == 0) {
            val alias = if (!isNeutral) {
                split[26]
            } else totalList[totalList.lastIndex - 1]
            val split1 = alias.split("/".toRegex()).toTypedArray()
            var i = 0
            while (i < cueNum) {
                if (split1.size > i) {
                    val bieming = split1[i]
                    if (TextUtils.isEmpty(bieming)) {
                        windowAlias.add("视频${i+1}")
                    } else {
                        windowAlias.add(bieming)
                    }
                } else {
                    windowAlias.add("")
                }
                i++
            }
        }
       return ListWrapper(getPreviewWindows())
    }


    private fun getPreviewWindows():List<PreviewVideoWindow>{
        val windowCount = windowName.size()
        val windows: MutableList<PreviewVideoWindow> =
            ArrayList()
        for (i in 1..windowCount) {
            val window = PreviewVideoWindow()
            window.index = i
            window.isCurrentOutput = curPreviewWindow == "V$i"
            val windowNamec = windowName[i]
            val map =
                windowNamecArray[i]
            val entries2 =
                map.entries
            var hasMulti = false
            for ((_, value) in entries2) {
                hasMulti = value.size > 1
            }
            if (windowNamec.contains(":") || map.size > 1 || hasMulti) {   //如果含有：，表示有多个源
                //计算sourceIndex
                var windowName = ""
                var strings: List<String?>? = null
                var sourceIndex = "1"
                var sourceIndexInt = 1
                if (windowNamec.contains(":")) {
                    windowName = windowNamec.replace(
                        windowNamec.substring(windowNamec.indexOf(":")),
                        ""
                    )
                    if (windowName == "VT_VI1") {
                        windowName = "HDMI"
                    }
                    strings = map[windowName]
                    sourceIndex = windowNamec.substring(windowNamec.indexOf(":") + 1)
                    sourceIndexInt = sourceIndex.toInt()
                } else {
                    windowName = windowNamec
                    strings = map[windowNamec]
                    sourceIndex = "1"
                    sourceIndexInt = 1
                }
                //TODO 这里修正sourceIndex
                val entries1 =
                    map.entries
                var i1 = 0
                var size = 0
                for ((key, value) in entries1) {
                    if (windowName == key) {
                        break
                    }
                    i1++
                    size += value.size
                }
                sourceIndexInt = size + sourceIndexInt
                window.curSourceIndex = sourceIndexInt
                //获取window的名字
                var s1: String? = ""
                s1 = if (strings == null || strings.isEmpty()) {
                    windowName
                } else if (strings.size == 1) {
                    strings[0]
                } else {
                    strings[sourceIndex.toInt() - 1]
                }
                if (s1.contains("(") && s1.contains(")")) {
                    s1 = window2NameMap[s1]
                }
                val cmds: MutableList<String> =
                    ArrayList()
                val label = windowLabels[i - 1]
                val alias = windowAlias[i - 1]
                if (TextUtils.isEmpty(label)) {
                    if (TextUtils.isEmpty(alias)) {
                        //中性版本标签处理
                        if (isNeutral && windowNamec.contains("SDI")) {
                            val substring = windowNamec.substring(3, 4)
                            val i2 = substring.toInt()
                            window.windowName = "视频$i2"
                        } else {
                            window.windowName = s1
                        }
                    } else {
                        window.windowName = alias
                    }
                } else {
                    window.windowName = label
                }
                val names: MutableList<String?> =
                    ArrayList()
                val entries =
                    map.entries
                for ((key, value) in entries) {
                    var j = 1
                    for (s in value) {
                        cmds.add(key + ":" + j++)
                        if (s.contains("(") && s.contains(")")) {
                            val name = window2NameMap[s]
                            names.add(name)
                        } else {
                            names.add(s)
                        }
                    }
                }
                window.sourcesCmd = cmds
                window.sources = names
                window.isHasMultiSource = names.size > 1
            } else {   //只含单个源
                val strings = map[windowNamec]
                val s = strings!![0]
                if (s.contains("(") && s.contains(")")) {
                    val name = window2NameMap[s]
                    var label = ""
                    var alias = ""
                    if (windowLabels.size >= i) {
                        label = windowLabels[i - 1]
                    }
                    if (windowLabels.size >= i) {
                        alias = windowAlias[i - 1]
                    }
                    if (TextUtils.isEmpty(label)) {
                        if (TextUtils.isEmpty(alias)) {
                            if (isNeutral && windowNamec.contains("SDI")) {
                                val substring = windowNamec.substring(3, 4)
                                val i2 = substring.toInt()
                                window.windowName = "视频$i2"
                            } else {
                                window.windowName = name
                            }
                        } else {
                            if(isNeutral){
                                window.windowName = alias
                            }else{
                                window.windowName = name
                            }

                        }
                    } else {
                        window.windowName = label
                    }
                } else {
                    val label = windowLabels[i - 1]
                    val alias = windowAlias[i - 1]
                    if (TextUtils.isEmpty(label)) {
                        if (TextUtils.isEmpty(alias)) {
                            if (isNeutral && windowNamec.contains("SDI")) {
                                val substring = windowNamec.substring(3, 4)
                                val i2 = substring.toInt()
                                window.windowName = "视频$i2"
                            } else {
                                //  window.setWindowName(s1);
                                window.windowName = windowNamec
                            }
                        } else {
                            window.windowName = alias
                        }
                    } else {
                        window.windowName = label
                    }
                }
                window.isHasMultiSource = false
            }
            //抠像设置
            if (i == keylocate[0]) {
                window.isCanMatImage = canImageMat1
                window.isMatting = isImageMatting1
            } else if (i == keylocate[1]) {
                window.isCanMatImage = canImageMat2
                window.isMatting = isImageMatting2
            }
            windows.add(window)
        }

        for (previewVideoWindow in windows) {
            var isPtz = false
            var video = ""
            var idx = 0
            var sdi = 0
            if (!ptzEnable) {
                val cueName = windowName[previewVideoWindow.index]
                var tmpCueName = ""
                if (cueName.contains("RTSP")) {
                    tmpCueName = getWindowNameByIndex(previewVideoWindow.index)?:""
                }
                if ((cueName.contains("SDI") || tmpCueName.contains("WIFI")) && previewVideoWindow.index <= ptzNum) {
                    isPtz = true
                    video = if (!TextUtils.isEmpty(tmpCueName)) {
                        tmpCueName
                    } else {
                        cueName
                    }
                    idx = video.split(":".toRegex()).toTypedArray()[0]
                        .replace("[^0-9]".toRegex(), "").toInt() - 0
                } else {
                    isPtz = false
                }
                sdi = isWiFi(ptzEnable, previewVideoWindow.index, video, idx)
            } else {
                if (previewVideoWindow.index <= ptzNum) {
                    isPtz = true
                    sdi = previewVideoWindow.index
                } else {
                    isPtz = false
                }
            }
            previewVideoWindow.isPtz = isPtz
            previewVideoWindow.ptzIdx = sdi
        }
        Cache.getCache().saveWindows(windows)
        return windows
    }


    /**
     * 通过窗口index查询窗口名称
     *
     * @param index
     * @return
     */
    fun getWindowNameByIndex(index: Int): String? {
        if (windowName == null) {
            return "V$index"
        }
        if (index > windowName.size()) {
            return "V$index"
        }
        val s = windowName[index]
        return if (s.contains(":")) {
            val split = s.split(":".toRegex()).toTypedArray()
            val streamIndex = split[1].toInt()
            val streamName = split[0]
            var value = nameMap[streamName]
            if(TextUtils.isEmpty(value)){
                value = streamName
                value
            }else{
                val split1 = value!!.split("/".toRegex()).toTypedArray()
                val s1 = split1[streamIndex - 1]
                if (s1.contains("(") && s1.contains(")")) {
                    window2NameMap[s1]
                } else {
                    s1
                }
            }

        } else {
            if (nameMap.containsKey(s)) {
                val s1 = nameMap[s]
                if (s1!!.contains("(") && s1.contains(")")) {
                    window2NameMap[s1]
                } else {
                    s1
                }
            } else {
                "V$index"
            }
        }
    }


    private fun isWiFi(
        ptzEnable: Boolean,
        index: Int,
        video: String,
        idx: Int
    ): Int {
        var sdi = 0
        sdi = if (ptzEnable) {
            index
        } else {
            if (idx != 0) {
                idx
            } else {
                index
            }
        }
        if (video.contains("WIFI")) {
            sdi += 32
        }
        return sdi
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}
}