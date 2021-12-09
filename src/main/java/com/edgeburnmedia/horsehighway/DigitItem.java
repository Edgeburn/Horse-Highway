package com.edgeburnmedia.horsehighway;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;


/*
* Work In Progress
* Lots of bugs to solve with this, so still being worked on
*/
public class DigitItem extends ItemStack {
    private DigitItem(ItemStack itemStack, HorseHighwayEditor.EditorOption digitType) {
        super(itemStack);
    }

    public static DigitItem getDigitItem(int value, HorseHighwayEditor.EditorOption digitType) {
        ItemStack newItem;
        ItemMeta meta;

        switch (value) {
            case 0:
                newItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                newItem.setAmount(1);
                meta = newItem.getItemMeta();
                meta.setDisplayName("§r§f0 " + getValueName(digitType));
                break;
            case 1,2,3,4,5,6,7,8,9:
                newItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                newItem.setAmount(value);
                meta = newItem.getItemMeta();
                meta.setDisplayName("§r§f" + value + " " + getValueName(digitType));
                break;
            default:
                newItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                newItem.setAmount(64);
                meta = newItem.getItemMeta();
                meta.setDisplayName("§r§c§lError! Tried to show " + value);
                break;
        }
        newItem.setItemMeta(meta);
        return new DigitItem(newItem, digitType);
    }

    public static int getValue(HorseHighwayEditor.EditorOption digitType, int value) {
        switch (digitType) {
            case HUNDREDS_DIGIT:
                return value * 100;
            case TENS_DIGIT:
                return value * 10;
            case ONES_DIGIT:
                return value;
            default:
                return 0;
        }
    }

    private static String getValueName(HorseHighwayEditor.EditorOption digitType) {
        return switch (digitType) {
            case HUNDREDS_DIGIT -> "hundreds";
            case TENS_DIGIT -> "tens";
            case ONES_DIGIT -> "ones";
            default -> null;
        };
    }
}
