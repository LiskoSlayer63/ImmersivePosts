package twistedgate.immersivepoles.common.blocks;

import net.minecraft.util.IStringSerializable;

public enum EnumMetaType implements IStringSerializable{
	META_0("meta0"),
	META_1("meta1"),
	META_2("meta2"),
	META_3("meta3"),
	META_4("meta4"),
	META_5("meta5"),
	META_6("meta6"),
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
