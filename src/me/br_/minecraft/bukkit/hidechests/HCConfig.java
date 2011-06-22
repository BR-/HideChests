package me.br_.minecraft.bukkit.hidechests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class HCConfig implements Runnable {
	public enum Status {
		ADDING, REMOVING, PERSISTENT
	}

	private static final int delay = 600;
	private static final Material block = Material.DIRT;
	private static final Set<Player> adding = new HashSet<Player>();
	private static final Set<Player> removing = new HashSet<Player>();
	private static final Set<Player> persistent = new HashSet<Player>();
	private static final Map<Location, ItemStack[]> chests = new HashMap<Location, ItemStack[]>();
	private static final Set<Location> revert = new HashSet<Location>();
	private final File config;

	public HCConfig(File config) {
		this.config = config;
	}

	public static int getDelay() {
		return delay;
	}

	public static Material getBlock() {
		return block;
	}

	public static void addPlayer(Player p, Status s) {
		switch (s) {
		case ADDING:
			adding.add(p);
			removing.remove(p);
			break;
		case REMOVING:
			removing.add(p);
			adding.remove(p);
			break;
		case PERSISTENT:
			persistent.add(p);
			break;
		}
	}

	public static void removePlayer(Player p) {
		adding.remove(p);
		removing.remove(p);
		persistent.remove(p);
	}

	public static boolean isAdding(Player p) {
		return adding.contains(p);
	}

	public static boolean isRemoving(Player p) {
		return removing.contains(p);
	}

	public static boolean isPersistent(Player p) {
		return persistent.contains(p);
	}

	public static boolean isChest(Location loc) {
		return chests.containsKey(loc);
	}

	public static void addInventory(Location loc) {
		chests.put(loc, ((Chest) loc.getBlock().getState()).getInventory()
				.getContents());
		revert.remove(loc);
	}

	public static void restoreInventory(Location loc) {
		((Chest) loc.getBlock().getState()).getInventory().setContents(
				chests.get(loc));
		chests.remove(loc);
		revert.add(loc);
	}

	public void run() {
		for (Location loc : new HashSet<Location>(revert)) {
			if (loc.getBlock().getType() == Material.CHEST) {
				addInventory(loc);
				((Chest) loc.getBlock().getState()).getInventory().clear();
				loc.getBlock().setType(Material.DIRT);
			}
		}
		try {
			config.getParentFile().mkdirs();
			config.createNewFile();
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
					config));
			Map<HCLocation, HCItemStack[]> m = new HashMap<HCLocation, HCItemStack[]>();
			for (Location loc : chests.keySet()) {
				HCItemStack[] is = new HCItemStack[chests.get(loc).length];
				int x = 0;
				for (ItemStack i : chests.get(loc)) {
					if (i == null) {
						is[x] = null;
					} else {
						is[x] = new HCItemStack(i.getTypeId(), i.getAmount(),
								i.getDurability());
					}
					x++;
				}
				m.put(new HCLocation(loc.getBlockX(), loc.getBlockY(), loc
						.getBlockZ(), loc.getWorld().getName()), is);
			}
			out.writeObject(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void load() {
		for (Location loc : revert) {
			if (loc.getBlock().getType() == Material.CHEST) {
				restoreInventory(loc);
			}
		}
		try {
			if (!config.exists()) {
				return;
			}
			ObjectInput in = new ObjectInputStream(new FileInputStream(config));
			Object read = in.readObject();
			if (read instanceof Map) {
				Map m = (Map) read;
				boolean keyhcl = true;
				for (Object key : m.keySet()) {
					if (!(key instanceof HCLocation)) {
						keyhcl = false;
					}
				}
				if (keyhcl) {
					boolean valuehis = true;
					for (Object value : m.values()) {
						if (!(value instanceof HCItemStack[])) {
							valuehis = false;
						}
					}
					if (valuehis) {
						chests.clear();
						for (HCLocation hcl : ((Map<HCLocation, HCItemStack[]>) m)
								.keySet()) {
							HCItemStack[] his = ((Map<HCLocation, HCItemStack[]>) m)
									.get(hcl);
							ItemStack[] is = new ItemStack[his.length];
							int x = 0;
							for (HCItemStack h : his) {
								if (h == null) {
									is[x] = null;
								} else {
									is[x] = new ItemStack(h.id, h.amount,
											h.damage);
								}
								x++;
							}
							chests.put(new Location(Bukkit.getServer()
									.getWorld(hcl.world), hcl.x, hcl.y, hcl.z),
									is);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
}