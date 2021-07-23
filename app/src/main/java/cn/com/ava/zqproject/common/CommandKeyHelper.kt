package cn.com.ava.zqproject.common

import cn.com.ava.lubosdk.util.GsonUtil
import cn.com.ava.zqproject.vo.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

object CommandKeyHelper {

    private val mGson: Gson by lazy {
        val type = object : TypeToken<List<CommandButton>>() {}.type
        GsonBuilder().registerTypeAdapter(type, object : TypeAdapter<List<CommandButton>>() {
            override fun write(out: JsonWriter, value: List<CommandButton>?) {
                value?.apply {
                    out.beginArray()
                    forEach {
                        out.beginObject()
                        out.name("type").value(it.type)
                        if (it is LayoutButton) {
                            out.name("layoutIndex").value(it.layoutIndex)
                        } else if (it is VideoWindowButton) {
                            out.name("windowIndex").value(it.windowIndex)
                        } else if (it is VideoPresetButton) {
                            out.name("videoWindowIndex").value(it.videoWindowIndex)
                            out.name("presetIndex").value(it.presetIndex)
                        }
                        out.endObject()
                    }
                    out.endArray()
                }

            }
            override fun read(reader: JsonReader): List<CommandButton> {
                reader.beginArray()
                reader.isLenient = true
                val buttons = arrayListOf<CommandButton>()
                while (reader.hasNext()) {
                    val nextToken = reader.peek()
                    if (nextToken == JsonToken.BEGIN_OBJECT) {
                        reader.beginObject()
                        val name = reader.nextName()
                        if (name == "type") {
                            val type = reader.nextInt()
                            if (type == TYPE_VIDEO) {
                                val name = reader.nextName()
                                val windowIndex = reader.nextInt()
                                val button = VideoWindowButton(windowIndex)
                                buttons.add(button)
                            } else if (type == TYPE_VIDEO_LAYOUT) {
                                val name = reader.nextName()
                                val layoutIndex = reader.nextInt()
                                val button = LayoutButton(layoutIndex)
                                buttons.add(button)
                            } else if (type == TYPE_PRE_STAGE) {
                                val name = reader.nextName()
                                var videoWindowIndex= reader.nextInt()
                                val name1 = reader.nextName()
                                var presetIndex =reader.nextInt()
                                val button = VideoPresetButton(videoWindowIndex, presetIndex)
                                buttons.add(button)
                            }
                        }
                        reader.endObject()
                    }
                }
                reader.endArray()
                return buttons
            }

        }).create()
    }


    fun getSelectedCommandKeys(): List<CommandButton> {
        //从SharePreference中获取json
        val keyJson = CommonPreference.getElement(CommonPreference.KEY_SELECTED_COMMAND_KEY, "")
        val type = object : TypeToken<List<CommandButton>>() {}.type
        val keys = mGson.fromJson<List<CommandButton>>(keyJson, type)
        if (keys != null) {
            return keys
        }
        return emptyList()
    }


    fun setSelectedCommandKeys(buttons: List<CommandButton>) {
        val type = object : TypeToken<List<CommandButton>>() {}.type
        val toJson = mGson.toJson(buttons,type)
        CommonPreference.putElement(CommonPreference.KEY_SELECTED_COMMAND_KEY, toJson)
    }


}