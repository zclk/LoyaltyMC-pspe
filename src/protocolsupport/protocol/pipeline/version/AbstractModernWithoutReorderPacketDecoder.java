package protocolsupport.protocol.pipeline.version;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.minecraft.server.v1_10_R1.EnumProtocol;
import protocolsupport.api.Connection;
import protocolsupport.protocol.packet.middle.ServerBoundMiddlePacket;
import protocolsupport.protocol.serializer.ProtocolSupportPacketDataSerializer;
import protocolsupport.protocol.storage.NetworkDataCache;
import protocolsupport.utils.netty.ChannelUtils;

public class AbstractModernWithoutReorderPacketDecoder extends AbstractPacketDecoder  {

	public AbstractModernWithoutReorderPacketDecoder(Connection connection, NetworkDataCache sharedstorage) {
		super(connection, sharedstorage);
	}

	private final ProtocolSupportPacketDataSerializer serializer = new ProtocolSupportPacketDataSerializer(null, connection.getVersion());

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> list) throws Exception {
		if (!input.isReadable()) {
			return;
		}
		serializer.setBuf(input);
		EnumProtocol protocol = ctx.channel().attr(ChannelUtils.CURRENT_PROTOCOL_KEY).get();
		ServerBoundMiddlePacket packetTransformer = registry.getTransformer(protocol, serializer.readVarInt());
		packetTransformer.readFromClientData(serializer);
		if (serializer.isReadable()) {
			throw new DecoderException("Did not read all data from packet " + packetTransformer.getClass().getName() + ", bytes left: " + serializer.readableBytes());
		}
		addPackets(protocol, packetTransformer.toNative(), list);
	}

}