package com.iridium.chunkbusters.gui;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.IridiumChunkBusters;
import com.iridium.chunkbusters.utils.ItemStackUtils;
import com.iridium.chunkbusters.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

@Getter
public class ConfirmationGUI implements InventoryHolder {

    private final int size;
    private final Location location;
    @Setter
    private boolean activated;

    public ConfirmationGUI(int size, Location location) {
        this.size = size;
        this.location = location;
        this.activated = false;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 27, StringUtils.color(IridiumChunkBusters.getInstance().getConfiguration().confirmationGUITitle));
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, XMaterial.GRAY_STAINED_GLASS_PANE.parseItem());
        }
        inventory.setItem(11, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().no));
        inventory.setItem(15, ItemStackUtils.makeItem(IridiumChunkBusters.getInstance().getConfiguration().yes));
        return inventory;
    }
}
