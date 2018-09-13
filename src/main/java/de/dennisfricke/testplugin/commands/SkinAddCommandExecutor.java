package de.dennisfricke.testplugin.commands;

import de.dennisfricke.testplugin.TestPlugin;
import de.dennisfricke.testplugin.task.SkinPersist;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

public class SkinAddCommandExecutor implements CommandExecutor {

	private TestPlugin plugin;

	public SkinAddCommandExecutor(TestPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] parameters) {
		try {
			if (parameters.length < 1) {
				commandSender.sendMessage("Please provide a player-name");
				return false;
			}
			Optional<UUID> uuid = plugin.getCore().getSkinApi().getUUID(parameters[0]);
			if(!uuid.isPresent()) {
				commandSender.sendMessage("Player not found");
				return false;
			}
			UUID uuid1 = uuid.get();
			Bukkit.getScheduler().runTaskAsynchronously(plugin, new SkinPersist(plugin, commandSender, uuid1));
			return true;
		} catch (Exception ex) {
			plugin.getLog().error("Exception occurred in skin add", ex);
			return false;
		}
	}
}
