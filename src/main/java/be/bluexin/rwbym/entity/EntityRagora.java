package be.bluexin.rwbym.entity;

import be.bluexin.rwbym.ModLootTables;
import be.bluexin.rwbym.RWBYModels;
import be.bluexin.rwbym.Init.RWBYItems;
import be.bluexin.rwbym.utility.RWBYConfig;
import be.bluexin.rwbym.utility.RWBYMath;
import be.bluexin.rwbym.weaponry.RWBYAmmoItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

public class EntityRagora extends EntityTameable {
	
	private Vec3d targetpos;
	
	private Vec3d[] ownertrace = new Vec3d[200];
	
	private AttackPhase attackphase = AttackPhase.MELEE;
	
    public EntityRagora(World world) {
        super(world);
        this.setSize(1F, 1F);
        this.setNoGravity(true);
        this.moveHelper = new RagoraMoveHelper(this);
        this.navigator = new PathNavigateFlying(this, world);
        this.experienceValue = 0;
        this.targetpos = new Vec3d(this.posX, this.posY, this.posZ);
    }
    
    public EntityRagora(World world, EntityPlayer player) {
    	this(world);
    	this.setTamedBy(player);
    	double x, y, z;
    	x = player.posX;
    	y = player.posY;
    	z = player.posZ;
    	y += 3;
    	this.setLocationAndAngles(x, y, z, player.rotationYaw, 0);
        this.targetpos = new Vec3d(this.posX, this.posY, this.posZ);
    }
    
    protected void initEntityAI() {
    	tasks.addTask(1, new EntityRagora.EntityAIFollowOwner(new BlockPos(0, 3, -2), this));
    	tasks.addTask(1, new EntityAIAttackMeleeWithRange(this, 0.3, true, 1) {
    		
    		private int timer = 0;
    		
    		@Override
    		public boolean shouldExecute() {
    			return super.shouldExecute() && EntityRagora.this.attackphase == AttackPhase.MELEE;
    		}
    		
    		@Override
    		public boolean shouldContinueExecuting() {
    			return super.shouldContinueExecuting() && EntityRagora.this.attackphase == AttackPhase.MELEE;
    		}
    		
    		@Override
    		public void startExecuting() {
    			super.startExecuting();
    		}
    		
    		@Override
    		public void updateTask() {
    			if (this.attacker.canEntityBeSeen(this.attacker.getAttackTarget())) {
    		        EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
    		        double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);
    				EntityLivingBase owner = EntityRagora.this.getOwner();
    				double angle;
    				if (owner == null) {
    					angle = this.attacker.getRNG().nextDouble() * 360;
    				}
    				else {
	    				double dx = entitylivingbase.posX - owner.posX;
	    				double dz = entitylivingbase.posZ - owner.posZ;
	    				angle = RWBYMath.atan2d(dx, dz);
    				}
    				double z = entitylivingbase.posZ + RWBYMath.cosd(angle) * (this.range + entitylivingbase.width);
    				double x = entitylivingbase.posX + RWBYMath.sind(angle) * (this.range + entitylivingbase.width);
    				double y = (entitylivingbase.height - this.attacker.height) / 2 + entitylivingbase.posY;
    				EntityRagora.this.setAIMoveSpeed(0.5F);
    				EntityRagora.this.targetpos = new Vec3d(x, y, z);
    				EntityRagora.this.navigator.clearPath();
    		        this.attackTick = Math.max(this.attackTick - 1, 0);
    		        this.checkAndPerformAttack(entitylivingbase, d0);
    			}
    			else {
    				super.updateTask();
    			}
    			
    			double dx = EntityRagora.this.getAttackTarget().posX - EntityRagora.this.posX;
    			double dz = EntityRagora.this.getAttackTarget().posZ - EntityRagora.this.posZ;
    			
    			EntityRagora.this.rotationYaw = (float) -RWBYMath.atan2d(dx, dz);
    			EntityRagora.this.getLookHelper().setLookPositionWithEntity(EntityRagora.this.getAttackTarget(), 0, 90);
    			if (timer < 400) {
    				timer++;
    				RWBYModels.LOGGER.info(timer);
    			}
    			else {
    				timer = this.attacker.getRNG().nextInt(100);
    				EntityRagora.this.attackphase = AttackPhase.RANGE;
    			}
    		}
    	});
    	tasks.addTask(1, new EntityAIAttackRange(this, 10, 5));
    	EntityAIBase temp = new EntityAIOwnerHurtByTarget(this) {
    		 @Override
    		public void updateTask() {
     	        EntityLivingBase entitylivingbase = EntityRagora.this.getOwner();
     	        if (entitylivingbase == null) {
     	        	return;
     	        }
     			EntityLivingBase target = entitylivingbase.getRevengeTarget();
     			if (target != this.taskOwner.getAttackTarget()) {
     				this.taskOwner.setAttackTarget(target);
     			}
 			}
    	};
    	temp.setMutexBits(0);
    	targetTasks.addTask(1, temp);
    	temp = new EntityAIOwnerHurtTarget(this) {
    		@Override
    		public void updateTask() {
    	        EntityLivingBase entitylivingbase = EntityRagora.this.getOwner();
    	        if (entitylivingbase == null) {
    	        	return;
    	        }
    			EntityLivingBase target = entitylivingbase.getLastAttackedEntity();
    			if (target != this.taskOwner.getAttackTarget()) {
    				this.taskOwner.setAttackTarget(target);
    			}
    		}
    	};
    	temp.setMutexBits(0);
    	targetTasks.addTask(2, temp);
    	temp = new EntityAINearestAttackableTarget(this, EntityMob.class, 20, true, false, new Predicate<EntityMob>() {
			@Override
			public boolean apply(EntityMob input) {
				if (input instanceof EntityPigZombie) {
					if (input.getAttackTarget() == EntityRagora.this || input.getAttackTarget() == EntityRagora.this.getOwner()) {
						return true;
					}
					return false;
				}
				return true;
			}
		}) {    		
    	    @Override
    	    protected AxisAlignedBB getTargetableArea(double targetDistance) {
    	    	EntityLivingBase owner = EntityRagora.this.getOwner();
    	        return owner == null ? new AxisAlignedBB(0, 0, 0, 0, 0, 0) : owner.getEntityBoundingBox().grow(targetDistance, targetDistance, targetDistance);
    	    }
    	};
    	temp.setMutexBits(0);
    	targetTasks.addTask(3, temp);
    }
    
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3499999940395355D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
    }
    
    @Override
    public void onEntityUpdate() {
    	super.onEntityUpdate();
    	EntityLivingBase owner = this.getOwner();
    	
    	if (owner != null) {
	    	for (int i = 199; i >= 0; i--) {
	    		if (i == 0) {
	    			ownertrace[0] = owner.getPositionVector().addVector(0, 0, 0);
	    		}
	    		else {
	    			ownertrace[i] = ownertrace[i - 1];
	    		}
	    	}
    	}
    	else {
    		this.setDead();
    	}
    }
    
    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));

        if (flag)
        {
            this.applyEnchantments(this, entityIn);
            if (entityIn instanceof EntityLiving) {
            	if (this.getRNG().nextInt(1) == 0) {
            		((EntityLiving)entityIn).setAttackTarget(this);
            	}
            }
        }

        return flag;
    }

    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ILLAGER;
    }

    protected ResourceLocation getLootTable() {
        return null;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    @Override
    protected net.minecraft.util.SoundEvent getDeathSound() {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.wolf.death"));
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }
    
    @Override
    public void fall(float distance, float damageMultiplier) {
    }
    
    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
    }
    
    private class EntityAIFollowOwner extends EntityAIBase {
    	
    	private EntityRagora entityragora;
    	
    	private BlockPos followpos;
    	
    	public EntityAIFollowOwner(BlockPos pos, EntityRagora entity) {
    		this.entityragora = entity;
    		this.followpos = pos;
    		this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute() {
			return entityragora.getAttackTarget() == null && entityragora.getOwner() != null;
		}
		
		@Override
		public void updateTask() {
			EntityPlayer owner = (EntityPlayer) entityragora.getOwner();
			
			double dx, dz;
			
			double yaw = -owner.rotationYaw;
			
			double z = followpos.getZ() * RWBYMath.cosd(yaw) - followpos.getX() * RWBYMath.sind(yaw);
			double x = followpos.getZ() * RWBYMath.sind(yaw) + followpos.getX() * RWBYMath.cosd(yaw);
			
			entityragora.setAIMoveSpeed(0.5F);
			Vec3d targetpos = new Vec3d(owner.posX + x, owner.posY + followpos.getY(), owner.posZ + z);
			
			dx = entityragora.targetpos.x - entityragora.posX;
			dz = entityragora.targetpos.z - entityragora.posZ;
			
			LABEL:
			if (!entityragora.canSeePos(targetpos)) {
				int cansee = 0;
				for (int i = 0; i < 200; i++) {
					Vec3d pos = ownertrace[i];
					if (pos != null && entityragora.canSeePos(pos)) {
						cansee++;
						if (cansee > 5) {
							entityragora.targetpos = pos;
							break LABEL;
						}
					}
					if (pos == null) {
						entityragora.targetpos = ownertrace[i - 1];
					}
				}
				if (cansee > 0) {
					entityragora.targetpos = ownertrace[199];
				}
			}
			else {
				entityragora.targetpos = targetpos;
				dx = entityragora.getOwner().posX - entityragora.posX;
				dz = entityragora.getOwner().posZ - entityragora.posZ;
			}
			
			entityragora.rotationYaw = (float) -RWBYMath.atan2d(dx, dz);
		}
    }
    
    public boolean canSeePos(Vec3d pos) {
        return this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), pos, false, true, false) == null;
    }
    
    private class RagoraMoveHelper extends EntityMoveHelper {

		public RagoraMoveHelper(EntityLiving entitylivingIn) {
			super(entitylivingIn);
		}
		
		@Override
		public void onUpdateMoveHelper() {
			double dx = EntityRagora.this.targetpos.x - EntityRagora.this.posX;
			double dy = EntityRagora.this.targetpos.y - EntityRagora.this.posY;
			double dz = EntityRagora.this.targetpos.z - EntityRagora.this.posZ;
			
			double a = 0.25;
			
			Vec3d v = new Vec3d(Math.abs(dx), Math.abs(dy), Math.abs(dz)).normalize().scale(EntityRagora.this.getAIMoveSpeed() * 2 / Math.sqrt(a));
						
			EntityRagora.this.motionX += MathHelper.clamp(dx, -v.x, v.x) * a - EntityRagora.this.motionX * 2 * Math.sqrt(a);
			EntityRagora.this.motionY += MathHelper.clamp(dy, -v.y, v.y) * a - EntityRagora.this.motionY * 2 * Math.sqrt(a);
			EntityRagora.this.motionZ += MathHelper.clamp(dz, -v.z, v.z) * a - EntityRagora.this.motionZ * 2 * Math.sqrt(a);
		}
		
		@Override
		public void setMoveTo(double x, double y, double z, double speedIn) {
			EntityRagora.this.setAIMoveSpeed((float) speedIn);
			EntityRagora.this.targetpos = new Vec3d(x, y, z);
		}
		
		public float turn(float currentangle, float targetangle, float maxdangle) {
			float dangle = targetangle - currentangle;
			dangle = normalize(dangle);
			dangle = MathHelper.clamp(dangle, -maxdangle, maxdangle);
			return currentangle + dangle;
		}
		
		public float normalize(float angle) {
			while (angle > 180) {
				angle -= 360;
			}
			while (angle <= -180) {
				angle += 360;
			}
			return angle;
		}
    }
    
    private class EntityAIAttackRange extends EntityAIBase {
    	
    	private EntityLivingBase entity;
    	
    	private float angle;
    	
    	private int direction;
    	
    	private float angVel = 1;
    	
    	private double radius, height;
    	
    	private int time;
    	
    	public EntityAIAttackRange(EntityLivingBase entityliving, double radius, double height) {
    		this.entity = entityliving;
    		this.radius = radius;
    		this.height = height;
    	}

		@Override
		public boolean shouldExecute() {
			return EntityRagora.this.getAttackTarget() != null && EntityRagora.this.attackphase == AttackPhase.RANGE;
		}
		
		@Override
		public void startExecuting() {
			
			Random rand = EntityRagora.this.getRNG();
			
			angle = rand.nextFloat() * 360;
			
			direction = rand.nextInt(2) * 2 - 1;
			
			time = rand.nextInt(20);
			
		}
		
		@Override
		public void updateTask() {
			
			double z = RWBYMath.cosd(angle) * radius + EntityRagora.this.getAttackTarget().posZ;
			double x = RWBYMath.sind(angle) * radius + EntityRagora.this.getAttackTarget().posX;
			double y = height + EntityRagora.this.getAttackTarget().posY;
			
			double dx = EntityRagora.this.getAttackTarget().posX - EntityRagora.this.posX;
			double dy = EntityRagora.this.getAttackTarget().posY - EntityRagora.this.posY;
			double dz = EntityRagora.this.getAttackTarget().posZ - EntityRagora.this.posZ;
			
			Vec3d look = new Vec3d(dx, dy, dz).normalize().scale(2).addVector(EntityRagora.this.getAttackTarget().motionX, EntityRagora.this.getAttackTarget().motionY, EntityRagora.this.getAttackTarget().motionZ);
			
			EntityRagora.this.rotationYaw = (float) -RWBYMath.atan2d(look.x, look.z);
			EntityRagora.this.getLookHelper().setLookPosition(look.x + EntityRagora.this.posX, look.y + EntityRagora.this.posY, look.z + EntityRagora.this.posZ, 0, 90);
			
			angle += angVel;
			
			EntityRagora.this.setAIMoveSpeed(0.5F);
			EntityRagora.this.targetpos = new Vec3d(x, y, z);
			
			time++;
			
			if (time > 100) {
				EntityBullet fireball = new EntityBullet(world, EntityRagora.this, (RWBYAmmoItem) RWBYItems.ragorafireball);
				Vec3d motion = EntityRagora.this.getLookVec().scale(2);
				fireball.motionX = look.x;
				fireball.motionY = look.y;
				fireball.motionZ = look.z;
				fireball.setNoGravity(true);
				fireball.setIsCritical(true);
				fireball.setParticle(EnumParticleTypes.DRAGON_BREATH);
				world.spawnEntity(fireball);
				EntityRagora.this.attackphase = AttackPhase.MELEE;
			}
			
		}
    	
    }
    
    private enum AttackPhase {
    	MELEE,
    	RANGE;
    }

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) {
		return null;
	}

}
