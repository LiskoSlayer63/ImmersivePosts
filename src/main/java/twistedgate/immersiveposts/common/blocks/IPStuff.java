package twistedgate.immersiveposts.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.immersiveposts.ModInfo;

@Mod.EventBusSubscriber(modid=ModInfo.ID)
public class IPStuff{
	public static final ArrayList<IPBlock> BLOCKS=new ArrayList<>();
	public static final ArrayList<Item> ITEMS=new ArrayList<>();
	
	public static final BlockPostBase postBase;
	public static final BlockPost woodPost;
	public static final BlockPost aluPost;
	public static final BlockPost steelPost;
	
	static{
		postBase=(BlockPostBase)new BlockPostBase();
		
		woodPost=new BlockPost(Material.WOOD, EnumPostMaterial.WOOD);
		aluPost=new BlockPost(Material.IRON, EnumPostMaterial.ALU);
		steelPost=new BlockPost(Material.IRON, EnumPostMaterial.STEEL);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		for(Block block:BLOCKS){
			event.getRegistry().register(block);
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		for(Item item:ITEMS){
			event.getRegistry().register(item);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void regModels(ModelRegistryEvent event){
		for(IPBlock block:BLOCKS){
			if(block.hasItem()){
				Item item=Item.getItemFromBlock(block);
				ModelResourceLocation loc=new ModelResourceLocation(block.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
			}
		}
	}
}
