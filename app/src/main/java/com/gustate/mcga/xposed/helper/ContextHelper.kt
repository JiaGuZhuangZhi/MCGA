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

package com.gustate.mcga.xposed.helper

import android.content.Context
import com.gustate.mcga.xposed.helper.ClassHelper.callStaticMethod
import com.gustate.mcga.xposed.helper.ClassHelper.loadClass

object ContextHelper {

    /**
     * 取当前 Context
     * @param classLoader [ClassLoader] 实例
     */
    fun getContext(classLoader: ClassLoader): Context {
        val activityThread = loadClass(
            name = "android.app.ActivityThread",
            loader = classLoader
        )
        return activityThread.callStaticMethod(
            name = "currentApplication",
            classLoader = classLoader
        )
    }

}