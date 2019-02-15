package twistedgate.immersivepoles.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum EnumMetaType implements IStringSerializable{
	POST_BASE("meta0"),
	POST("meta1"),
	POST_TOP("meta2"),
	ARM_NORTH("meta3"),
	ARM_EAST("meta4"),
	ARM_SOUTH("meta5"),
	ARM_WEST("meta6"),
	META_7("meta7"),
	META_8("meta8"),
	META_9("meta9"),
	META_10("meta10"),
	META_11("meta11"),
	META_12("meta12"),
	META_13("meta13"),
	META_14("meta14"),
	META_15("meta15");
	
	final String name;
	private EnumMetaType(String name){
		this.name=name;
	}
	
	@Override
	public String getName(){
		return this.name;
	}
}
