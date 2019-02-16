package twistedgate.immersiveposts.client;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersiveposts.ImmersivePost;
import twistedgate.immersiveposts.common.CommonProxy;
import twistedgate.immersiveposts.common.blocks.IPStuff;

public class ClientProxy extends CommonProxy{
	@Override
	public void preInit(FMLPreInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Pre-init.");
		IPStuff.regModels();
	}
	
	@Override
	public void init(FMLInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Init.");
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Post-init.");
	}
}
