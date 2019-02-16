package twistedgate.immersivepoles.common.blocks;

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
import twistedgate.immersivepoles.util.BlockUtilities;

// Using IPBlock temporarly for debugging purposes!
public class BlockPole extends IPBlock implements IPostBlock{
	public static final PropertyDirection DIRECTION=PropertyDirection.create("facing");
	public static final PropertyBool FLIP=PropertyBool.create("flip");
	public static final PropertyEnum<EnumPoleType> TYPE=PropertyEnum.create("type", EnumPoleType.class);
	
	protected EnumPoleMaterial poleMaterial;
	public BlockPole(Material blockMaterial, EnumPoleMaterial poleMaterial){
		super(blockMaterial, poleMaterial.getName());
		this.poleMaterial=poleMaterial;
		
		setResistance(3.0F);
		setHardness(3.0F);
		switch(this.poleMaterial){
			case WOOD:setHarvestLevel("axe", 0);break;
			case ALU:
			case STEEL:setHarvestLevel("pickaxe", 1);break;
		}
		
		setDefaultState(this.blockState.getBaseState()
				.withProperty(DIRECTION, EnumFacing.NORTH)
				.withProperty(FLIP, false)
				.withProperty(TYPE, EnumPoleType.POST)
				);
	}
	
	public final EnumPoleMaterial getPoleMaterial(){
		return this.poleMaterial;
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
		if(state.getValue(TYPE)!=EnumPoleType.ARM)
			drops.add(this.poleMaterial.getFenceItem());
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
			case 1: return state.withProperty(TYPE, EnumPoleType.POST_TOP);
			
			case 2: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.NORTH);
			case 3: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.EAST);
			case 4: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.SOUTH);
			case 5: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.WEST);
			
			case 6: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.NORTH);
			case 7: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.EAST);
			case 8: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.SOUTH);
			case 9: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(FLIP, true).withProperty(DIRECTION, EnumFacing.WEST);
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
		return this.poleMaterial.getFenceItem();
	}

	@Override
	public boolean canConnectTransformer(IBlockAccess world, BlockPos pos){
		IBlockState state=world.getBlockState(pos);
		return state.getValue(TYPE)!=EnumPoleType.ARM;
	}
	
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack held=playerIn.getHeldItemMainhand();
			if(EnumPoleMaterial.isFenceItem(held)){
				
				if(!held.isItemEqual(this.poleMaterial.getFenceItem())){
					playerIn.sendMessage(new TextComponentString("Expected: "+this.poleMaterial.getFenceItem().getDisplayName()+"."));
					return true;
				}
				
				for(int y=1;y<(worldIn.getActualHeight()-pos.getY());y++){
					BlockPos nPos=pos.add(0,y,0);
					
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
						
					}else if(!(worldIn.getBlockState(nPos).getBlock() instanceof BlockPole || worldIn.getBlockState(nPos)==this)){
						return true;
					}
				}
			}else if(Utils.isHammer(held)){
				switch(state.getValue(TYPE)){
					case POST:case POST_TOP:{
						IBlockState defaultState=getDefaultState().withProperty(TYPE, EnumPoleType.ARM);
						switch(facing){
							case NORTH:case EAST:case SOUTH:case WEST:{
								BlockPos nPos=pos.offset(facing);
								
								if(worldIn.isAirBlock(nPos)){
									worldIn.setBlockState(nPos, defaultState.withProperty(DIRECTION, facing), 3);
								}else{
									IBlockState stat=worldIn.getBlockState(nPos);
									if(stat.getBlock() instanceof BlockPole){
										if(state.getValue(TYPE)==EnumPoleType.ARM && ((BlockPole)stat.getBlock()).poleMaterial==this.poleMaterial)
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
		
		return Utils.isHammer(playerIn.getHeldItemMainhand()) || EnumPoleMaterial.isFenceItem(playerIn.getHeldItemMainhand());
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		EnumPoleType thisType=state.getValue(TYPE);
		
		if(thisType!=EnumPoleType.ARM){
			if(worldIn.isAirBlock(pos.offset(EnumFacing.DOWN))){
				dropBlockAsItem(worldIn, fromPos, state, 1);
				worldIn.setBlockToAir(pos);
				return;
			}
		}
		
		Block aboveBlock=BlockUtilities.getBlockFromDirection(worldIn, pos, EnumFacing.UP);
		if(thisType==EnumPoleType.POST && aboveBlock!=this){
			worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPoleType.POST_TOP));
			
		}else if(thisType==EnumPoleType.POST_TOP && aboveBlock==this){
			worldIn.setBlockState(pos, state.withProperty(TYPE, EnumPoleType.POST));
			
		}else if(thisType==EnumPoleType.ARM){
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
