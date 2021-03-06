package be.bluexin.rwbym.blocks;

import be.bluexin.rwbym.Init.RWBYItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class RWBYBlock extends BlockBase {
        public RWBYBlock(String name, Material mat, CreativeTabs tab, float hardness, float resistance, String tool, int harvest) {
                super(name, mat, tab, hardness, resistance, tool, harvest);
                this.setTickRandomly(true);
        }

        public Item getItemDropped(IBlockState state, Random rand, int fortune)
        {
                if (this == RWBYItems.rwbyblock1)
                {
                        return RWBYItems.gravitydustrock;
                }
                else if (this == RWBYItems.rwbyblock2)
                {
                        return RWBYItems.firedustrock;
                }else if (this == RWBYItems.rwbyblock3)
                {
                        return RWBYItems.winddustrock;
                }else if (this == RWBYItems.rwbyblock4)
                {
                        return RWBYItems.dustrock;
                }else if (this == RWBYItems.rwbyblock5)
                {
                        return RWBYItems.waterdustrock;
                }else if (this == RWBYItems.rwbyblock6)
                {
                        return RWBYItems.lightdustrock;
                }else if (this == RWBYItems.rwbyblock9)
                {
                        return RWBYItems.icedustrock;
                }
                else
                {
                        return this == Blocks.QUARTZ_ORE ? Items.QUARTZ : Item.getItemFromBlock(this);
                }
        }

        public int quantityDropped(Random random)
        {
                return 4 + random.nextInt(5);
        }

        public int quantityDroppedWithBonus(int fortune, Random random)
        {
                if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped((IBlockState)this.getBlockState().getValidStates().iterator().next(), random, fortune))
                {
                        int i = random.nextInt(fortune + 2) - 1;

                        if (i < 0)
                        {
                                i = 0;
                        }

                        return this.quantityDropped(random) * (i + 1);
                }
                else
                {
                        return this.quantityDropped(random);
                }
        }
}