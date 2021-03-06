package be.bluexin.rwbym.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Level;

import be.bluexin.rwbym.RWBYModels;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IRWBYContainerFactory {
	
	public static <T extends IRWBYContainerFactory> T createInstance(Class<T> clazz, IInventory playerInv, ItemStack stack) {
		try {
			Constructor<T> constructor = clazz.getConstructor(IInventory.class, ItemStack.class);
			return constructor.newInstance(playerInv, stack);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			RWBYModels.LOGGER.log(Level.FATAL, "Unable to Create Container Instance: {}", clazz);
			e.printStackTrace();
		}
		return null;
	}
	
}
