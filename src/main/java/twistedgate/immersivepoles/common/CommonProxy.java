package twistedgate.immersivepoles.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersivepoles.ImmersivePoles;

/**
 * Why do i need this again?
 * @author TwistedGate
 */
public class CommonProxy{
	public void preInit(FMLPreInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Pre-init.");
		
	}
	
	public void init(FMLInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Init.");
		
	}
	
	public void postInit(FMLPostInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Post-init.");
		
	}
}
