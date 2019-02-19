package twistedgate.immersiveposts;

import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersiveposts.common.blocks.IPStuff;

@Mod(
	modid=ModInfo.ID,
	name=ModInfo.NAME,
	dependencies=ModInfo.DEPENDING,
	certificateFingerprint=ModInfo.CERT_PRINT,
	updateJSON=ModInfo.UPDATE_URL
)
public class ImmersivePosts{
	@Mod.Instance(ModInfo.ID)
	public static ImmersivePosts instance;
	
	public static Logger log;
	
	public static final CreativeTabs ipCreativeTab=new CreativeTabs(ModInfo.ID){
		ItemStack displayItem=null;
		@Override
		public ItemStack getTabIconItem(){
			if(this.displayItem==null)
				displayItem=new ItemStack(IPStuff.postBase);
			return this.displayItem;
		}
	};
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		log=event.getModLog();
	}
}
