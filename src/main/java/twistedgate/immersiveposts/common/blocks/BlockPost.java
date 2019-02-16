package twistedgate.immersiveposts.common.blocks;

import blusunrize.immersiveengineering.api.IPostBlock;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import twistedgate.immersiveposts.util.BlockUtilities;

// Using IPBlock temporarly for debugging purposes!
public class BlockPost extends IPBlock implements IPostBlock{
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
		return new BlockStateContainer(this, new IProperty<?>[]{
			DIRECTION,
			FLIP,
			TYPE
		});
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
					default:rot=0; // Aka North and Up or Down
				}
				
				if(state.getValue(FLIP)){
					return 6+rot;
				}else{
					return 2+rot;
				}
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
		return state.getValue(TYPE)!=EnumPostType.ARM;
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
				
				for(int y=1;y<(worldIn.getActualHeight()-pos.getY());y++){
					BlockPos nPos=pos.add(0,y,0);
					
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
								}else{
									IBlockState stat=worldIn.getBlockState(nPos);
									if(stat.getBlock() instanceof BlockPost){
										if(state.getValue(TYPE)==EnumPostType.ARM && ((BlockPost)stat.getBlock()).postMaterial==this.postMaterial)
											worldIn.setBlockToAir(nPos);
									}
								}
								
								return true;
							}
							default:return true;
						}
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
		
		Block aboveBlock=BlockUtilities.getBlockFromDirection(worldIn, pos, EnumFacing.UP);
		if(thisType==EnumPostType.POST && aboveBlock!=this){
			worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPostType.POST_TOP));
			
		}else if(thisType==EnumPostType.POST_TOP && aboveBlock==this){
			worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPostType.POST));
			
		}else if(thisType==EnumPostType.ARM){
			EnumFacing f=state.getValue(DIRECTION).getOpposite();
			if(BlockUtilities.getBlockFromDirection(worldIn, pos, f)!=this){
				worldIn.setBlockToAir(pos);
				return;
			}
			
			worldIn.setBlockState(pos, state.withProperty(FLIP, BlockUtilities.getBlockFromDirection(worldIn, pos, EnumFacing.DOWN)!=Blocks.AIR), 3);
		}
	}
	
	static final AxisAlignedBB POST_SHAPE=new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
	
	AxisAlignedBB stateBounds(IBlockAccess world, BlockPos pos, IBlockState state){
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
