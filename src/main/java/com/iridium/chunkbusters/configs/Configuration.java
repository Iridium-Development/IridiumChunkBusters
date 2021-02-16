package com.iridium.chunkbusters.configs;

import com.cryptomorin.xseries.XMaterial;
import com.iridium.chunkbusters.Item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration {

    public String prefix = "&e&lChunkBusters &8»";
    public String confirmationGUITitle = "&7Are you sure?";
    public String actionBarMessage = "&e&lY-Level: {ylevel}";

    public boolean startYWherePlaced = true;

    public int deleteInteval = 3;

    public Item chunkBuster = new Item(XMaterial.END_PORTAL_FRAME, 1, "&e&l>> Chunk Buster <<", Collections.singletonList("&7A {size}x{size} ChunkBuster"));
    public Item yes = new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 1, "&a&lYes", Collections.emptyList());
    public Item no = new Item(XMaterial.RED_STAINED_GLASS_PANE, 1, "&c&lNo", Collections.emptyList());

    public List<XMaterial> blacklist = Arrays.asList(XMaterial.BEDROCK, XMaterial.SPAWNER);

}
