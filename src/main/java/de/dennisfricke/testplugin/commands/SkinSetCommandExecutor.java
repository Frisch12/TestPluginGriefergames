package de.dennisfricke.testplugin.commands;

import de.dennisfricke.testplugin.TestPlugin;
import de.dennisfricke.testplugin.task.SkinApplier;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.UUID;

public class SkinSetCommandExecutor implements CommandExecutor {

	private TestPlugin plugin;

	public SkinSetCommandExecutor(TestPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] parameters) {
		try {
			if(parameters.length < 2) {
				commandSender.sendMessage("Invalid command usage. Missing parameters");
				return false;
			}
			String source = parameters[0];
			String dest = parameters[1];
			Optional<UUID> uuid = plugin.getCore().getSkinApi().getUUID(source);
			if(uuid.isPresent()) {
				SkinApplier task = new SkinApplier(plugin, commandSender, Bukkit.getPlayer(dest), plugin.getStorage().getSkin(uuid.get()), true);
				Bukkit.getScheduler().runTask(plugin, task);
			}
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
