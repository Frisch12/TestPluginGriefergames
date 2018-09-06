package de.dennisfricke.testplugin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] parameters) {
        if(commandSender instanceof Player) {
            Player player = (Player)commandSender;
            WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
            wrappedGameProfile.getProperties().clear();
            wrappedGameProfile.getProperties().put("textures", null);
        }
        return false;
    }
}
