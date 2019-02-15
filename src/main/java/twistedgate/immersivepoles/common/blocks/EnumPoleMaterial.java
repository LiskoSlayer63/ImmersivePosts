package twistedgate.immersivepoles.common.blocks;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration1;
import blusunrize.immersiveengineering.common.blocks.wooden.BlockTypes_WoodenDecoration;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public enum EnumPoleMaterial implements IStringSerializable{
	WOOD("woodpole", IEContent.blockWoodenDecoration, BlockTypes_WoodenDecoration.FENCE.getMeta()),
	ALU("aluminiumpole", IEContent.blockMetalDecoration1, BlockTypes_MetalDecoration1.ALUMINUM_FENCE.getMeta()),
	STEEL("steelpole", IEContent.blockMetalDecoration1, BlockTypes_MetalDecoration1.STEEL_FENCE.getMeta())
	;
	
	private final String name;
	private final BlockIEBase<?> block;
	private final int meta;
	private EnumPoleMaterial(String name, BlockIEBase<?> block, int meta){
		this.name=name;
		this.block=block;
		this.meta=meta;
	}
	
	public ItemStack getFenceItem(){
		return new ItemStack(this.block, 1, this.meta);
	}
	
	public IBlockState getBlockState(){
		return this.block.getStateFromMeta(this.meta);
	}
	
	public BlockIEBase<?> getFenceBlock(){
		return this.block;
	}
	
	public int getFenceMeta(){
		return this.meta;
	}
	
	@Override
	public String getName(){
		return this.name;
	}
	
	
	public static Block getFenceBlock(ItemStack stack){
		if(stack==null || stack.isEmpty()) return null;
		
		IBlockState s=getFenceState(stack);
		if(s!=null) return s.getBlock();
		return null;
	}
	
	public static IBlockState getFenceState(ItemStack stack){
		if(stack==null || stack.isEmpty()) return null;
		
		for(EnumPoleMaterial mat:EnumPoleMaterial.values()){
			if(stack.isItemEqual(mat.getFenceItem())){
				return mat.getBlockState();
			}
		}
		
		return null;
	}
	
	public static boolean isFenceItem(ItemStack stack){
		if(stack==null || stack.isEmpty()) return false;
		
		for(EnumPoleMaterial mat:values())
			if(stack.isItemEqual(mat.getFenceItem())) return true;
		
		return false;
	}
	
	public static ItemStack getFenceItem(ItemStack stack){
		if(stack==null || stack.isEmpty()) return null;
		
		for(EnumPoleMaterial mat:values())
			if(stack.isItemEqual(mat.getFenceItem())) return mat.getFenceItem();
		
		return null;
	}
}
