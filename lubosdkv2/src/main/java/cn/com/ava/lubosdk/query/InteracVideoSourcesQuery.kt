package cn.com.ava.lubosdk.query

import android.text.TextUtils
import cn.com.ava.lubosdk.AVATable
import cn.com.ava.lubosdk.Cache
import cn.com.ava.lubosdk.Constant
import cn.com.ava.lubosdk.IQuery
import cn.com.ava.lubosdk.entity.InteracVideoSource
import cn.com.ava.lubosdk.entity.ListWrapper
import cn.com.ava.lubosdk.entity.QueryResult
import java.util.*


/**
 * 互动视频源查询
 * */
class InteracVideoSourcesQuery(
    override var onResult: (QueryResult) -> Unit,
    override var onError: ((Throwable) -> Unit)? = null
) : IQuery<ListWrapper<InteracVideoSource>> {

    override val name: String
        get() = "InteracVideoSource"
    override val mQueryFields: Array<Int>
        get() = arrayOf(
            AVATable.UI_INTERASOURCE, AVATable.UI_INTERAVIDEOBT,
            AVATable.SEM_INTERATSSOURCE, AVATable.SEM_TEACHMODE,
            AVATable.UI_INTERACT1V3, AVATable.INTERA_MEETINGINFO,
            AVATable.UI_INTERAHDMI, AVATable.SEM_INTERAHDMISOURCE,
            AVATable.UI_DEVMODEL, AVATable.UI_MACHINEMODEL,
            AVATable.UI_INTERACT1V3
        )
    override var offsets: Int = 0
        get() = field
        set(value) {
            field = value
        }

    private var canInterac1V3:Boolean = false

    override fun build(split: List<String>): ListWrapper<InteracVideoSource> {

        //能否1V3解析
        val machineModel = split[9]
        val devModel = split[8]
        val interac1V3 = split[10]
        if (machineModel.contains("h2") || machineModel.contains("x8s") || machineModel.contains("h4m")) {
            if (devModel.contains("AE-A") || devModel.contains("AE-E") || devModel.contains("AE-C") || devModel.contains(
                    "AE-V"
                )
            ) {
                canInterac1V3 = !devModel.contains("S")
            }
        } else {
            canInterac1V3 = interac1V3 == "2" || machineModel.contains("ql")
        }

        val interaVideoBT: String = split.get(1)
        val tsSource: Array<String> = split.get(0).split("/".toRegex()).toTypedArray()
        val interaTSSource: String = split.get(2)
        val videos = interaVideoBT.split(",".toRegex()).toTypedArray()
        val hasTeacher =
            videos[0] != "null" && !TextUtils.isEmpty(tsSource[0])
        val hasStudent = videos[1] != "null" && tsSource.size > 1
        val hasTSSource = !TextUtils.isEmpty(interaTSSource)
        val isSip = TextUtils.isEmpty(split.get(5))
        //HDMI解析
        val hdmiIndexStr: String = split.get(6)
        var hdmiIndex: Array<String>? = null
        var hasMultiHdmi = false
        var selectHdmi: String = split.get(7)
        if (!TextUtils.isEmpty(hdmiIndexStr)) {
            hdmiIndex = hdmiIndexStr.split(",".toRegex()).toTypedArray()
            hasMultiHdmi = hdmiIndex.size > 1
            if (TextUtils.isEmpty(selectHdmi)) {  //如果是空的话默认第一个
                selectHdmi = hdmiIndex[0]
            }
        }
        val videoList: MutableList<InteracVideoSource> =
            ArrayList()
        val maxStr: String = split.get(3)
        // int maxShow = TextUtils.isEmpty(maxStr) ? 4 : Integer.parseInt(maxStr);
        // int maxShow = TextUtils.isEmpty(maxStr) ? 4 : Integer.parseInt(maxStr);
        val maxShow: Int = parseTeachMode(split[3], split[4], split[5])
        for (i in 0..3) {
            if (videos[i] != "null") {
                val video = InteracVideoSource()
                if (i == 0) {   //老师画面
                    if (!hasTeacher) continue
                    if (hasTSSource) {
                        val s =
                            interaTSSource.split(" ".toRegex()).toTypedArray()[0]
                        video.windowName =
                            Cache.getCache().getWindowsByIndex(s.toInt()).getWindowName()
                        video.selectedIndex = s.toInt()
                    } else {
                        val split1 =
                            tsSource[0].split(",".toRegex()).toTypedArray()[0]
                        video.windowName =
                            Cache.getCache().getWindowsByIndex(split1.toInt()).getWindowName()
                        video.selectedIndex = split1.toInt()
                    }
                    video.type = Constant.SOURCE_TEACHER
                    video.commandIndex = i + 1
                    videoList.add(video)
                } else if (i == 1) {  //学生画面
                    if (!hasStudent) continue
                    if (hasTSSource) {
                        if (hasTeacher) {
                            val s =
                                interaTSSource.split(" ".toRegex()).toTypedArray()[1]
                            video.windowName =
                                Cache.getCache().getWindowsByIndex(s.toInt()).getWindowName()
                            video.selectedIndex = s.toInt()
                        } else {
                            val s =
                                interaTSSource.split(" ".toRegex()).toTypedArray()[1]
                            video.windowName =
                                Cache.getCache().getWindowsByIndex(s.toInt()).getWindowName()
                            video.selectedIndex = s.toInt()
                        }
                    } else {
                        val split1 =
                            tsSource[1].split(",".toRegex()).toTypedArray()[0]
                        video.windowName =
                            Cache.getCache().getWindowsByIndex(split1.toInt()).getWindowName()
                    }
                    video.commandIndex = i + 1
                    video.type = Constant.SOURCE_STUDENT
                    videoList.add(video)
                } else if (i == 2) {  //电脑画面
                    val computerIndex = selectHdmi.toInt()
                    video.windowName =
                        Cache.getCache().getWindowsByIndex(computerIndex).getWindowName()
                    video.commandIndex = i + 1
                    video.type = Constant.SOURCE_COMPUTER
                    videoList.add(video)
                } else if (i == 3) {  //板书画面
                    video.windowName = "板书"
                    video.commandIndex = i + 1
                    videoList.add(video)
                }
            }
        }
        //TODO 不是云南的注释
//        if (isSip) {
//            val video1 = InteracVideoSource()
//            video1.windowName = "取消"
//            video1.commandIndex = 9
//            video1.type = Constant.SOURCE_CANCEL
//            videoList.add(video1)
//        }
        for (i1 in 0 until maxShow - 1) {
            val video = InteracVideoSource()
            video.windowName = "远程" + (i1 + 1)
            video.commandIndex = 5 + i1
            video.type = Constant.SOURCE_REMOTE
            videoList.add(video)
        }
        //TODO 不是云南的注释
//        if (isSip) {
//            val video = InteracVideoSource()
//            video.windowName = "远程4"
//            video.commandIndex = 8
//            video.type = Constant.SOURCE_REMOTE
//            videoList.add(video)
//        }
        return ListWrapper(videoList)
    }


    private fun parseTeachMode(
        teachMode: String,
        interac1V3: String,
        meetingInfo: String
    ): Int {
        var `val` = 0
        if ("" != teachMode) {
            `val` = teachMode.toInt()
        }
        val canInterac1V3 = canInterac1V3
        var isInnerCloud = false
        val s = meetingInfo.split(" ".toRegex()).toTypedArray()
        isInnerCloud = s.isNotEmpty() && s[0].length == 9
        return if (!canInterac1V3 && isInnerCloud) {
            2
        } else when (`val`) {
            0 -> 4
            1 -> 3
            2 -> 6
            else -> 4
        }
    }

    override var isResume: Boolean = false
        get() = field
        set(value) {field = value}

}