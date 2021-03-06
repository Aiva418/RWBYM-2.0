package be.bluexin.rwbym.inventory;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class RWBYItemStackHandler extends ItemStackHandler {
	
	private List<String> validItems;
	
	public RWBYItemStackHandler(int slots, List<String> validItems) {
		super(slots);
		this.validItems = validItems;
	}
	
	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return validItems.contains(stack.getItem().getRegistryName().toString());
	}

}
