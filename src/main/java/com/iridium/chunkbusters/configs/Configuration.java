package com.iridium.chunkbusters.configs;

import com.iridium.iridiumcore.Item;
import com.iridium.iridiumcore.dependencies.xseries.XMaterial;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration {

    public String prefix = "<GRADIENT:FFB61D>&lIridiumChunkBusters</GRADIENT:FF711D> &8»";
    public String confirmationGUITitle = "&7Are you sure?";
    public String actionBarMessage = "&e&lY-Level: {ylevel}";
    public String dateTimeFormat = "EEEE, MMMM dd HH:mm:ss";

    public boolean startYWherePlaced = true;
    public boolean restoreChunkBusters = true;
    public boolean onlyRestoreWhenBlockIsAir = true;

    public int deleteInterval = 3;

    public Item chunkBuster = new Item(XMaterial.END_PORTAL_FRAME, 1, "&e&l>> Chunk Buster <<", Collections.singletonList("&7A {size}x{size} ChunkBuster"));
    public Item yes = new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 1, "&a&lYes", Collections.emptyList());
    public Item no = new Item(XMaterial.RED_STAINED_GLASS_PANE, 1, "&c&lNo", Collections.emptyList());
    public Item nextPage = new Item(XMaterial.GREEN_STAINED_GLASS_PANE, 1, "&a&lNext", Collections.emptyList());
    public Item previousPage = new Item(XMaterial.RED_STAINED_GLASS_PANE, 1, "&c&lPrevious", Collections.emptyList());
    public Item chunkBusterLog = new Item(XMaterial.PLAYER_HEAD, 0, 1, "&e&l{player}", "{player}", Arrays.asList("&7Size: {size}x{size}", "&7Time: {time}", "&7Chunk: {chunk}", "", "&e&l[!] &7Click to restore chunks"));

    public List<XMaterial> blacklist = Arrays.asList(XMaterial.BEDROCK, XMaterial.SPAWNER);

}
