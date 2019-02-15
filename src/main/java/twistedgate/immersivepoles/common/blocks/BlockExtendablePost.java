package twistedgate.immersivepoles.common.blocks;

import blusunrize.immersiveengineering.api.IPostBlock;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration1;
import blusunrize.immersiveengineering.common.blocks.wooden.BlockTypes_WoodenDecoration;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twistedgate.immersivepoles.ImmersivePoles;
import twistedgate.immersivepoles.util.BlockUtilities;

/**
 * IGNORE THIS
 * 
 * @author TwistedGate
 */
public class BlockExtendablePost extends IPBlock implements IPostBlock{
	
	public static final PropertyEnum<EnumPoleType> TYPE=PropertyEnum.create("type", EnumPoleType.class);
	public static final PropertyEnum<EnumPoleMaterial> MAT=PropertyEnum.create("mat", EnumPoleMaterial.class);
	public static final PropertyEnum<EnumMetaType> META=PropertyEnum.create("meta", EnumMetaType.class);
	
	public BlockExtendablePost() {
		super(Material.WOOD, "extendablepost");
		setHardness(2.0F);
		
		setDefaultState(this.blockState.getBaseState()
				.withProperty(TYPE, EnumPoleType.ARM)
				.withProperty(MAT, EnumPoleMaterial.WOOD)
				.withProperty(META, EnumMetaType.POST_BASE)
				);
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, new IProperty<?>[]{
			TYPE,
			MAT,
			META,
		});
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		if(meta<0 || meta>=EnumMetaType.values().length){
			return getDefaultState().withProperty(META, EnumMetaType.POST_BASE)
			.withProperty(MAT, EnumPoleMaterial.WOOD);
			
		}else{
			IBlockState state=getDefaultState()
					.withProperty(META, EnumMetaType.values()[meta]);
			
			switch(meta){
				case 0: // Default
				case 1:case 2:case 3:{ // Wood
					state=state.withProperty(MAT, EnumPoleMaterial.WOOD);
					break;
				}
				case 4:case 5:case 6:{ // Aluminium
					state=state.withProperty(MAT, EnumPoleMaterial.ALU);
					break;
				}
				case 7:case 8:case 9:{ // Steel
					state=state.withProperty(MAT, EnumPoleMaterial.STEEL);
					break;
				}
			}
			
			return state;
		}
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(META).ordinal();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos){
		return state;
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack held=playerIn.getHeldItem(EnumHand.MAIN_HAND);
			if(!held.isEmpty()){
				if(isFence(held)){
					ImmersivePoles.log.debug(state+" fenced with "+getType(held));
					
					for(int i=0;i<(worldIn.getActualHeight()-pos.getY());i++){
						BlockPos nPos=pos.add(0, i, 0);
						
						if(worldIn.isAirBlock(nPos)){
							if(worldIn.setBlockState(nPos, IEContent.blockWoodenDecoration.getStateFromMeta(BlockTypes_WoodenDecoration.FENCE.getMeta()), 3)){
								
								if(!playerIn.capabilities.isCreativeMode){
									held.shrink(1);
								}
								
								ImmersivePoles.log.debug("Added pole.");
							}
							return true;
						}else if(worldIn.getBlockState(nPos).getBlock()!=this){
							ImmersivePoles.log.debug("Ignoring.");
							return true;
						}
					}
					
				}else if(Utils.isHammer(held)){
					ImmersivePoles.log.debug("Hammered.");
					
					EnumMetaType m=state.getValue(META);
					if(m==EnumMetaType.POST || m==EnumMetaType.POST_TOP){
						int side=facing.ordinal();
						BlockPos tmp=pos.add(facing.getDirectionVec());
						
						if(side>1 && worldIn.isAirBlock(tmp)){
							worldIn.setBlockState(tmp, this.getStateFromMeta(1+side), 3);
							ImmersivePoles.log.debug("trigger0");
						}
						
					}else if(m!=EnumMetaType.POST_BASE){
						worldIn.setBlockToAir(pos);
						ImmersivePoles.log.debug("trigger1");
					}
				}
			}
		}
		
		return isValid(playerIn.getHeldItem(EnumHand.MAIN_HAND));
	}
	
	/*
	 * i originaly planned to use the offhand too, but i decided against it for simplicitys sake
	 * it's the only reason the generic methods isFence and isValid exist.
	 */
	private boolean isValid(ItemStack stack){
		if(stack.isEmpty()) return false;
		
		return Utils.isHammer(stack) || isFence(stack);
	}
	
	private boolean isFence(ItemStack stack){
		if(!stack.isEmpty()){
			if(stack.isItemEqual(new ItemStack(IEContent.blockWoodenDecoration, 1, BlockTypes_WoodenDecoration.FENCE.getMeta())))
				return true;
			
			if(stack.isItemEqual(new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.ALUMINUM_FENCE.getMeta())))
				return true;
			
			if(stack.isItemEqual(new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.STEEL_FENCE.getMeta())))
				return true;
		}
		
		return false;
	}
	
	private EnumPoleMaterial getType(ItemStack stack){
		if(!stack.isEmpty()){
			if(stack.isItemEqual(new ItemStack(IEContent.blockWoodenDecoration, 1, BlockTypes_WoodenDecoration.FENCE.getMeta())))
				return EnumPoleMaterial.WOOD;
			
			if(stack.isItemEqual(new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.ALUMINUM_FENCE.getMeta())))
				return EnumPoleMaterial.ALU;
			
			if(stack.isItemEqual(new ItemStack(IEContent.blockMetalDecoration1, 1, BlockTypes_MetalDecoration1.STEEL_FENCE.getMeta())))
				return EnumPoleMaterial.STEEL;
		}
		
		return null;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		poke(worldIn, pos);
	}
	
	private void poke(IBlockAccess world, BlockPos curPos){
		EnumMetaType thisMeta=world.getBlockState(curPos).getValue(META);
		
		if(thisMeta==EnumMetaType.POST || thisMeta==EnumMetaType.POST_TOP){
			IBlockState state=world.getBlockState(curPos);
			if(BlockUtilities.getBlockFromDirection(world, curPos, EnumFacing.DOWN)!=this){
				dropBlockAsItem((World) world, curPos, state, 1);
				((World)world).setBlockToAir(curPos);
				return;
			}
		}
		
		BlockPos abovePos=curPos.offset(EnumFacing.UP);
		Block above=BlockUtilities.getBlockFrom(world, abovePos);
		int aboveMeta=above.getMetaFromState(world.getBlockState(abovePos));
		
		if(above==IEContent.blockWoodenDecoration && aboveMeta==BlockTypes_WoodenDecoration.FENCE.getMeta()){
			BlockUtilities.setBlockStateAtDirection(((World)world), curPos, this.getStateFromMeta(1), EnumFacing.UP, 2);
			if(thisMeta==EnumMetaType.POST_TOP){
				((World)world).setBlockState(curPos, this.getStateFromMeta(1), 3);
			}
			
		}else if(thisMeta==EnumMetaType.POST && above.isAir(world.getBlockState(abovePos), world, abovePos)){
			((World)world).setBlockState(curPos, this.getStateFromMeta(2), 3);
			
		}else if(thisMeta.ordinal()>EnumMetaType.POST_TOP.ordinal()){
			int side=thisMeta.ordinal()-1;
			EnumFacing f=EnumFacing.values()[side].getOpposite();
			if(BlockUtilities.getBlockFromDirection(world, curPos, f)!=this){
				((World)world).setBlockToAir(curPos);
			}
		}
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		int meta=state.getValue(META).ordinal();
		
		if(meta==0) super.getDrops(drops, world, pos, state, fortune);
		if(meta<3){
			drops.add(new ItemStack(IEContent.blockWoodenDecoration, 1, BlockTypes_WoodenDecoration.FENCE.getMeta()));
		}
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos){
		return stateBounds(worldIn, pos, blockState);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
		return stateBounds(worldIn, pos, state);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return stateBounds(source, pos, state);
	}
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity){
		return state.getValue(META).ordinal()>0;
	}
	
	@Override
	public boolean canConnectTransformer(IBlockAccess world, BlockPos pos){
		return world.getBlockState(pos).getValue(META).ordinal()>0;
	}
	
	// #####################################################################
	
	@SuppressWarnings("deprecation")
	boolean canArmConnectToBlock(IBlockAccess world, BlockPos pos, boolean down){
		if(world.isAirBlock(pos) || BlockUtilities.getBlockFrom(world, pos) instanceof IPostBlock) return false;
		
		Block b=BlockUtilities.getBlockFrom(world, pos);
		if(down){
			return b.getBoundingBox(world.getBlockState(pos), world, pos).maxY>=1;
		}else{
			return b.getBoundingBox(world.getBlockState(pos), world, pos).minY>=0;
		}
	}
	
	AxisAlignedBB stateBounds(IBlockAccess world, BlockPos pos, IBlockState state){
		EnumMetaType meta=state.getValue(META);
		switch(meta){
			case POST_BASE:{
				return new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
			}
			case POST:case POST_TOP:{
				return new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
			}
			case ARM_NORTH:case ARM_EAST:case ARM_SOUTH:case ARM_WEST:{
				float minY=0.34375F;
				float maxY=1.0F;
				
				float minX=meta==EnumMetaType.ARM_WEST?-0.25F:0.3125F;
				float maxX=meta==EnumMetaType.ARM_SOUTH? 1.25F:0.6875F;
				
				float minZ=meta==EnumMetaType.ARM_EAST?-0.25F:0.3125F;
				float maxZ=meta==EnumMetaType.ARM_NORTH? 1.25F:0.6875F;
				
				if(canArmConnectToBlock(world, pos.offset(EnumFacing.DOWN), true)){
					minY=0.0F;
					maxY=0.65625F;
					
					if(canArmConnectToBlock(world, pos.offset(EnumFacing.UP), false))
						maxY=1.0F;
				}
				
				return new AxisAlignedBB(minX,minY,minZ,maxX,maxY,maxZ);
			}
			default:return new AxisAlignedBB(pos);
		}
	}
}
