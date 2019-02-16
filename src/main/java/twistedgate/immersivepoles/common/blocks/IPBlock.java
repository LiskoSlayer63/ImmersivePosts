package twistedgate.immersivepoles.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import twistedgate.immersivepoles.ImmersivePoles;
import twistedgate.immersivepoles.ModInfo;

public class IPBlock extends Block{
	
	public IPBlock(Material material, String name){
		super(material);
		ResourceLocation res=new ResourceLocation(ModInfo.ID, name);
		
		setRegistryName(res);
		setUnlocalizedName("immersivepoles."+name);
		setCreativeTab(ImmersivePoles.ipCreativeTab);
		
		IPStuff.BLOCKS.add(this);
	}
	
	/** Doesnt actualy register the item, just adds it to the list for registration */
	public final void registerBlockItem(){
		IPStuff.ITEMS.add(toItemBlock(this.getRegistryName()));
	}
	
	
	private final ItemBlock toItemBlock(ResourceLocation res){
		ItemBlock item=new ItemBlock(this);
		item.setRegistryName(res);
		return item;
	}
}
