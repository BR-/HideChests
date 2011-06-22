package me.br_.minecraft.bukkit.hidechests;

import java.io.Serializable;

public class HCLocation implements Serializable {
	private static final long serialVersionUID = 1L;
	public int x;
	public int y;
	public int z;
	public String world;

	public HCLocation(int x, int y, int z, String world) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
}