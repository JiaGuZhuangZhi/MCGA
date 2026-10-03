package com.gustate.mcga.data.setting.about.group

import androidx.annotation.StringRes
import com.gustate.mcga.R
import com.gustate.mcga.data.setting.base.LinkSetting
import com.gustate.mcga.data.setting.base.SettingGroup
import com.gustate.mcga.data.setting.base.SettingNode

object AcknowledgmentsGroup : SettingGroup(
    title = R.string.acknowledgments,
    dependency = null,
    children = getReportAndAnswerNode()
)

private fun getReportAndAnswerNode(): List<SettingNode> {
    val nodeList = mutableListOf<SettingNode>()
    acknowledgementsList.forEach {
        nodeList.add(
            element = LinkSetting(
                url = it.link,
                title = it.name,
                summary = it.describe,
                icon = R.drawable.mobile_share,
            )
        )
    }
    return nodeList
}

private val acknowledgementsList = listOf(
    LinkInfo(
        name = R.string.tikaliu,
        describe = R.string.tikaliu_des,
        link = "https://www.coolapk.com/u/36684465"
    ),
    LinkInfo(
        name = R.string.lamprose,
        describe = R.string.lamprose_des,
        link = "https://github.com/lamprose"
    ),
    LinkInfo(
        name = R.string.jx,
        describe = R.string.jx_des,
        link = "https://www.coolapk.com/u/2713396"
    ),
    LinkInfo(
        name = R.string.mjw,
        describe = R.string.mjw_des,
        link = "https://www.coolapk.com/u/3242606"
    ),
    LinkInfo(
        name = R.string.qq_8272,
        describe = R.string.qq_8272_des,
        link = "https://github.com/Perpetual-Memories"
    ),
    LinkInfo(
        name = R.string.qq_7829,
        describe = R.string.qq_7829_des,
        link = null
    ),
    LinkInfo(
        name = R.string.qq_2808,
        describe = R.string.qq_2808_des,
        link = null
    ),
    LinkInfo(
        name = R.string.qq_2775,
        describe = R.string.qq_2775_des,
        link = null
    )
)

private data class LinkInfo(
    @field:StringRes val name: Int,
    @field:StringRes val describe: Int,
    val link: String? = null
)
