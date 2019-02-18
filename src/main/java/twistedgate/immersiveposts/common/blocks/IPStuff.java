package twistedgate.immersiveposts.common.blocks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import twistedgate.immersiveposts.ImmersivePost;
import twistedgate.immersiveposts.ModInfo;

@Mod.EventBusSubscriber(modid=ModInfo.ID)
public class IPStuff{
	
	public static final ArrayList<IPBlock> BLOCKS=new ArrayList<>();
	public static final ArrayList<Item> ITEMS=new ArrayList<>();
	
	// Could make these final too.. Hmmmmm
	public static BlockPostBase postBase;
	public static BlockPost woodPost;
	public static BlockPost aluPost;
	public static BlockPost steelPost;
	
	public static void init(){
		ImmersivePost.log.debug("=== Init ===");
		
		postBase=(BlockPostBase)new BlockPostBase().registerBlockItem();
		
		woodPost=new BlockPost(Material.WOOD, EnumPostMaterial.WOOD);
		aluPost=new BlockPost(Material.IRON, EnumPostMaterial.ALU);
		steelPost=new BlockPost(Material.IRON, EnumPostMaterial.STEEL);
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		ImmersivePost.log.debug("=== Registering Blocks ===");
		for(Block block:BLOCKS){
			ImmersivePost.log.debug("Registering Block: "+block.getRegistryName());
			event.getRegistry().register(block);
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		ImmersivePost.log.debug("=== Registering Items ===");
		for(Item item:ITEMS){
			ImmersivePost.log.debug("Registering Item: "+item.getRegistryName());
			event.getRegistry().register(item);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void regModels(){
		ImmersivePost.log.debug("=== Registering Models ===");
		for(IPBlock block:BLOCKS){
			if(block.hasItem()){
				Item item=Item.getItemFromBlock(block);
				ModelResourceLocation loc=new ModelResourceLocation(block.getRegistryName(), "inventory");
				ModelLoader.setCustomModelResourceLocation(item, 0, loc);
				
				ImmersivePost.log.debug(item+" 0 "+loc);
			}
		}
	}
}
