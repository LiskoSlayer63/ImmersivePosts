package twistedgate.immersivepoles;

import org.apache.logging.log4j.Logger;

import blusunrize.immersiveengineering.common.IEContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twistedgate.immersivepoles.common.CommonProxy;
import twistedgate.immersivepoles.common.blocks.IPStuff;
import twistedgate.immersivepoles.tiles.TEExtendedPost;

@Mod(
	modid=ModInfo.ID,
	name=ModInfo.NAME,
	version=ModInfo.VERSION,
	dependencies=ModInfo.DEPENDING,
	certificateFingerprint=ModInfo.CERT_PRINT
)
public class ImmersivePoles{
	@Mod.Instance(ModInfo.ID)
	public static ImmersivePoles instance;
	
	@SidedProxy(modId=ModInfo.ID, serverSide=ModInfo.PROXY_SERVER, clientSide=ModInfo.PROXY_CLIENT)
	public static CommonProxy proxy;
	
	public static Logger log;
	
	public static CreativeTabs ipCreativeTab=new CreativeTabs(ModInfo.ID){
		ItemStack displayItem=null;
		@Override
		public ItemStack getTabIconItem(){
			if(this.displayItem==null)
				displayItem=new ItemStack(IEContent.blockWoodenDevice1,1,3);
			
			return this.displayItem;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log=event.getModLog();
		
		IPStuff.init();
		
		//OBJLoader.INSTANCE.addDomain(ModInfo.ID);
		
		proxy.preInit(event);
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		proxy.init(event);
		
		regTile(TEExtendedPost.class);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		proxy.postInit(event);
	}
	
	private static void regTile(Class<? extends TileEntity> tile){
		String name=tile.getSimpleName();
		name=name.substring(2);
		
		ResourceLocation loc=new ResourceLocation(ModInfo.ID, name);
		GameRegistry.registerTileEntity(tile, loc);
		
		log.debug("Registered TileEntity: "+loc);
	}
}
