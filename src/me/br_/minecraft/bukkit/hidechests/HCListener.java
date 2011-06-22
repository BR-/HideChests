package me.br_.minecraft.bukkit.hidechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;

class HCListener extends BlockListener {
	private HCMain main;

	public HCListener(HCMain main) {
		this.main = main;
	}

	public void onBlockBreak(BlockBreakEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (HCConfig.isChest(e.getBlock().getLocation())) {
			e.setCancelled(true);
			e.getBlock().setType(Material.CHEST);
			HCConfig.restoreInventory(e.getBlock().getLocation());
			Bukkit.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(main,
							new HCTimer(e.getBlock().getLocation()),
							HCConfig.getDelay());
		}
	}

	public void onBlockDamage(BlockDamageEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getBlock().getType() == Material.CHEST
				&& HCConfig.isAdding(e.getPlayer())) {
			HCConfig.addInventory(e.getBlock().getLocation());
			((Chest) e.getBlock().getState()).getInventory().clear();
			e.getBlock().setType(Material.DIRT);
			e.getPlayer().sendMessage("[HideChests] Hidden chest added.");
			e.setCancelled(true);
		} else if (e.getBlock().getType() == HCConfig.getBlock()
				&& HCConfig.isRemoving(e.getPlayer())) {
			e.getBlock().setType(Material.CHEST);
			HCConfig.restoreInventory(e.getBlock().getLocation());
			e.getPlayer().sendMessage("[HideChests] Hidden chest removed.");
			e.setCancelled(true);
		} else {
			return;
		}
		if (!HCConfig.isPersistent(e.getPlayer())) {
			HCConfig.removePlayer(e.getPlayer());
		}
	}

	private class HCTimer implements Runnable {
		Location loc;

		public HCTimer(Location loc) {
			this.loc = loc;
		}

		public void run() {
			if (loc != null && loc.getBlock().getType() == Material.CHEST) {
				HCConfig.addInventory(loc);
				((Chest) loc.getBlock().getState()).getInventory().clear();
				loc.getBlock().setType(HCConfig.getBlock());
			}
		}
	}
}