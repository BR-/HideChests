package me.br_.minecraft.bukkit.hidechests;

import java.io.File;

import me.br_.minecraft.bukkit.hidechests.HCConfig.Status;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HCMain extends JavaPlugin {
	public void onDisable() {
		new HCConfig(new File(this.getDataFolder(), "chests.dat")).run();
		System.out.println("[HideChests] Disabled.");
	}

	public void onEnable() {
		new HCConfig(new File(this.getDataFolder(), "chests.dat")).load();
		this.getServer()
				.getScheduler()
				.scheduleSyncRepeatingTask(
						this,
						new HCConfig(new File(this.getDataFolder(),
								"chests.dat")), 1200, 1200);
		HCListener list = new HCListener(this);
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, list, Event.Priority.High,
				this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, list, Event.Priority.Normal,
				this);
		System.out.println("[HideChests] Enabled.");
	}

	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (sender instanceof ConsoleCommandSender) {
			if (args.length == 1 && args[0].equalsIgnoreCase("save")) {
				new HCConfig(new File(this.getDataFolder(), "chests.dat"))
						.run();
				sender.sendMessage("[HideChests] Saved!");
			} else {
				sender.sendMessage("[HideChests] You cannot do that!");
			}
			return true;
		}
		if (args.length > 2) {
			return false;
		} else if (args.length == 0) {
			HCConfig.removePlayer((Player) sender);
			sender.sendMessage("[HideChests] You are not doing anything.");
		} else if (args.length == 2) {
			if (("add".equalsIgnoreCase(args[0]) && "remove"
					.equalsIgnoreCase(args[1]))
					|| ("add".equalsIgnoreCase(args[1]) && "remove"
							.equalsIgnoreCase(args[0]))) {
				return false;
			}
			onCommand(sender, command, label, new String[] { args[0] });
			onCommand(sender, command, label, new String[] { args[1] });
		} else if ("add".equalsIgnoreCase(args[0])) {
			HCConfig.addPlayer((Player) sender, Status.ADDING);
			sender.sendMessage("[HideChests] You are now adding.");
		} else if ("remove".equalsIgnoreCase(args[0])) {
			HCConfig.addPlayer((Player) sender, Status.REMOVING);
			sender.sendMessage("[HideChests] You are now removing.");
		} else if ("persist".equalsIgnoreCase(args[0])) {
			HCConfig.addPlayer((Player) sender, Status.PERSISTENT);
			sender.sendMessage("[HideChests] You are now persistent.");
		} else if ("save".equalsIgnoreCase(args[0]) && sender.isOp()) {
			new HCConfig(new File(this.getDataFolder(), "chests.dat")).run();
			sender.sendMessage("[HideChests] Saved!");
		}
		return true;
	}
}