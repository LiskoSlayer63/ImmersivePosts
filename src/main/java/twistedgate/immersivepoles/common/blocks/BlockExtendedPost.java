package twistedgate.immersivepoles.common.blocks;

import java.util.Random;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.wooden.BlockTypes_WoodenDevice1;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import twistedgate.immersivepoles.ImmersivePoles;
import twistedgate.immersivepoles.tiles.TEExtendedPost;
import twistedgate.immersivepoles.util.BlockUtilities;

/**
 * IGNORE THIS
 * 
 * @author TwistedGate
 */
public class BlockExtendedPost extends IPBlock implements ITileEntityProvider{
	public static final PropertyEnum<EnumMetaType> TYPE=PropertyEnum.create("type", EnumMetaType.class);
	
	public BlockExtendedPost(){
		super(Material.WOOD, "extendedpost");
		setHardness(2.0F);
		
		setDefaultState(this.blockState.getBaseState()
				.withProperty(TYPE, EnumMetaType.POST_BASE)
				);
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, TYPE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		if(meta<0 || meta>=EnumMetaType.values().length)
			return getDefaultState().withProperty(TYPE, EnumMetaType.POST_BASE);
		
		return getDefaultState().withProperty(TYPE, EnumMetaType.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(TYPE).ordinal();
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
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity){
		return true;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public int quantityDropped(Random random){
		return 0;
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		switch(state.getValue(TYPE)){
			case POST_BASE:{
				BlockPos tmp=pos.offset(EnumFacing.UP);
				if(worldIn.getTileEntity(tmp) instanceof TEExtendedPost){
					worldIn.setBlockToAir(tmp);
				}
				break;
			}
			case POST:{
				BlockPos tmp=pos.offset(EnumFacing.DOWN);
				if(worldIn.getTileEntity(tmp) instanceof TEExtendedPost){
					worldIn.setBlockToAir(tmp);
				}
				
				EnumFacing[] dirs={EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
				for(EnumFacing facing:dirs){
					BlockPos offset=pos.offset(facing);
					IBlockState offState=worldIn.getBlockState(offset);
					if(offState.getBlock()==this && offState.getValue(TYPE).ordinal()>=EnumMetaType.POST_TOP.ordinal()){
						worldIn.setBlockToAir(offset);
					}
				}
				break;
			}
			default:return;
		}
		
		if((!worldIn.isRemote && !worldIn.restoringBlockSnapshots) && state.getValue(TYPE)==EnumMetaType.POST_BASE && worldIn.getGameRules().getBoolean("doTileDrops")){
			worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this, 1, 0)));
		}
		
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!Utils.isHammer(playerIn.getHeldItem(EnumHand.MAIN_HAND))) return false;
		
		switch(state.getValue(TYPE)){
			case POST:{
				EnumFacing fac0=facing;
				EnumFacing fac1=facing.getOpposite();
				BlockPos offset0=pos.offset(fac0);
				BlockPos offset1=pos.offset(fac1);
				IBlockState tmp;
				
				if(!worldIn.isAirBlock(offset0)) return false;
				
				if(worldIn.getTileEntity(offset0) instanceof TEExtendedPost && (tmp=worldIn.getBlockState(offset0)).getBlock().getMetaFromState(tmp)!=fac0.ordinal())
					return false;
				
				if(worldIn.getTileEntity(offset1) instanceof TEExtendedPost && (tmp=worldIn.getBlockState(offset1)).getBlock().getMetaFromState(tmp)!=fac1.ordinal())
					return false;
				
				worldIn.setBlockState(offset0, getDefaultState(), 3);
				
				if(worldIn.getTileEntity(offset0) instanceof TEExtendedPost)
					worldIn.setBlockState(offset0, getStateFromMeta(facing.ordinal()), 3);
				break;
			}
			case POST_TOP:case ARM_NORTH:case ARM_EAST:case ARM_SOUTH:{
				worldIn.setBlockToAir(pos);
				break;
			}
			default:break;
		}
		
		return true;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor){
		IBlockState state=world.getBlockState(pos);
		if(state.getBlock().getMetaFromState(state)==0){
			state=world.getBlockState(pos.add(0, -1, 0));
			Block block=state.getBlock();
			
			if(!(block==IEContent.blockWoodenDevice1 && block.getMetaFromState(state)==BlockTypes_WoodenDevice1.POST.getMeta()) && block!=this){
				((World)world).setBlockToAir(pos);
				ImmersivePoles.log.info("Trigger0");
			}
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new TEExtendedPost();
	}
	
	@SuppressWarnings("deprecation")
	boolean canArmConnectToBlock(IBlockAccess world, BlockPos pos, boolean down){
		if(world.isAirBlock(pos)) return false;
		
		Block b=BlockUtilities.getBlockFrom(world, pos);
		if(down){
			return b.getBoundingBox(world.getBlockState(pos), world, pos).maxY>=1;
		}else{
			return b.getBoundingBox(world.getBlockState(pos), world, pos).minY>=0;
		}
	}
	
	AxisAlignedBB stateBounds(IBlockAccess world, BlockPos pos, IBlockState state){
		EnumMetaType meta=state.getValue(TYPE);
		switch(meta){
			case POST_BASE:return new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
			case POST:{
				float minX=meta==EnumMetaType.ARM_EAST?0.0F:0.3125F;
				float minZ=meta==EnumMetaType.POST_TOP?0.0F:0.3125F;
				float maxX=meta==EnumMetaType.ARM_SOUTH?1.0F:0.6875F;
				float maxZ=meta==EnumMetaType.ARM_NORTH?1.0F:0.6875F;
				
				return new AxisAlignedBB(minX, 0.0F, minZ, maxX, 1.0F, maxZ);
			}
			case POST_TOP:
			case ARM_NORTH:
			case ARM_EAST:
			case ARM_SOUTH:{
				float minY=0.5F;
				float maxY=1.0F;
				if(canArmConnectToBlock(world, pos.offset(EnumFacing.DOWN), true)){
					minY=0.0F;
					maxY=0.5F;
					
					if(canArmConnectToBlock(world, pos.offset(EnumFacing.UP), false))
						maxY=1.0F;
				}
				
				float minX=meta==EnumMetaType.ARM_SOUTH?0.0F:0.3125F;
				float minZ=meta==EnumMetaType.ARM_NORTH?0.0F:0.3125F;
				float maxX=meta==EnumMetaType.ARM_EAST?1.0F:0.6875F;
				float maxZ=meta==EnumMetaType.POST_TOP?1.0F:0.6875F;
				
				return new AxisAlignedBB(minX,minY,minZ,maxX,maxY,maxZ);
			}
			default:return new AxisAlignedBB(pos);
		}
	}
}
