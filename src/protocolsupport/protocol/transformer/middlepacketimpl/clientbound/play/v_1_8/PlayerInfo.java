package protocolsupport.protocol.transformer.middlepacketimpl.clientbound.play.v_1_8;

import java.io.IOException;

import com.mojang.authlib.properties.Property;

import protocolsupport.api.ProtocolVersion;
import protocolsupport.protocol.ClientBoundPacket;
import protocolsupport.protocol.transformer.middlepacket.clientbound.play.MiddlePlayerInfo;
import protocolsupport.protocol.transformer.middlepacketimpl.PacketData;
import protocolsupport.utils.recyclable.RecyclableCollection;
import protocolsupport.utils.recyclable.RecyclableSingletonList;

public class PlayerInfo extends MiddlePlayerInfo<RecyclableCollection<PacketData>> {

	@Override
	public RecyclableCollection<PacketData> toData(ProtocolVersion version) throws IOException {
		PacketData serializer = PacketData.create(ClientBoundPacket.PLAY_PLAYER_INFO_ID, version);
		serializer.a(action);
		serializer.writeVarInt(infos.length);
		for (Info info : infos) {
			serializer.writeUUID(info.uuid);
			switch (action) {
				case ADD: {
					serializer.writeString(info.username);
					serializer.writeVarInt(info.properties.length);
					for (Property property : info.properties) {
						serializer.writeString(property.getName());
						serializer.writeString(property.getValue());
						serializer.writeBoolean(property.hasSignature());
						if (property.hasSignature()) {
							serializer.writeString(property.getSignature());
						}
					}
					serializer.writeVarInt(info.gamemode);
					serializer.writeVarInt(info.ping);
					serializer.writeBoolean(info.displayNameJson != null);
					if (info.displayNameJson != null) {
						serializer.writeString(info.displayNameJson);
					}
					break;
				}
				case GAMEMODE: {
					serializer.writeVarInt(info.gamemode);
					break;
				}
				case PING: {
					serializer.writeVarInt(info.ping);
					break;
				}
				case DISPLAY_NAME: {
					serializer.writeBoolean(info.displayNameJson != null);
					if (info.displayNameJson != null) {
						serializer.writeString(info.displayNameJson);
					}
					break;
				}
				case REMOVE: {
					break;
				}
			}
		}
		return RecyclableSingletonList.create(serializer);
	}

}
