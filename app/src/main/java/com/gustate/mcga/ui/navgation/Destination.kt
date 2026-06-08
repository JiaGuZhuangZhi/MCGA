package com.gustate.mcga.ui.navgation

enum class Destination(val route: String) {
    MAIN(route = "main"),
    SYSTEMUI(route = "systemui"),
    SYSTEMUI_HOST(route = "systemui_host"),
    SYSTEMUI_TILE(route = "systemui/tile"),
    SYSTEMUI_NOTIFICATION(route = "systemui/notification"),
    SYSTEMUI_NOTIFICATION_CLOCK(route = "systemui/notification_clock")
}
