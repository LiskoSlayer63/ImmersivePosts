package twistedgate.immersivepoles.client;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import twistedgate.immersivepoles.ImmersivePoles;
import twistedgate.immersivepoles.common.CommonProxy;
import twistedgate.immersivepoles.common.blocks.IPStuff;

public class ClientProxy extends CommonProxy{
	@Override
	public void preInit(FMLPreInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Pre-init.");
		IPStuff.regModels();
	}
	
	@Override
	public void init(FMLInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Init.");
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent event){
		ImmersivePoles.log.debug("ClientProxy: Post-init.");
	}
}
