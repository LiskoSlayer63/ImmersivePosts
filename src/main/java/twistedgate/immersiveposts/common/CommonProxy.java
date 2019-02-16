package twistedgate.immersiveposts.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersiveposts.ImmersivePost;

/**
 * Why do i need this again?
 * @author TwistedGate
 */
public class CommonProxy{
	public void preInit(FMLPreInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Pre-init.");
		
	}
	
	public void init(FMLInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Init.");
		
	}
	
	public void postInit(FMLPostInitializationEvent event){
		ImmersivePost.log.debug("ClientProxy: Post-init.");
		
	}
}
