package twistedgate.immersiveposts.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import twistedgate.immersiveposts.ImmersivePosts;
import twistedgate.immersiveposts.ModInfo;

public class IPBlock extends Block{
	protected boolean hasItem=false;
	public IPBlock(Material material, String name){
		super(material);
		ResourceLocation res=new ResourceLocation(ModInfo.ID, name);
		
		setRegistryName(res);
		setUnlocalizedName("immersiveposts."+name);
		setCreativeTab(ImmersivePosts.ipCreativeTab);
		
		IPStuff.BLOCKS.add(this);
	}
	
	public final boolean hasItem(){
		return this.hasItem;
	}
}
