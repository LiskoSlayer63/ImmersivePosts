package twistedgate.immersivepoles.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum EnumPoleType implements IStringSerializable{
	POST("post"),
	POST_TOP("top"),
	ARM("arm")
	;
	
	final String name;
	private EnumPoleType(String name){
		this.name=name;
	}
	
	@Override
	public String getName(){
		return this.name;
	}
}
