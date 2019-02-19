package twistedgate.immersiveposts.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import twistedgate.immersiveposts.ImmersivePosts;
import twistedgate.immersiveposts.ModInfo;

public class IPBlock extends Block{
	private boolean hasItem=false;
	public IPBlock(Material material, String name){
		super(material);
		ResourceLocation res=new ResourceLocation(ModInfo.ID, name);
		
		setRegistryName(res);
		setUnlocalizedName("immersiveposts."+name);
		setCreativeTab(ImmersivePosts.ipCreativeTab);
		
		IPStuff.BLOCKS.add(this);
	}
	
	/** Doesnt actualy register the item, just adds it to the list <i>for</i> registration */
	public final IPBlock registerBlockItem(){
		IPStuff.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		this.hasItem=true;
		return this;
	}
	
	public final boolean hasItem(){
		return this.hasItem;
	}
}
