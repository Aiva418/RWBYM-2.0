package be.bluexin.rwbym.utility.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class MessagePlayerEXP extends MessageBase<MessagePlayerEXP> {
	
	private int exp;
	
	public MessagePlayerEXP() {}
	
	public MessagePlayerEXP(int exp) {
		this.exp = exp;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.exp = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.exp);
	}

	@Override
	public void handleClientSide(MessagePlayerEXP message, EntityPlayer player) {
		
	}

	@Override
	public void handleServerSide(MessagePlayerEXP message, EntityPlayer player) {
		//player.addExperience(message.exp);
		///*
		int exp  = player.experienceTotal + message.exp;
		player.experience = 0;
		player.experienceLevel = 0;
		player.experienceTotal = 0;
		player.addExperience(exp);
		//*/
	}

}
