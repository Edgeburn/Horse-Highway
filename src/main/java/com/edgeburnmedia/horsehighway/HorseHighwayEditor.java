package com.edgeburnmedia.horsehighway;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
    private int hundreds;
    private int tens;
    private int ones;

    HorseHighwayEditor(HorseHighway plugin) {
        hundreds = 0;
        tens = 0;
        ones = 0;

        this.plugin = plugin;

        inventory = Bukkit.createInventory(null, 54, "Horse Highway Editor");

        initInventory();
    }


    public Inventory getInventory() {
        return inventory;
    }

    private void initInventory() {
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
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), DigitItem.getDigitItem(0, EditorOption.HUNDREDS_DIGIT));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), DigitItem.getDigitItem(0, EditorOption.TENS_DIGIT));
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), DigitItem.getDigitItem(0, EditorOption.ONES_DIGIT));
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

    public int getTotalValue() {
        return DigitItem.getValue(EditorOption.HUNDREDS_DIGIT, hundreds) + DigitItem.getValue(EditorOption.TENS_DIGIT, tens) + DigitItem.getValue(EditorOption.ONES_DIGIT, ones);
    }

    private void updateAcceptButton(Inventory inventory, Material material) {
        String newAcceptItemName = "§r§a§lAccept " + getTotalValue() + " kph for block " + material;
        ItemStack newAcceptItem = new ItemStack(Material.EMERALD_BLOCK, 1);
        ItemMeta meta = newAcceptItem.getItemMeta();
        meta.setDisplayName(newAcceptItemName);
        newAcceptItem.setItemMeta(meta);
        inventory.setItem(getIndexForClickedEditorOption(EditorOption.ACCEPT), newAcceptItem);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // checking equality of the inventories directly doesn't seem to work, so checking the titles.
        // permission check is to prevent a possible exploit where a player can name a chest "Horse Highway Editor"
        if (!e.getView().getTitle().equals("Horse Highway Editor") || !e.getWhoClicked().hasPermission("horsehighway.editor")) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        EditorOption clickedEditorOption = getClickedEditorOptionFromIndex(e.getRawSlot());
        Material itemEdited;
        try {
            itemEdited = e.getInventory().getItem(getIndexForClickedEditorOption(EditorOption.INSERT_POINT)).getType();
        } catch (NullPointerException ex) {
            itemEdited = Material.AIR;
        }
        // declare these as separate variables to keep the if statements more readable
        boolean clickedOnInsertPoint = clickedEditorOption == EditorOption.INSERT_POINT;
        boolean isClickWithinGUI = e.getRawSlot() <= 53;

        // cancel item clicks unless it's in the player inventory or in the insert point
        if ((!clickedOnInsertPoint) && isClickWithinGUI) {
            e.setCancelled(true);
        } else if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) { // we also don't want any shift clicking
            e.setCancelled(true);
        }

        // check if the action is any type of placement inside the inventory GUI unless the space clicked is
        if (!clickedOnInsertPoint && isClickWithinGUI && isPlacement(e.getAction())) {
            e.setCancelled(true);
        }

        // As of this point it has been guaranteed that the inventory is the editor

        Material materialEdited = e.getInventory().getItem(getIndexForClickedEditorOption(EditorOption.INSERT_POINT)).getType();

        switch (clickedEditorOption) {
            case INCREMENT_HUNDREDS:
                hundreds = increment(hundreds);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), DigitItem.getDigitItem(hundreds, EditorOption.HUNDREDS_DIGIT));
                break;
            case DECREMENT_HUNDREDS:
                hundreds = decrement(hundreds);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.HUNDREDS_DIGIT), DigitItem.getDigitItem(hundreds, EditorOption.HUNDREDS_DIGIT));
                break;
            case INCREMENT_TENS:
                tens = increment(tens);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), DigitItem.getDigitItem(tens, EditorOption.TENS_DIGIT));
                break;
            case DECREMENT_TENS:
                tens = decrement(tens);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.TENS_DIGIT), DigitItem.getDigitItem(tens, EditorOption.TENS_DIGIT));
                break;
            case INCREMENT_ONES:
                ones = increment(ones);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), DigitItem.getDigitItem(ones, EditorOption.ONES_DIGIT));
                break;
            case DECREMENT_ONES:
                ones = decrement(ones);
                e.getInventory().setItem(getIndexForClickedEditorOption(EditorOption.ONES_DIGIT), DigitItem.getDigitItem(ones, EditorOption.ONES_DIGIT));
                break;
            case CANCEL:
                Bukkit.getScheduler().runTask(plugin, () -> {
                   e.getView().close();
                });
            default:
                return;
        }

        player.playSound(e.getWhoClicked().getLocation(), "ui.button.click", 1, 1);
        updateAcceptButton(e.getInventory(), itemEdited);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // We need to clear the number stored by the Inventory
        hundreds = 0;
        tens = 0;
        ones = 0;
    }

    private static boolean isPlacement(InventoryAction action) {
        boolean isPlacement = false;
        InventoryAction[] placements = {InventoryAction.PLACE_ALL, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ONE, InventoryAction.MOVE_TO_OTHER_INVENTORY};

        for (InventoryAction inventoryAction : placements) {
            if (action == inventoryAction) {
                isPlacement = true;
            }
        }

        return isPlacement;
    }

    @EventHandler
    public void onInventoryMove(final InventoryDragEvent e) {
        if (e.getView().getTitle().equals("Horse Highway Editor")) {
            e.setCancelled(true);
        }
    }

    public enum EditorOption {
        INSERT_POINT, INCREMENT_HUNDREDS, DECREMENT_HUNDREDS, INCREMENT_TENS, DECREMENT_TENS, INCREMENT_ONES, DECREMENT_ONES, HUNDREDS_DIGIT, TENS_DIGIT, ONES_DIGIT, CANCEL, ACCEPT
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

    private static int getIndexForClickedEditorOption(EditorOption option) {
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

    private static int increment(int oldValue) {
        if (oldValue >= 9) {
            return 0;
        } else {
            return oldValue + 1;
        }
    }

    private static int decrement(int oldValue) {
        if (oldValue <= 0) {
            return 9;
        } else {
            return oldValue - 1;
        }
    }
}
