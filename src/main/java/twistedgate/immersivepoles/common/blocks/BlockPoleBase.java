package twistedgate.immersivepoles.common.blocks;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPoleBase extends IPBlock{
	
	private static final AxisAlignedBB BASE_SIZE=new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
	
	public BlockPoleBase(){
		super(Material.ROCK, "polebase");
		setResistance(3.0F);
		setHardness(2.0F);
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		return false;
	}
	
	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos){
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return BASE_SIZE;
	}
	
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack held=playerIn.getHeldItemMainhand();
			if(!held.isEmpty()){
				if(EnumPoleMaterial.isFenceItem(held)){
					
					if(!worldIn.isAirBlock(pos.offset(EnumFacing.UP))){
						IBlockState aboveState=worldIn.getBlockState(pos.offset(EnumFacing.UP));
						Block b=aboveState.getBlock();
						
						if(b instanceof BlockPole){
							BlockPole p=(BlockPole)b;
							
							if(p==IPStuff.woodPole && !held.isItemEqual(EnumPoleMaterial.WOOD.getFenceItem())){
								playerIn.sendMessage(new TextComponentString("Expected: "+EnumPoleMaterial.WOOD.getFenceItem().getDisplayName()+"."));
								return true;
								
							}else if(p==IPStuff.aluPole && !held.isItemEqual(EnumPoleMaterial.ALU.getFenceItem())){
								playerIn.sendMessage(new TextComponentString("Expected: "+EnumPoleMaterial.ALU.getFenceItem().getDisplayName()+"."));
								return true;
								
							}else if(p==IPStuff.steelPole && !held.isItemEqual(EnumPoleMaterial.STEEL.getFenceItem())){
								playerIn.sendMessage(new TextComponentString("Expected: "+EnumPoleMaterial.STEEL.getFenceItem().getDisplayName()+"."));
								return true;
							}
						}
					}
					
					for(int i=0;i<(worldIn.getActualHeight()-pos.getY());i++){
						BlockPos nPos=pos.add(0, i, 0);
						
						if(worldIn.isAirBlock(nPos)){
							
							IBlockState fb=null;
							if(held.isItemEqual(EnumPoleMaterial.WOOD.getFenceItem()))
								fb=IPStuff.woodPole.getDefaultState().withProperty(BlockPole.TYPE, EnumPoleType.POST_TOP);
							
							else if(held.isItemEqual(EnumPoleMaterial.ALU.getFenceItem()))
								fb=IPStuff.aluPole.getDefaultState().withProperty(BlockPole.TYPE, EnumPoleType.POST_TOP);
							
							else if(held.isItemEqual(EnumPoleMaterial.STEEL.getFenceItem()))
								fb=IPStuff.steelPole.getDefaultState().withProperty(BlockPole.TYPE, EnumPoleType.POST_TOP);
							
							if(worldIn.setBlockState(nPos, fb)){
								if(!playerIn.capabilities.isCreativeMode){
									held.shrink(1);
								}
							}
							
							return true;
						}else if(!(worldIn.getBlockState(nPos).getBlock() instanceof BlockPole || worldIn.getBlockState(nPos).getBlock()==this)){
							return true;
						}
					}
				}
			}
		}
		
		return Utils.isHammer(playerIn.getHeldItemMainhand()) || EnumPoleMaterial.isFenceItem(playerIn.getHeldItemMainhand());
	}
}
