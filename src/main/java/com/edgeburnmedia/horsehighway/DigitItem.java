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
    HorseHighwayEditor.EditorOption digitType;
    private int value;

    public static DigitItem fromValue(int value, HorseHighwayEditor.EditorOption digitType) {
        ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        DigitItem newDigitItem = new DigitItem(value, digitType);
        return newDigitItem.updatedItemDisplay();
    }

    public static DigitItem getDefaultDigitItem(HorseHighwayEditor.EditorOption digitType) {
        ItemStack newItem;
        ItemMeta meta;
        newItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        newItem.setAmount(1);
        meta = newItem.getItemMeta();
        meta.setDisplayName("§r§f0 " + digitType.name().toLowerCase(Locale.ROOT));
        DigitItem digitItem = new DigitItem(newItem, digitType);
        digitItem.value = 0;
        digitItem = digitItem.updatedItemDisplay();
        return digitItem;
    }

    // Used in fromValue() to have a DigitItem object to run updatedItemDisplay() with
    private DigitItem(int value, HorseHighwayEditor.EditorOption digitType) {
        super(Material.RED_STAINED_GLASS_PANE);
        this.value = value;
        this.digitType = digitType;

        // this section should never actually show in game
        Damageable meta;
        meta = (Damageable) getItemMeta();
        meta.setDamage(-999);
        meta.setDisplayName("§r§c§lError!");

        setItemMeta(meta);
    }

//    DigitItem(HorseHighwayEditor.EditorOption digitType) {
//        super(Material.GRAY_STAINED_GLASS_PANE, 1);
//        ItemMeta meta = getItemMeta();
//        setItemMeta(meta);
//        this.digitType = digitType;
//        meta.setDisplayName("§r§f0 " + getValueName());
//        this.value = 0;
//    }

    private DigitItem(ItemStack itemStack, HorseHighwayEditor.EditorOption digitType) {
        super(itemStack);
        this.digitType = digitType;
    }

    private DigitItem updatedItemDisplay() {
        ItemStack newItem;
        Damageable meta;

        switch (value) {
            case 0:
                newItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                newItem.setAmount(1);
                meta = (Damageable) newItem.getItemMeta();
                meta.setDamage(-100);
                meta.addEnchant(Enchantment.DIG_SPEED, 0, true);
                meta.setDisplayName("§r§f0 " + getValueName());
                break;
            case 1,2,3,4,5,6,7,8,9:
                newItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
                newItem.setAmount(value);
                meta = (Damageable) newItem.getItemMeta();
                meta.setDamage(100 - value);
                meta.addEnchant(Enchantment.DIG_SPEED, value, true);
                meta.setDisplayName("§r§f" + value + " " + getValueName());
                break;
            default:
                newItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                newItem.setAmount(64);
                meta = (Damageable) newItem.getItemMeta();
                meta.setDamage(-999);
                meta.setDisplayName("§r§c§lError! Tried to show " + value);
                break;
        }
        newItem.setItemMeta(meta);
        return new DigitItem(newItem, digitType);
    }

    public int getValue() {
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

    public int getValueFromEnchant() {
        ItemMeta meta = getItemMeta();
        int enchantmentLevel = meta.getEnchantLevel(Enchantment.DIG_SPEED);
        return enchantmentLevel;
    }

    public DigitItem increment() {
        if (value >= 9) {
            value = 0;
        } else {
            value++;
        }
        return updatedItemDisplay();
    }

    public DigitItem decrement() {
        if (value <= 0) {
            value = 9;
        } else {
            value--;
        }
        return updatedItemDisplay();
    }

    private String getValueName() {
        return switch (digitType) {
            case HUNDREDS_DIGIT -> "hundreds";
            case TENS_DIGIT -> "tens";
            case ONES_DIGIT -> "ones";
            default -> null;
        };
    }
}
