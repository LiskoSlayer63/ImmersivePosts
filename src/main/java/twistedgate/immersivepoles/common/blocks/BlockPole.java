package twistedgate.immersivepoles.common.blocks;

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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Using IPBlock temporarly for debugging purposes!
public class BlockPole extends IPBlock{
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
			
			case 2: return state.withProperty(TYPE, EnumPoleType.ARM); // Using default Direction
			case 3: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.EAST);
			case 4: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.SOUTH);
			case 5: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(DIRECTION, EnumFacing.WEST);
			
			case 6: return state.withProperty(TYPE, EnumPoleType.ARM).withProperty(FLIP, true); // Using default Direction
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		
	}
	
	static final AxisAlignedBB POST_SHAPE=new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
	static final AxisAlignedBB POST_TOP_SHAPE=new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
	
	AxisAlignedBB stateBounds(IBlockAccess world, BlockPos pos, IBlockState state){
		switch(state.getValue(TYPE)){
			case POST:
				return POST_SHAPE;
			case POST_TOP:
				return POST_TOP_SHAPE;
			case ARM:{
				boolean flipped=state.getValue(FLIP);
				EnumFacing facing=state.getValue(DIRECTION);
				
				float minY=flipped?0.0F:0.34375F;
				float maxY=flipped?0.65625F:1.0F;
				
				float minX;
				float maxX;
				if(flipped){
					minX=facing==EnumFacing.WEST?-0.25F:0.3125F;
					maxX=facing==EnumFacing.EAST? 1.25F:0.6875F;
				}else{
					minX=facing==EnumFacing.EAST?-0.25F:0.3125F;
					maxX=facing==EnumFacing.WEST? 1.25F:0.6875F;
				}
				
				float minZ;
				float maxZ;
				if(flipped){
					minZ=facing==EnumFacing.NORTH?-0.25F:0.3125F;
					maxZ=facing==EnumFacing.SOUTH? 1.25F:0.6875F;
				}else{
					minZ=facing==EnumFacing.SOUTH?-0.25F:0.3125F;
					maxZ=facing==EnumFacing.NORTH? 1.25F:0.6875F;
				}
				
				return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			}
		}
		return new AxisAlignedBB(pos);
	}
}
