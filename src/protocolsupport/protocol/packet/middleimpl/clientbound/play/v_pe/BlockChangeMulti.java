package protocolsupport.protocol.packet.middleimpl.clientbound.play.v_pe;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import protocolsupport.protocol.ConnectionImpl;
import protocolsupport.protocol.packet.middle.clientbound.play.MiddleBlockChangeMulti;
import protocolsupport.protocol.packet.middleimpl.ClientBoundPacketData;
import protocolsupport.protocol.typeremapper.basic.TileEntityRemapper;
import protocolsupport.protocol.utils.types.Position;
import protocolsupport.utils.recyclable.RecyclableArrayList;
import protocolsupport.utils.recyclable.RecyclableCollection;

public class BlockChangeMulti extends MiddleBlockChangeMulti {

	public BlockChangeMulti(ConnectionImpl connection) {
		super(connection);
	}

	protected final TileEntityRemapper tileremapper = TileEntityRemapper.getRemapper(connection.getVersion());

	@Override
	public RecyclableCollection<ClientBoundPacketData> toData() {
		Int2IntMap tilestates = cache.getTileCache().getChunk(chunk);
		RecyclableArrayList<ClientBoundPacketData> packets = RecyclableArrayList.create();
		for (Record record : records) {
			BlockChangeSingle.create(connection.getVersion(), Position.fromLocal(chunk, record.localCoord), record.id, packets);
			if (tileremapper.tileThatNeedsBlockData(record.id)) {
				tilestates.put(record.localCoord, record.id);
			} else {
				tilestates.remove(record.localCoord);
			}
			if (tileremapper.usedToBeTile(record.id)) {
				packets.add(BlockTileUpdate.create(connection.getVersion(),
					tileremapper.getLegacyTileFromBlock(Position.fromLocal(chunk, record.localCoord), record.id)
				));
			}
		}
		return packets;
	}

}