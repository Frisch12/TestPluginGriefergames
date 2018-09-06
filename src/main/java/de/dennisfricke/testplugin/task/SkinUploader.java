package de.dennisfricke.testplugin.task;

import com.github.games647.changeskin.core.model.auth.Account;
import com.github.games647.changeskin.core.shared.task.SharedUploader;
import de.dennisfricke.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SkinUploader extends SharedUploader {

    private final TestPlugin plugin;
    private final CommandSender invoker;

    public SkinUploader(TestPlugin plugin, Account owner, String url, CommandSender invoker) {
        super(plugin.getCore(), owner, url);

        this.plugin = plugin;
        this.invoker = invoker;
    }

    @Override
    public void sendMessageInvoker(String key) {
        plugin.sendMessage(invoker, key);
    }

    @Override
    protected void scheduleChangeTask(String oldSkinUrl) {
        Runnable task = new SkinChanger(plugin, owner, url, oldSkinUrl, invoker);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, 60 * 20L);
    }
}
