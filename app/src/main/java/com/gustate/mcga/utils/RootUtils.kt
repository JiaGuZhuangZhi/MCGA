package com.gustate.mcga.utils

import com.gustate.mcga.R
import com.gustate.mcga.data.model.RootManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RootUtils {

    /**
     * Root 权限是否可用 (检查连通性)
     * @return Boolean
     */
    suspend fun isRootAvailable(): Boolean = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val output = executeRootCommand(command = "echo Connected")
            output == "Connected"
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 获取 Root 管理器信息
     * @see RootManager
     */
    suspend fun getRootManager(): RootManager = withContext(context = Dispatchers.IO) {
        return@withContext try {
            val ksuVer = executeRootCommand(command = "/data/adb/ksud -V")
            val magiskVer = executeRootCommand(command = "magisk -v")
            if (ksuVer?.isNotEmpty() == true) {
                RootManager(
                    rootManagerName = R.string.kernelsu,
                    rootManagerVer = ksuVer
                )
            } else if (magiskVer?.isNotEmpty() == true) {
                RootManager(
                    rootManagerName = R.string.magisk,
                    rootManagerVer = magiskVer
                )
            } else {
                RootManager(
                    rootManagerName = R.string.unknown_root_manager,
                    rootManagerVer = "0"
                )
            }
        } catch (_: Exception) {
            RootManager(
                rootManagerName = R.string.unknown_root_manager,
                rootManagerVer = "0"
            )
        }
    }

    /**
     * 使用 root 权限执行 shell 命令
     * @param command 除权限声明外的指令, 默认为检测 su 连通性 (正常返回 "Connected")
     * @return String 执行结果
     */
    suspend fun executeRootCommand(
        command: String = "echo Connected"
    ): String? = withContext(context = Dispatchers.IO) {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("su", "-c", command))
            // 结果
            val output = process
                .inputStream
                .bufferedReader()
                .use {
                    it.readText().trim()
                }
            // 退出值
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                return@withContext output
            } else {
                return@withContext null
            }
        } catch (_: Exception) {
            return@withContext null
        } finally {
            // 显式销毁
            process?.destroy()
        }
    }

}