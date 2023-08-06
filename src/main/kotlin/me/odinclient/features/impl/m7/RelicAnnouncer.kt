package me.odinclient.features.impl.m7

import me.odinclient.features.Category
import me.odinclient.features.Module
import me.odinclient.features.settings.impl.SelectorSetting
import me.odinclient.utils.skyblock.ChatUtils
import me.odinclient.utils.skyblock.ChatUtils.unformattedText
import me.odinclient.utils.skyblock.dungeon.DungeonUtils
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object RelicAnnouncer : Module(
    name = "Relic Announcer",
    description = "Automatically announce your relic to the rest of the party",
    category = Category.M7
) {
    private val colors = arrayListOf("Green", "Purple", "Blue", "Orange", "Red")
    private val selected: Int by SelectorSetting("Color", "Green", colors)

    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        if (!DungeonUtils.inDungeons) return
        if (event.unformattedText !== "[BOSS] Necron: All this, for nothing...") return
        ChatUtils.partyMessage("${colors[selected]} Relic")
    }
}