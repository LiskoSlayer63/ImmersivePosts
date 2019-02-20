package twistedgate.immersiveposts.common.blocks;

import blusunrize.immersiveengineering.api.IPostBlock;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import twistedgate.immersiveposts.util.BlockUtilities;

/**
 * All-in-one package. Containing everything into one neat class is the best.
 * @author TwistedGate
 */
public class BlockPost extends IPBlock implements IPostBlock{
	public static final IUnlistedProperty<Boolean> PARM_NORTH=Properties.toUnlisted(PropertyBool.create("hasNorth"));
	public static final IUnlistedProperty<Boolean> PARM_EAST=Properties.toUnlisted(PropertyBool.create("hasEast"));
	public static final IUnlistedProperty<Boolean> PARM_SOUTH=Properties.toUnlisted(PropertyBool.create("hasSouth"));
	public static final IUnlistedProperty<Boolean> PARM_WEST=Properties.toUnlisted(PropertyBool.create("hasWest"));
	
	public static final PropertyDirection DIRECTION=PropertyDirection.create("facing");
	public static final PropertyBool FLIP=PropertyBool.create("flip");
	public static final PropertyEnum<EnumPostType> TYPE=PropertyEnum.create("type", EnumPostType.class);
	
	protected EnumPostMaterial postMaterial;
	public BlockPost(Material blockMaterial, EnumPostMaterial postMaterial){
		super(blockMaterial, postMaterial.getName());
		this.postMaterial=postMaterial;
		
		setResistance(3.0F);
		setHardness(3.0F);
		switch(this.postMaterial){
			case WOOD:setHarvestLevel("axe", 0);break;
			case ALU:
			case STEEL:setHarvestLevel("pickaxe", 1);break;
		}
		
		setDefaultState(this.blockState.getBaseState()
				.withProperty(DIRECTION, EnumFacing.NORTH)
				.withProperty(FLIP, false)
				.withProperty(TYPE, EnumPostType.POST)
				);
	}
	
	public final EnumPostMaterial getPostMaterial(){
		return this.postMaterial;
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer.Builder(this)
				.add(DIRECTION).add(FLIP).add(TYPE)
				.add(PARM_NORTH).add(PARM_EAST).add(PARM_SOUTH).add(PARM_WEST)
				.build();
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		if(state.getValue(TYPE)!=EnumPostType.ARM)
			drops.add(this.postMaterial.getFenceItem());
	}
	
	@Override
	public int getMetaFromState(IBlockState state){
		switch(state.getValue(TYPE)){
			case POST: return 0;
			case POST_TOP: return 1;
			case ARM:{
				int rot;
				switch(state.getValue(DIRECTION)){
					case EAST:rot=1;break;
					case SOUTH:rot=2;break;
					case WEST:rot=3;break;
					default:rot=0; // Aka North, Up and Down
				}
				
				return (state.getValue(FLIP)?6:2)+rot;
			}
			default: return 0;
		}
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta){
		IBlockState state=getDefaultState();
		switch(meta){
			case 0: return state;
			case 1: return state.withProperty(TYPE, EnumPostType.POST_TOP);
			
			case 2: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(DIRECTION, EnumFacing.NORTH);
			case 3: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(DIRECTION, EnumFacing.EAST);
			case 4: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(DIRECTION, EnumFacing.SOUTH);
			case 5: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(DIRECTION, EnumFacing.WEST);
			
			case 6: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.NORTH);
			case 7: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.EAST);
			case 8: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.SOUTH);
			case 9: return state.withProperty(TYPE, EnumPostType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.WEST);
			default: return state;
		}
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		return ((IExtendedBlockState)state)
				.withProperty(PARM_NORTH, canConnect(world, pos, EnumFacing.NORTH))
				.withProperty(PARM_EAST, canConnect(world, pos, EnumFacing.EAST))
				.withProperty(PARM_SOUTH, canConnect(world, pos, EnumFacing.SOUTH))
				.withProperty(PARM_WEST, canConnect(world, pos, EnumFacing.WEST));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return stateBounds(source, pos, state);
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
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		return false;
	}
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity){
		return true;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return this.postMaterial.getFenceItem();
	}
	
	@Override
	public boolean canConnectTransformer(IBlockAccess world, BlockPos pos){
		IBlockState state=world.getBlockState(pos);
		return state.getValue(TYPE)==EnumPostType.POST;
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack held=playerIn.getHeldItemMainhand();
			if(EnumPostMaterial.isFenceItem(held)){
				if(!held.isItemEqual(this.postMaterial.getFenceItem())){
					playerIn.sendMessage(new TextComponentString("Expected: "+this.postMaterial.getFenceItem().getDisplayName()+"."));
					return true;
				}
				
				for(int y=0;y<(worldIn.getActualHeight()-pos.getY());y++){
					BlockPos nPos=pos.add(0,y,0);
					
					if((BlockUtilities.getBlockFrom(worldIn, nPos) instanceof BlockPost)){
						IBlockState s=worldIn.getBlockState(nPos);
						if(s.getValue(BlockPost.TYPE)==EnumPostType.ARM && s.getValue(BlockPost.FLIP)){
							return true;
						}
						
						BlockPos up=nPos.offset(EnumFacing.UP);
						if((BlockUtilities.getBlockFrom(worldIn, up) instanceof BlockPost)){
							s=worldIn.getBlockState(up);
							if(s.getValue(BlockPost.TYPE)==EnumPostType.ARM){
								return true;
							}
						}
					}
					
					if(worldIn.isAirBlock(nPos)){
						
						IBlockState fb=null;
						if(held.isItemEqual(EnumPostMaterial.WOOD.getFenceItem()))
							fb=IPStuff.woodPost.getDefaultState().withProperty(BlockPost.TYPE, EnumPostType.POST_TOP);
						
						else if(held.isItemEqual(EnumPostMaterial.ALU.getFenceItem()))
							fb=IPStuff.aluPost.getDefaultState().withProperty(BlockPost.TYPE, EnumPostType.POST_TOP);
						
						else if(held.isItemEqual(EnumPostMaterial.STEEL.getFenceItem()))
							fb=IPStuff.steelPost.getDefaultState().withProperty(BlockPost.TYPE, EnumPostType.POST_TOP);
						
						if(worldIn.setBlockState(nPos, fb)){
							if(!playerIn.capabilities.isCreativeMode){
								held.shrink(1);
							}
						}
						return true;
						
					}else if(!(worldIn.getBlockState(nPos).getBlock() instanceof BlockPost || worldIn.getBlockState(nPos)==this)){
						return true;
					}
				}
			}else if(Utils.isHammer(held)){
				switch(state.getValue(TYPE)){
					case POST:case POST_TOP:{
						IBlockState defaultState=getDefaultState().withProperty(TYPE, EnumPostType.ARM);
						switch(facing){
							case NORTH:case EAST:case SOUTH:case WEST:{
								BlockPos nPos=pos.offset(facing);
								if(worldIn.isAirBlock(nPos)){
									worldIn.setBlockState(nPos, defaultState.withProperty(DIRECTION, facing), 3);
								}else if(BlockUtilities.getBlockFrom(worldIn, nPos)==this){
									if(worldIn.getBlockState(nPos).getValue(TYPE)==EnumPostType.ARM){
										worldIn.setBlockToAir(nPos);
									}
								}
							}
							default:break;
						}
						return true;
					}
					case ARM:{
						worldIn.setBlockToAir(pos);
						return true;
					}
				}
			}
		}
		
		return Utils.isHammer(playerIn.getHeldItemMainhand()) || EnumPostMaterial.isFenceItem(playerIn.getHeldItemMainhand());
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		EnumPostType thisType=state.getValue(TYPE);
		
		if(thisType!=EnumPostType.ARM){
			if(worldIn.isAirBlock(pos.offset(EnumFacing.DOWN))){
				dropBlockAsItem(worldIn, fromPos, state, 1);
				worldIn.setBlockToAir(pos);
				return;
			}
		}
		
		IBlockState aboveState=worldIn.getBlockState(pos.offset(EnumFacing.UP));
		Block aboveBlock=aboveState.getBlock();
		if(thisType==EnumPostType.POST && aboveBlock!=this){
			worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPostType.POST_TOP));
			
		}else if(thisType==EnumPostType.POST_TOP && aboveBlock==this){
			if(aboveState.getValue(TYPE)!=EnumPostType.ARM)
				worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPostType.POST));
			
		}else if(thisType==EnumPostType.ARM){
			EnumFacing f=state.getValue(DIRECTION).getOpposite();
			if(BlockUtilities.getBlockFromDirection(worldIn, pos, f)!=this){
				worldIn.setBlockToAir(pos);
				return;
			}
			
			if(canConnect(worldIn, pos, EnumFacing.UP)){
				worldIn.setBlockState(pos, state.withProperty(FLIP, false), 3);
			}
			
			boolean bool=canConnect(worldIn, pos, EnumFacing.DOWN);
			worldIn.setBlockState(pos, state.withProperty(FLIP, bool), 3);
		}
	}
	
	static boolean canConnect(IBlockAccess world, BlockPos pos, EnumFacing facing){
		BlockPos nPos=pos.offset(facing);
		
		if(world.isAirBlock(nPos) || BlockUtilities.getBlockFrom(world, nPos) instanceof IPostBlock)
			return false;
		
		IBlockState state=world.getBlockState(nPos);
		switch(facing){
			case DOWN: return state.getBoundingBox(world, nPos).maxY>=1.0;
			case UP: return state.getBoundingBox(world, nPos).minY<=0.0;
			case SOUTH: return state.getBoundingBox(world, nPos).minZ>=1.0;
			case NORTH: return state.getBoundingBox(world, nPos).minZ<=0.0;
			case EAST: return state.getBoundingBox(world, nPos).maxX>=1.0;
			case WEST: return state.getBoundingBox(world, nPos).maxX<=0.0;
		}
		return false;
	}
	
	static final AxisAlignedBB POST_SHAPE=new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
	
	static AxisAlignedBB stateBounds(IBlockAccess world, BlockPos pos, IBlockState state){
		switch(state.getValue(TYPE)){
			case ARM:{
				EnumFacing facing=state.getValue(DIRECTION);
				boolean flipped=state.getValue(FLIP);
				
				float minY=flipped?0.0F:0.34375F;
				float maxY=flipped?0.65625F:1.0F;
				
				float minX=facing==EnumFacing.EAST?-0.25F:0.3125F;
				float maxX=facing==EnumFacing.WEST? 1.25F:0.6875F;
				float minZ=facing==EnumFacing.SOUTH?-0.25F:0.3125F;
				float maxZ=facing==EnumFacing.NORTH? 1.25F:0.6875F;
				
				return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			}
			default: return POST_SHAPE;
		}
	}
}
