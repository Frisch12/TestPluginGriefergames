package de.dennisfricke.testplugin.task;

import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.github.games647.changeskin.core.shared.MessageReceiver;
import com.github.games647.changeskin.core.shared.task.SharedDownloader;
import de.dennisfricke.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SkinPersist implements Runnable, MessageReceiver {
	protected final TestPlugin plugin;
	private final CommandSender invoker;
	private UUID targetUUID;

	public SkinPersist(TestPlugin plugin, CommandSender invoker, UUID targetUUID) {
		this.plugin = plugin;
		this.invoker = invoker;
		this.targetUUID = targetUUID;
	}

	@Override
	public void sendMessageInvoker(String id) {
		plugin.sendMessage(invoker, id);
	}

	@Override
	public void run() {
		SkinModel storedSkin = this.plugin.getStorage().getSkin(this.targetUUID);
		if (storedSkin == null) {
			storedSkin = this.plugin.getCore().getSkinApi().downloadSkin(this.targetUUID).orElse(null);
			this.plugin.getStorage().save(storedSkin);
		}
	}
}
