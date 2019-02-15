package twistedgate.immersivepoles.common.blocks;

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
import twistedgate.immersivepoles.ImmersivePoles;
import twistedgate.immersivepoles.ModInfo;

@Mod.EventBusSubscriber(modid=ModInfo.ID)
public class IPStuff{
	
	public static final ArrayList<Block> BLOCKS=new ArrayList<>();
	public static final ArrayList<Item> ITEMS=new ArrayList<>();
	
	public static IPBlock extendablePost; // Old, a try to port the orignal 1:1
	
	public static BlockPoleBase poleBase;
	public static BlockPole woodPole;
	public static BlockPole aluPole;
	public static BlockPole steelPole;
	
	public static void init(){
		ImmersivePoles.log.debug("=== Init ===");
		
		poleBase=new BlockPoleBase();
		
		woodPole=(BlockPole) new BlockPole(Material.WOOD, EnumPoleMaterial.WOOD);
		aluPole=(BlockPole) new BlockPole(Material.IRON, EnumPoleMaterial.ALU);
		steelPole=(BlockPole) new BlockPole(Material.IRON, EnumPoleMaterial.STEEL);
		
//		extendablePost=new BlockExtendablePost();
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event){
		ImmersivePoles.log.debug("=== Registering Blocks ===");
		for(Block block:BLOCKS){
			ImmersivePoles.log.debug("Registering Block: "+block.getRegistryName());
			event.getRegistry().register(block);
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event){
		ImmersivePoles.log.debug("=== Registering Items ===");
		for(Item item:ITEMS){
			ImmersivePoles.log.debug("Registering Item: "+item.getRegistryName());
			event.getRegistry().register(item);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static void regModels(){
		ImmersivePoles.log.debug("=== Registering Models ===");
		for(Block block:BLOCKS)
			for(int i=0;i<16;i++)
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), i, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}
