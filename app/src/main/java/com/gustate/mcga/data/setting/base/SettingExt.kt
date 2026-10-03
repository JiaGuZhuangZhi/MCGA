/**
 * MCGA (Make Color Great Again) - A Free and Open-Source Xposed Module for ColorOS Users
 *
 * Copyright (C) 2026 Zhuangzhi Meng (Gustate XiaoMeng)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.gustate.mcga.data.setting.base

import android.content.SharedPreferences

object SettingExt {

    /**
     * 监听设置组中的配置变化
     * @param group 设置组
     * @param onChanged 配置变化回调
     * @return [SharedPreferences.OnSharedPreferenceChangeListener]
     */
    fun SharedPreferences.listenConfig(
        group: SettingGroup,
        onChanged: () -> Unit
    ): SharedPreferences.OnSharedPreferenceChangeListener {
        val keys = group.getSettingKeys()
        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key in keys) {
                    onChanged()
                }
            }
        registerOnSharedPreferenceChangeListener(listener)
        return listener
    }

    /**
     * 获取设置项的值。
     * @param setting 设置节点
     * @return 设置值
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> SharedPreferences.getValue(
        setting: SettingNode
    ): T = when (setting) {
        is BooleanSetting ->
            getBoolean(setting.key, setting.default)

        is FloatSetting ->
            getFloat(setting.key, setting.default)

        is IntSetting ->
            getInt(setting.key, setting.default)

        is ColorSetting ->
            getInt(setting.key, setting.default)

        else -> Unit
    } as T

    /**
     * 获取设置项的可选值
     * 设置项不存在时返回 null
     * @param setting 设置节点
     * @return 设置值 未保存时返回 null
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> SharedPreferences.getValueOrNull(
        setting: SettingNode
    ): T? {
        if (setting !is SettingItem<*>)
            return null
        if (!contains(setting.key))
            return null
        return when (setting) {
            is BooleanSetting ->
                getBoolean(setting.key, setting.default)

            is FloatSetting ->
                getFloat(setting.key, setting.default)

            is IntSetting ->
                getInt(setting.key, setting.default)

            is ColorSetting ->
                getInt(setting.key, setting.default)

            else ->
                return null
        } as T
    }

    /**
     * 获取设置组中的全部设置项
     * 会递归遍历嵌套的设置组
     * @return 设置项列表
     */
    fun SettingGroup.getSettings(): List<SettingItem<*>> =
        children.flatMap { node ->
            when (node) {
                is SettingItem<*> ->
                    listOf(node)

                is SettingGroup ->
                    node.getSettings()
            }
        }

    /**
     * 获取设置组中的全部设置项键值
     * @return 设置项键值集合
     */
    fun SettingGroup.getSettingKeys(): Set<String> {
        return getSettings().mapTo(
            destination = linkedSetOf()
        ) { it.key }
    }
}