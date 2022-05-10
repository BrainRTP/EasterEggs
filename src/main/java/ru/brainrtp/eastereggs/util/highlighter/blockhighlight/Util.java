package ru.brainrtp.eastereggs.util.highlighter.blockhighlight;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {

    public static void sendStopToAll() {
        Bukkit.getOnlinePlayers().forEach(Util::sendStop);
    }

    public static void sendBlockHighlight(Player pl, BlockHighlight highlight) {
        ByteBuf packet = Unpooled.buffer();
        packet.writeLong(PacketUtil.blockPosToLong(highlight.getX(), highlight.getY(), highlight.getZ()));
        packet.writeInt(highlight.getColor());

        String text = ChatColor.translateAlternateColorCodes('&', highlight.getText());

        PacketUtil.writeString(packet, text);

        packet.writeInt(highlight.getTime());

        PacketUtil.sendPayload(pl, "debug/game_test_add_marker", packet);
    }

    public static void sendStop(Player pl) {
        PacketUtil.sendPayload(pl, "debug/game_test_clear", Unpooled.wrappedBuffer(new byte[0]));
    }
}
