package be.bluexin.rwbym.capabilities.Ruby;

import be.bluexin.rwbym.Init.RWBYItems;
import be.bluexin.rwbym.capabilities.Aura.AuraProvider;
import be.bluexin.rwbym.capabilities.Aura.IAura;
import be.bluexin.rwbym.RWBYModels;
import be.bluexin.rwbym.client.particle.RosePetal;
import be.bluexin.rwbym.entity.EntityBeowolf;
import be.bluexin.rwbym.entity.EntityMutantDeathStalker;
import be.bluexin.rwbym.utility.RWBYConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Iterator;
import java.util.List;

public class Ruby implements IRuby {

	private static final int MAX_LEVEL = 3;

	private int invisiblityTimer = 0;
			
	private boolean active = false;
	
	private int level1Time = 50;
	
	private int maxUseTime = 360;
	
	private float auraUse = 0.1F;
	
	// a level greater than 0 will signal that this is the active semblance
	private int level = 0;
	
	/**activates the semblance*/
	@Override
	public boolean onActivate(EntityPlayer player) {
		
		//System.out.println("" + this.level);

		switch(this.level) {
		case 1:
			if (player.onGround){
				this.setInvisisbility(level1Time);
				this.active = true;
				return true;
			}
		case 2:
		case 3:
			this.active = true;
			return true;
		default:
			return false;
		}
	}
	
	@Override
	public boolean deActivate(EntityPlayer player) {
		
		switch(level) {
		case 1:
			return false;
		case 2:
		case 3:
			this.active = false;
			return true;
		}
		return false;
	}

	/**sets the invisibility for level one*/
	@Override
	public void setInvisisbility(int time) {
		this.invisiblityTimer = time;
	}
	
	@Override
	public void setInvisisbilityTimer(int time) {
		this.invisiblityTimer = time;
	}

	@Override
	public void setCooldownTimer(int time) {

	}

	/**currently not used*/
	@Override
	public void setPlayerY(double Y) {

	}
	
	/**updates the main logic of the semblance*/
	@Override
	public void onUpdate(EntityPlayer player) {

		IAura aura = player.getCapability(AuraProvider.AURA_CAP, null);

		//System.out.println(cooldown);
		
		if (this.active) {
			
			if (!this.useAura(player, auraUse)) return;
			
			player.fallDistance = 0;
			
			if (player.onGround || this.level > 1) {
				//System.out.println(this.cooldown);
				
				boolean flag = player.isElytraFlying();
				
				Vec3d look = player.getLookVec();
				
				//how much speed to keep
				double drag = 0.1D;
				//comment out this line to go back to normal
				player.motionX /= flag ? 0.99 : 0.91;
				player.motionY /= 0.98;
				player.motionZ /= flag ? 0.99 : 0.91;
				if (!flag) player.motionY += 0.08;
				Vec3d motion = look.scale(Math.sqrt(player.motionX * player.motionX + player.motionY * player.motionY + player.motionZ * player.motionZ));
				player.motionX = Math.abs(motion.x) < Math.abs(look.x) ? look.x : player.motionX + (look.x - player.motionX) * drag;
				player.motionY = Math.abs(motion.y) < Math.abs(look.y) ? look.y : player.motionY + (look.y - player.motionY) * drag;
				player.motionZ = Math.abs(motion.z) < Math.abs(look.z) ? look.z : player.motionZ + (look.z - player.motionZ) * drag;
				player.fallDistance = 0;

				if(this.level > 2){
					AxisAlignedBB axisalignedbb = player.getEntityBoundingBox().grow(2,2,2);
	
	
					List<Entity> list1 = player.world.getEntitiesWithinAABBExcludingEntity(player, axisalignedbb);
	
					if (!list1.isEmpty())
					{
						double y = Math.pow(player.motionY, 2);
						double x = Math.pow(player.motionX, 2);
						double z = Math.pow(player.motionZ, 2);
	
						double d3 = Math.sqrt(x+y+z);
						float f = (float)d3;
	
						for (Entity entity : list1)
						{
							if (entity instanceof EntityLivingBase) {
								EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
								entitylivingbase.attackEntityFrom(new EntityDamageSource("rose petal", player), f*10);
							}
						}
					}
				}

				for (int i = 0; i < (this.level > 1 ? 32 : 2); i++) {
					ItemStack is = player.getHeldItemMainhand();
					ItemStack is2 = player.getHeldItemOffhand();
					if(is.getItem() == RWBYItems.crescentfrost){
						RWBYModels.proxy.generateSummerpetals(player);
					}else if(is.getItem() == RWBYItems.crescentgunfrost){
						RWBYModels.proxy.generateSummerpetals(player);
					}else if(is2.getItem() == RWBYItems.crescentfrost){
						RWBYModels.proxy.generateSummerpetals(player);
					}else if(is2.getItem() == RWBYItems.crescentgunfrost){
						RWBYModels.proxy.generateSummerpetals(player);
					}else RWBYModels.proxy.generateRosepetals(player);
				}
			}
		}

		switch(this.level) {
		case 1:
			if (this.invisiblityTimer > 0) {
				this.invisiblityTimer--;
			}
			else {
				this.active = false;
			}
			break;
		case 2:
		case 3:
			break;
		}
	}
	
	@Override
	public int getInvisibilityTimer() {
		return this.invisiblityTimer;
	}
	
	@Override
	public int getCooldown() {
		return 0;
	}

	@Override
	public boolean getInvisibility() {
		switch(this.level) {
		case 1: 
			return false;
		case 2:
		case 3:
			return this.active;
		default:
			return false;
		}
	}
	
	@Override
	public boolean getActiveStatus() {
		return this.active;
	}

	@Override
	public double getPlayerY() {
		return 0;
	}
	
	@Override
	public int getLevel() {
		return this.level;
	}
	
	@Override
	public void setLevel(int level) {
		if (level > MAX_LEVEL) {
			return;
		}
		this.level = level;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Itimer", invisiblityTimer);
		nbt.setInteger("level", level);
		nbt.setBoolean("active", active);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.invisiblityTimer = nbt.getInteger("Itimer");
		this.level = nbt.getInteger("level");
		this.active = nbt.getBoolean("active");
	}

	@Override
	public Capability getCapability() {
		return RubyProvider.RUBY_CAP;
	}
	
	@Override
	public String toString() {
		return "Ruby";
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean isInvisible() {
		switch(this.level) {
		case 1: 
			return false;
		case 2:
		case 3:
			return this.active;
		default:
			return false;
		}
	}

	@Override
	public float[] getColor() {
		float color[] = new float[3];
		color[0] = 0.6F;
		color[1] = 0.1F;
		color[2] = 0.1F;
		return color;
	}
	
}
