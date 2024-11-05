package fr.tt54.topluck.cmd;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.manager.InvManager;
import fr.tt54.topluck.manager.TopLuckManager;
import fr.tt54.topluck.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdTopLuck implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        System.out.println("onCommand called with args: " + Arrays.toString(args));

        if (!(sender instanceof Player)) {
            System.out.println("Command sender is not a player");
            if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
                Main.getInstance().reload();
                System.out.println(Main.getMessages().getMessage("reload"));
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (Permission.hasPermission(player, "topluck.reload")) {
                        player.sendMessage(Main.getMessages().getMessage("reload"));
                    }
                }
                return true;
            }
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }

        Player playerSender = (Player) sender;
        System.out.println("Command sender is a player: " + playerSender.getName());

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                System.out.println("Reload command received");
                if (Permission.hasPermission(sender, "topluck.reload")) {
                    if (args.length != 1) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " reload"));
                        return false;
                    }
                    Main.getInstance().reload();
                    System.out.println(Main.getMessages().getMessage("reload"));
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (Permission.hasPermission(player, "topluck.reload")) {
                            player.sendMessage(Main.getMessages().getMessage("reload"));
                        }
                    }
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("registerblock")) {
                System.out.println("Register block command received");
                if (!Permission.hasPermission(sender, "topluck.see")) {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }

                int displayId = playerSender.getItemInHand().getTypeId();
                if (args.length == 2) {
                    try {
                        displayId = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " " + args[0] + " [DisplayItemID]"));
                        return false;
                    }
                }

                if (args.length > 2) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " " + args[0] + " [DisplayItemID]"));
                    return false;
                }

                if (playerSender.getItemInHand() == null || playerSender.getItemInHand().getType() == Material.AIR) {
                    sender.sendMessage(Main.getMessages().getMessage("notiteminhand"));
                    return false;
                }

                TopLuckManager.addResource(playerSender.getItemInHand(), displayId);
                sender.sendMessage(Main.getMessages().getMessage("blockregistered", "%type%", playerSender.getItemInHand().getType().name(), "%data%", "" + playerSender.getItemInHand().getData().getData()));

                return true;
            } else {
                System.out.println("Player inventory command received");
                if (Permission.hasPermission(sender, "topluck.see")) {
                    if (args.length != 1) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " [joueur]"));
                        return false;
                    }

                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Main.getMessages().getMessage("notconnected"));
                        return false;
                    }

                    playerSender.openInventory(InvManager.getTopLuckPlayerInventory(args[0]));
                    sender.sendMessage(Main.getMessages().getMessage("playertopluckopened", "%player%", args[0]));
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }
            }
        }

        if (Permission.hasPermission(sender, "topluck.see")) {
            playerSender.openInventory(InvManager.getTopLuckInventory(0));
            sender.sendMessage(Main.getMessages().getMessage("topluckopened"));
            return true;
        } else {
            sender.sendMessage(Main.getMessages().getMessage("notpermission"));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> msg = new ArrayList<>();
        if (args.length == 1) {
            if (Permission.hasPermission(sender, "topluck.reload") && "reload".startsWith(args[0]))
                msg.add("reload");

            if (Permission.hasPermission(sender, "topluck.registerblock") && "registerblock".startsWith(args[0]))
                msg.add("registerblock");

            if (Permission.hasPermission(sender, "topluck.see")) {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    String name = player.getName();
                    if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                        msg.add(name);
                    }
                }
            }
        }
        return (msg.isEmpty()) ? Collections.emptyList() : msg;
    }
}
