package me.br_.minecraft.bukkit.hidechests;

import java.io.Serializable;

public class HCItemStack implements Serializable {
	private static final long serialVersionUID = 1L;
	public int id;
	public int amount;
	public short damage;

	public HCItemStack(int id, int amount, short damage) {
		this.id = id;
		this.amount = amount;
		this.damage = damage;
	}
}