package twistedgate.immersiveposts.common.blocks;

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
import twistedgate.immersiveposts.util.BlockUtilities;

public class BlockPostBase extends IPBlock{
	private static final AxisAlignedBB BASE_SIZE=new AxisAlignedBB(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
	
	public BlockPostBase(){
		super(Material.ROCK, "postbase");
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ItemStack held=playerIn.getHeldItemMainhand();
			if(EnumPostMaterial.isFenceItem(held)){
				if(!worldIn.isAirBlock(pos.offset(EnumFacing.UP))){
					IBlockState aboveState=worldIn.getBlockState(pos.offset(EnumFacing.UP));
					Block b=aboveState.getBlock();
					
					if(b instanceof BlockPost){
						BlockPost p=(BlockPost)b;
						
						if(p==IPStuff.woodPost && !held.isItemEqual(EnumPostMaterial.WOOD.getFenceItem())){
							playerIn.sendMessage(new TextComponentString("Expected: "+EnumPostMaterial.WOOD.getFenceItem().getDisplayName()+"."));
							return true;
							
						}else if(p==IPStuff.aluPost && !held.isItemEqual(EnumPostMaterial.ALU.getFenceItem())){
							playerIn.sendMessage(new TextComponentString("Expected: "+EnumPostMaterial.ALU.getFenceItem().getDisplayName()+"."));
							return true;
							
						}else if(p==IPStuff.steelPost && !held.isItemEqual(EnumPostMaterial.STEEL.getFenceItem())){
							playerIn.sendMessage(new TextComponentString("Expected: "+EnumPostMaterial.STEEL.getFenceItem().getDisplayName()+"."));
							return true;
						}
					}
				}
				
				for(int i=1;i<(worldIn.getActualHeight()-pos.getY());i++){
					BlockPos nPos=pos.add(0, i, 0);
					
					if((BlockUtilities.getBlockFrom(worldIn, nPos) instanceof BlockPost)){
						IBlockState s=worldIn.getBlockState(nPos);
						if(s.getValue(BlockPost.TYPE)==EnumPostType.ARM){
							return true;
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
						
					}else if(!(worldIn.getBlockState(nPos).getBlock() instanceof BlockPost || worldIn.getBlockState(nPos).getBlock()==this)){
						return true;
					}
				}
			}
		}
		
		return EnumPostMaterial.isFenceItem(playerIn.getHeldItemMainhand());
	}
}
