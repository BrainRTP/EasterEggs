package ru.brainrtp.eastereggs.util.text;

import org.bukkit.entity.Player;

public interface PlaceholderHook {

    String replace(String placeholder, Player player);

}
