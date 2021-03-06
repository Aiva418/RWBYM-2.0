package be.bluexin.rwbym.entity;

import be.bluexin.rwbym.Init.RWBYItems;
import be.bluexin.rwbym.ModLootTables;
import be.bluexin.rwbym.utility.RWBYConfig;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

import static sun.audio.AudioPlayer.player;

public class EntityApathy extends EntityMob {
    World world = null;
    private int cooldown = 0;

    public EntityApathy(World var3) {
        super(var3);
        world = var3;
        this.setSize(1.3F, 2.5F);
    }

    protected void initEntityAI() {
        super.initEntityAI();
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(4, new EntityAIAttackMeleeWithRange(this, 1.0D, false, 0.5F));
        this.tasks.addTask(8, new EntityAIWander(this, 0.6D));
        this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, EntityVindicator.class));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2499999940395355D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(RWBYConfig.aggrorange);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.cooldown --;
        if(cooldown <0){
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().grow(3,3,3);
            this.cooldown = 120;
            List<EntityPlayer> list = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);

            for (EntityPlayer entityplayer : list)
            {
                entityplayer.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 120, 1, true, true));
            }
        AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox().grow(10,10,10);
            this.cooldown = 120;
        List<EntityPlayer> list2 = this.world.<EntityPlayer>getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb2);

        for (EntityPlayer entityplayer : list2)
        {
            entityplayer.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120, 1, true, true));
            entityplayer.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120, 1, true, true));
        }}



    }



    protected ResourceLocation getLootTable() {
        return ModLootTables.Boarbatusk;
    }


    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ILLAGER;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SQUID_DEATH;
    }

    protected net.minecraft.util.SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ELDER_GUARDIAN_DEATH;
    }
    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

}
