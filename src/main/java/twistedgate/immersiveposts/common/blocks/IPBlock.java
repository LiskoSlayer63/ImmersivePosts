package twistedgate.immersiveposts.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.ResourceLocation;
import twistedgate.immersiveposts.ImmersivePosts;
import twistedgate.immersiveposts.ModInfo;

public class IPBlock extends Block{
	public IPBlock(Material material, String name){
		super(material);
		ResourceLocation res=new ResourceLocation(ModInfo.ID, name);
		
		setRegistryName(res);
		setTranslationKey("immersiveposts."+name);
		setCreativeTab(ImmersivePosts.ipCreativeTab);
		
		IPStuff.BLOCKS.add(this);
	}
	
	public boolean hasItem(){
		return false;
	}
}
