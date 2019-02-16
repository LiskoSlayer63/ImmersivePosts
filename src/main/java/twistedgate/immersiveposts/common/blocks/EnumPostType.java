package twistedgate.immersiveposts.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum EnumPostType implements IStringSerializable{
	POST("post"),
	POST_TOP("top"),
	ARM("arm")
	;
	
	final String name;
	private EnumPostType(String name){
		this.name=name;
	}
	
	@Override
	public String getName(){
		return this.name;
	}
}
