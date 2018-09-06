package de.dennisfricke.testplugin.task;

import com.github.games647.changeskin.core.model.skin.SkinModel;
import com.github.games647.changeskin.core.shared.task.SharedSkinSelect;
import de.dennisfricke.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkinSelector extends SharedSkinSelect {

    private final TestPlugin plugin;
    private final Player receiver;

    public SkinSelector(TestPlugin plugin, Player receiver, int targetId) {
        super(plugin.getCore(), targetId);

        this.plugin = plugin;
        this.receiver = receiver;
    }

    @Override
    protected void scheduleApplyTask(SkinModel targetSkin) {
        Bukkit.getScheduler().runTask(plugin, new SkinApplier(plugin, receiver, receiver, targetSkin, true));
    }

    @Override
    public void sendMessageInvoker(String id) {
        plugin.sendMessage(receiver, id);
    }
}
