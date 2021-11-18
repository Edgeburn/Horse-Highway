package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/*
* Item locations
* top insertion point = 10
* left insertion point = 18
* right insertion point = 20
* bottom insertion point = 28
*
* insertion point = 19
*
* increase = 14, 15, 16
* decrease = 32, 33, 34
*
* cancel = 51
* accept = 53
*/



/*
* Work In Progress
* Lots of bugs to solve with this, so still being worked on
 */
public class HorseHighwayEditor implements Listener {
    private HorseHighway plugin;
    private final Inventory inventory;
    private DigitItem hundredsDigitItem;
    private DigitItem tensDigitItem;
    private DigitItem onesDigitItem;
    private int hundreds;
    private int tens;
    private int ones;

    HorseHighwayEditor(HorseHighway plugin) {
        this.plugin = plugin;

        inventory = Bukkit.createInventory(null, 54, "Horse Highway Editor");


        initInventory();
    }


    public Inventory getInventory() {
        return inventory;
    }

    private void initInventory() {
        hundredsDigitItem = DigitItem.getDefaultDigitItem(EditorOption.HUNDREDS_DIGIT);
        tensDigitItem = DigitItem.getDefaultDigitItem(EditorOption.TENS_DIGIT);
        onesDigitItem = DigitItem.getDefaultDigitItem(EditorOption.ONES_DIGIT);

        // insert point border
        inventory.setItem(10, generateItem(Material.GREEN_STAINED_GLASS_PANE, "---"));
        inventory.setItem(18, generateItem(Material.GREEN_STAINED_GLASS_PANE, "---"));
        inventory.setItem(20, generateItem(Material.GREEN_STAINED_GLASS_PANE, "---"));
        inventory.setItem(28, generateItem(Material.GREEN_STAINED_GLASS_PANE, "---"));

        // increase
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.INCREMENT_HUNDREDS), generateItem(Material.EMERALD_BLOCK, "§r§a§lIncrease Hundreds"));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.INCREMENT_TENS), generateItem(Material.EMERALD_BLOCK, "§r§a§lIncrease Tens"));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.INCREMENT_ONES), generateItem(Material.EMERALD_BLOCK, "§r§a§lIncrease Ones"));

        // decrease
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.DECREMENT_HUNDREDS), generateItem(Material.REDSTONE_BLOCK, "§r§c§lDecrease Hundreds"));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.DECREMENT_TENS), generateItem(Material.REDSTONE_BLOCK, "§r§c§lDecrease Tens"));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.DECREMENT_ONES), generateItem(Material.REDSTONE_BLOCK, "§r§c§lDecrease Ones"));

        // cancel and accept
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.CANCEL), generateItem(Material.REDSTONE_BLOCK, "§r§c§lCancel"));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.ACCEPT), generateItem(Material.EMERALD_BLOCK, "§r§a§lAccept"));

        // digits
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), hundredsDigitItem);
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), tensDigitItem);
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), onesDigitItem);
    }

    private ItemStack generateItem(Material material, String name) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    public void openInventory(Player player) {
        player.openInventory(this.getInventory());
    }

    public int getValue() {
        return getHundredsDigitItem().getValue() + getTensDigitItem().getValue() + getOnesDigitItem().getValue();
    }

    private void updateAcceptButton(Inventory inventory, Material material) {
        String newAcceptItemName = "§r§a§lAccept " + getValue() + " kph for block " + material;
        ItemStack newAcceptItem = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta meta = newAcceptItem.getItemMeta();
        meta.setDisplayName(newAcceptItemName);
        newAcceptItem.setItemMeta(meta);
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.ACCEPT), newAcceptItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        // checking equality of the inventories directly doesn't seem to work, so checking the titles.
        // permission check is to prevent a possible
        if (!e.getView().getTitle().equals("Horse Highway Editor") || !e.getWhoClicked().hasPermission("horsehighway.editor")) {
            return;
        }

//        e.getWhoClicked().sendMessage(String.valueOf(e.getRawSlot()));
        Player player = (Player) e.getWhoClicked();
        EditorOption clickedEditorOption = getClickedEditorOptionFromIndex(e.getRawSlot());

        // cancel item clicks unless it's in the player inventory or in the insert point
        if ((clickedEditorOption != EditorOption.INSERT_POINT) && e.getRawSlot() <= 53) {
            e.setCancelled(true);
        } else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
            e.setCancelled(true);
        }

        ItemStack clickedItemStack;
        DigitItem newDigit;
        DigitItem oldDigit = null;
        int enchantLevel = -1;

//        if (e.getInventory().getItem(e.getRawSlot()) != null) {
//            Bukkit.getServer().broadcastMessage("clicked was not null");
//            ItemMeta meta;
//            clickedItemStack = e.getInventory().getItem(e.getRawSlot());
//            meta = clickedItemStack.getItemMeta();
//            enchantLevel = meta.getEnchantLevel(Enchantment.DIG_SPEED);
//            Bukkit.getServer().broadcastMessage("enchant found was " + enchantLevel);
//        }
//
//        Bukkit.getServer().broadcastMessage("after if, enchantLevel is " + enchantLevel);

        switch (clickedEditorOption) {
            case INCREMENT_HUNDREDS:
                enchantLevel = hundredsDigitItem.getValueFromEnchant();
                oldDigit = DigitItem.fromValue(enchantLevel, EditorOption.HUNDREDS_DIGIT);
                newDigit = oldDigit.increment();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.HUNDREDS_DIGIT);
                break;
            case DECREMENT_HUNDREDS:
                newDigit = oldDigit.decrement();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.HUNDREDS_DIGIT);
                break;
            case INCREMENT_TENS:
                newDigit = oldDigit.increment();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.TENS_DIGIT);
                break;
            case DECREMENT_TENS:
                newDigit = oldDigit.decrement();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.TENS_DIGIT);
                break;
            case INCREMENT_ONES:
                newDigit = oldDigit.increment();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.ONES_DIGIT);
                break;
            case DECREMENT_ONES:
                newDigit = oldDigit.decrement();
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), newDigit);
                updateDigit(newDigit, EditorOption.ONES_DIGIT);
                break;
        }

    }

    @EventHandler
    public void onInventoryMove(final InventoryDragEvent e) {
        if (!e.getView().getTitle().equals("Horse Highway Editor") || !e.getWhoClicked().hasPermission("horsehighway.editor")) {
            e.setCancelled(true);
        }
    }

    public enum EditorOption {
        INSERT_POINT, INCREMENT_HUNDREDS, DECREMENT_HUNDREDS, INCREMENT_TENS, DECREMENT_TENS, INCREMENT_ONES, DECREMENT_ONES, HUNDREDS_DIGIT, TENS_DIGIT, ONES_DIGIT, CANCEL, ACCEPT
    }

    private void updateDigit(DigitItem item, EditorOption digit) {
        switch (digit) {
            case HUNDREDS_DIGIT -> hundredsDigitItem = item;
            case TENS_DIGIT -> tensDigitItem = item;
            case ONES_DIGIT -> onesDigitItem = item;
        }
    }

    private EditorOption getClickedEditorOptionFromIndex(int index) {
        return switch (index) {
            case 19 -> EditorOption.INSERT_POINT;
            case 14 -> EditorOption.INCREMENT_HUNDREDS;
            case 15 -> EditorOption.INCREMENT_TENS;
            case 16 -> EditorOption.INCREMENT_ONES;
            case 32 -> EditorOption.DECREMENT_HUNDREDS;
            case 33 -> EditorOption.DECREMENT_TENS;
            case 34 -> EditorOption.DECREMENT_ONES;
            case 51 -> EditorOption.CANCEL;
            case 53 -> EditorOption.ACCEPT;
            case 23 -> EditorOption.HUNDREDS_DIGIT;
            case 24 -> EditorOption.TENS_DIGIT;
            case 25 -> EditorOption.ONES_DIGIT;
            default -> null;
        };
    }

    private int getIndexForClickedEditorOption(EditorOption option) {
        return switch (option) {
            case INSERT_POINT -> 19;
            case INCREMENT_HUNDREDS -> 14;
            case INCREMENT_TENS -> 15;
            case INCREMENT_ONES -> 16;
            case DECREMENT_HUNDREDS -> 32;
            case DECREMENT_TENS -> 33;
            case DECREMENT_ONES -> 34;
            case CANCEL -> 51;
            case ACCEPT -> 53;
            case HUNDREDS_DIGIT -> 23;
            case TENS_DIGIT -> 24;
            case ONES_DIGIT -> 25;
            default -> -999;
        };
    }

    public DigitItem getHundredsDigitItem() {
        return hundredsDigitItem;
    }

    public DigitItem getTensDigitItem() {
        return tensDigitItem;
    }

    public DigitItem getOnesDigitItem() {
        return onesDigitItem;
    }
}
