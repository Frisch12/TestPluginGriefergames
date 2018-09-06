package de.dennisfricke.testplugin.task;

import com.github.games647.changeskin.core.shared.task.SharedNameResolver;
import de.dennisfricke.testplugin.TestPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NameResolver extends SharedNameResolver {

    private final TestPlugin plugin;
    private final CommandSender invoker;
    private final Player player;

    public NameResolver(TestPlugin plugin, CommandSender invoker, String targetName, Player player
            , boolean keepSkin) {
        super(plugin.getCore(), targetName, keepSkin);

        this.plugin = plugin;
        this.invoker = invoker;
        this.player = player;
    }

    @Override
    public void sendMessageInvoker(String id) {
        plugin.sendMessage(invoker, id);
    }

    @Override
    protected boolean hasSkinPermission(UUID uuid) {
        if (invoker == null || !core.getConfig().getBoolean("skinPermission"))
            return true;

        return plugin.hasSkinPermission(invoker, uuid, true);
    }

    @Override
    protected void scheduleDownloader(UUID uuid) {
        //run this is the same thread
        new SkinDownloader(plugin, invoker, player, uuid, keepSkin).run();
    }
}
