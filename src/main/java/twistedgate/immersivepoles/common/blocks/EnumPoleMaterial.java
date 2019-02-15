package twistedgate.immersivepoles.common.blocks;

import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.blocks.BlockIEBase;
import blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration1;
import blusunrize.immersiveengineering.common.blocks.wooden.BlockTypes_WoodenDecoration;
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
}
