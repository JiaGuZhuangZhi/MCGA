package com.gustate.mcga.xposed.base

interface HookFeature<in C> {

    /**
     * 首次安装 Hook
     */
    fun install(config: C)

    /**
     * 配置变化时更新
     */
    fun update(config: C)

}