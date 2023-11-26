package me.odinmain.utils.skyblock

import me.odinmain.OdinMain.mc
import me.odinmain.utils.noControlCodes
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import java.awt.Color

object ItemUtils {

    /**
     * Returns the ExtraAttribute Compound
     */
    val ItemStack?.extraAttributes: NBTTagCompound?
        get() {
            return this?.getSubCompound("ExtraAttributes", false)
        }

    /**
     * Returns displayName without control codes.
     */
    val ItemStack.unformattedName: String
        get() = this.displayName.noControlCodes

    /**
     * Returns the lore for an Item
     */
    val ItemStack.lore: List<String>
        get() = this.tagCompound?.getCompoundTag("display")?.getTagList("Lore", 8)?.let {
            List(it.tagCount()) { i -> it.getStringTagAt(i) }
        } ?: emptyList()

    /**
     * Returns Item ID for an Item
     */
    val ItemStack?.itemID: String
        get() = this?.extraAttributes?.getString("id") ?: ""


    inline val heldItem: ItemStack?
        get() = mc.thePlayer?.heldItem


     /**
     * Returns if an item has an ability
     */
    val ItemStack?.hasAbility: Boolean
        get() {
            val lore = this?.lore
            lore?.forEach{
                if (it.contains("Ability:") && it.endsWith("RIGHT CLICK")) return true
            }
            return false
        }
     /**
     * Returns if an item is a shortbow
     */
    val ItemStack?.isShortbow: Boolean
        get() {
            return this?.lore?.any { it.contains("Shortbow: Instantly shoots!") } == true
        }

    val EntityPlayerSP.holdingEtherWarp: Boolean
        get() = this.heldItem?.itemID == "ETHER_WARP"

    /**
     * Returns the ID of held item
     */
    fun isHolding(id: String): Boolean =
        mc.thePlayer?.heldItem?.itemID == id

    /**
     * Returns first slot of an Item
     */
    fun getItemSlot(item: String, ignoreCase: Boolean = true): Int? =
        mc.thePlayer.inventory.mainInventory.indexOfFirst { it?.unformattedName?.contains(item, ignoreCase) == true }.takeIf { it != -1 }

    /**
     * Gets index of an item in a chest.
     * @return null if not found.
     * @return null
     */
    fun getItemIndexInContainerChest(container: ContainerChest, item: String, ignoreCase: Boolean): Int? {
        return container.inventorySlots.subList(0, container.inventory.size - 36).firstOrNull {
            it.stack?.unformattedName?.contains(item, ignoreCase) == true
        }?.slotNumber
    }

    enum class ItemRarity(
        val loreName: String,
        val colorCode: String,
        val color: Color
    ) {
        COMMON("COMMON", "§f", Color.WHITE),
        UNCOMMON("UNCOMMON", "§2", Color.GREEN),
        RARE("RARE", "§9", Color.BLUE),
        EPIC("EPIC", "§5", Color.MAGENTA),
        LEGENDARY("LEGENDARY", "§6", Color.ORANGE),
        MYTHIC("MYTHIC", "§d", Color.PINK),
        DIVINE("DIVINE", "§b", Color.CYAN),
        SPECIAL("SPECIAL", "§c", Color.RED),
        VERY_SPECIAL("VERY SPECIAL", "§c", Color.RED);
    }

    private val rarityRegex: Regex = Regex("§l(?<rarity>[A-Z]+) ?(?<type>[A-Z ]+)?(?:§[0-9a-f]§l§ka)?$")

    /**
     * Gets the rarity of an item
     * @param lore Lore of an item
     */
    fun getRarity(lore: List<String>): ItemRarity? {
        // Start from the end since the rarity is usually the last line or one of the last.
        for (i in lore.indices.reversed()) {
            val currentLine = lore[i]
            val match = rarityRegex.find(currentLine) ?: continue
            val rarity: String = match.groups["rarity"]?.value ?: continue
            return ItemRarity.entries.find { currentLine.noControlCodes.startsWith(it.loreName) }
        }
        return null
    }
}