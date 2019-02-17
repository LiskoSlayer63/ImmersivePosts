package twistedgate.immersiveposts;

import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.common.IEContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersiveposts.common.CommonProxy;
import twistedgate.immersiveposts.common.blocks.IPStuff;

@Mod(
	modid=ModInfo.ID,
	name=ModInfo.NAME,
//	version=ModInfo.VERSION, // Disabled because replacing stuff aint working
	dependencies=ModInfo.DEPENDING,
	certificateFingerprint=ModInfo.CERT_PRINT,
	updateJSON=ModInfo.UPDATE_URL
)
public class ImmersivePost{
	@Mod.Instance(ModInfo.ID)
	public static ImmersivePost instance;
	
	@SidedProxy(modId=ModInfo.ID, serverSide=ModInfo.PROXY_SERVER, clientSide=ModInfo.PROXY_CLIENT)
	public static CommonProxy proxy;
	
	public static Logger log;
	
	public static CreativeTabs ipCreativeTab=new CreativeTabs(ModInfo.ID){
		ItemStack displayItem=null;
		@Override
		public ItemStack getTabIconItem(){
			if(this.displayItem==null)
				displayItem=new ItemStack(IEContent.blockWoodenDevice1,1,3);
				// Only temporarely using IE's wooden post
				// Until i figure out the deal with the Extendable Post item
			
			return this.displayItem;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log=event.getModLog();
		
		IPStuff.init();
		
		/*
		 * Maybe i'll use OBJs at some point
		 * since JSON isnt being much of a help thing
		 */
		//OBJLoader.INSTANCE.addDomain(ModInfo.ID);
		
		proxy.preInit(event);
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}
}
