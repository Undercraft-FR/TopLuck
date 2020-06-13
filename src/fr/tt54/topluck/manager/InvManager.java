package fr.tt54.topluck.manager;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.utils.ItemBuilder;
import fr.tt54.topluck.utils.MaterialType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InvManager {

    public static Inventory getTopLuckInventory() {
        TopLuckManager.saveTopLuck();
        Inventory inv = Bukkit.createInventory(null, 9 * 6, "§cTopLuck");
        List<Player> playersTemp = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Player> playersSorted = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        for (Player p : playersTemp) {
            Player pwin = p;
            for (Player p2 : playersTemp) {
                if (Double.parseDouble(TopLuckManager.getStonePercent(pwin)) > Double.parseDouble(TopLuckManager.getStonePercent(p2)) && !playersSorted.contains(p2)) {
                    pwin = p2;
                } else if (playersSorted.contains(p) && !playersSorted.contains(p2)) {
                    pwin = p2;
                }
            }
            playersSorted.add(pwin);
            players.add(pwin);
        }

        for (int i = 0; i < (Math.min(players.size(), 9 * 5)); i++) {
            ItemBuilder builder = new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal())).setName("§6" + (players.get(i)).getName()).setSkullOf(players.get(i))
                    .addLoreLine("§7Stone : §f" + TopLuckManager.getStonePercent(players.get(i)) + "% §8(" + TopLuckManager.getShowedStoneMined(players.get(i)) + ")");
            for (MaterialType type : TopLuckManager.blockCounted) {
                String name = Material.getMaterial(type.getId()).name().substring(0, 1).toUpperCase() + Material.getMaterial(type.getId()).name().toLowerCase().replace("_", " ").substring(1);
                if (type.getData() != 0)
                    name += ":" + type.getData();
                builder = builder.addLoreLine("§7" + name + " : §f" + TopLuckManager.getOrePercent(players.get(i), type) + "% §8(" + TopLuckManager.getShowedOreMined(players.get(i), type) + ")");
            }
            inv.setItem(i, builder.build());
        }

        return inv;
    }

    public static Inventory getTopLuckPlayerInventory(String playerName) {
        TopLuckManager.saveTopLuck();
        Inventory inv = Bukkit.createInventory(null, 9 * 6, "§cTopLuck §4" + playerName);

        ItemBuilder stoneBuilder = new ItemBuilder(Material.STONE);
        stoneBuilder.setName("§7Stone");
        stoneBuilder.addLoreLine(Main.getMessages().getMessage("inventory.lastconnection", "%percent%", TopLuckManager.getLastStonePercent(Bukkit.getPlayer(playerName)), "%number%", "" + TopLuckManager.getResourceMinedFromLastCo(Bukkit.getPlayer(playerName), new MaterialType(1, 0))));
        stoneBuilder.addLoreLine(Main.getMessages().getMessage("inventory.begin", "%percent%", TopLuckManager.getTotalStonePercent(Bukkit.getPlayer(playerName)), "%number%", "" + TopLuckManager.getTotalStoneMined(Bukkit.getPlayer(playerName))));
        inv.setItem(0, stoneBuilder.build());

        for (int i = 0; i < Math.min(9 * 5 - 1, TopLuckManager.blockCounted.size()); i++) {
            MaterialType type = TopLuckManager.blockCounted.get(i);
            ItemBuilder builder = new ItemBuilder(new ItemStack(Material.getMaterial(type.getId()), 1, (byte) type.getData()));

            String name = Material.getMaterial(type.getId()).name().substring(0, 1).toUpperCase() + Material.getMaterial(type.getId()).name().toLowerCase().replace("_", " ").substring(1);
            if (type.getData() != 0)
                name += ":" + type.getData();

            builder.setName("§7" + name);
            builder.addLoreLine(Main.getMessages().getMessage("inventory.lastconnection", "%percent%", TopLuckManager.getLastOrePercent(Bukkit.getPlayer(playerName), type), "%number%", "" + TopLuckManager.getResourceMinedFromLastCo(Bukkit.getPlayer(playerName), type)));
            builder.addLoreLine(Main.getMessages().getMessage("inventory.begin", "%percent%", TopLuckManager.getTotalOrePercent(Bukkit.getPlayer(playerName), type), "%number%", "" + TopLuckManager.getTotalOresMined(Bukkit.getPlayer(playerName), type)));
            inv.setItem(i + 1, builder.build());
        }

        inv.setItem(9 * 6 - 1, new ItemBuilder(Material.BARRIER).setName(Main.getMessages().getMessage("inventory.quit")).build());
        inv.setItem(49, new ItemBuilder(Material.ENDER_PEARL).setName(Main.getMessages().getMessage("inventory.teleport")).build());
        inv.setItem(48, new ItemBuilder(Material.CHEST).setName(Main.getMessages().getMessage("inventory.clear")).build());

        return inv;
    }
}
