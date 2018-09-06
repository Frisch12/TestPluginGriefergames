package de.dennisfricke.testplugin.task;

import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.github.games647.changeskin.core.shared.task.SharedInvalidator;
import de.dennisfricke.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinInvalidator extends SharedInvalidator {

    private final TestPlugin plugin;
    private final CommandSender invoker;
    private final Player receiver;

    public SkinInvalidator(TestPlugin plugin, CommandSender invoker, Player receiver) {
        super(plugin.getCore(), receiver.getUniqueId());

        this.plugin = plugin;
        this.invoker = invoker;
        this.receiver = receiver;
    }

    @Override
    public void sendMessageInvoker(String id) {
        plugin.sendMessage(invoker, id);
    }

    @Override
    protected void scheduleApplyTask(SkinModel skinData) {
        Bukkit.getScheduler().runTask(plugin, new SkinApplier(plugin, invoker, receiver, skinData, false));
    }
}
