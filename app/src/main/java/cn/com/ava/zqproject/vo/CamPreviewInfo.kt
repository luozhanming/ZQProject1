package cn.com.ava.zqproject.vo

import cn.com.ava.lubosdk.entity.PreviewVideoWindow

/**
 * 摄像头预览信息
 * */
data class CamPreviewInfo(
    val row: Int,/*预览行数*/
    val column: Int,/*预览列数*/
    val camCount: Int,/*摄像头个数*/
    val previewWindow: List<PreviewVideoWindow>,/*摄像头实体*/
    val curOutputIndex:Int/*当前输出*/
)
