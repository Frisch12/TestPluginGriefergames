package de.dennisfricke.testplugin;


import com.github.games647.changeskin.core.ChangeSkinCore;
import com.github.games647.changeskin.core.CommonUtil;
import com.github.games647.changeskin.core.PlatformPlugin;
import com.github.games647.changeskin.core.SkinStorage;
import com.github.games647.changeskin.core.message.ChannelMessage;
import com.github.games647.changeskin.core.message.NamespaceKey;
import com.github.games647.changeskin.core.model.UserPreference;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.dennisfricke.testplugin.bungee.CheckPermissionListener;
import de.dennisfricke.testplugin.bungee.SkinUpdateListener;
import de.dennisfricke.testplugin.commands.SkinAddCommandExecutor;
import de.dennisfricke.testplugin.commands.SkinSetCommandExecutor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import static com.github.games647.changeskin.core.message.CheckPermMessage.CHECK_PERM_CHANNEL;
import static com.github.games647.changeskin.core.message.ForwardMessage.FORWARD_COMMAND_CHANNEL;
import static com.github.games647.changeskin.core.message.PermResultMessage.PERMISSION_RESULT_CHANNEL;
import static com.github.games647.changeskin.core.message.SkinUpdateMessage.UPDATE_SKIN_CHANNEL;

public class TestPlugin extends JavaPlugin implements PlatformPlugin<CommandSender> {
    private final ConcurrentMap<UUID, UserPreference> loginSessions = CommonUtil.buildCache(2 * 60, -1);
    private Logger logger = CommonUtil.createLoggerFromJDK(getLogger());
    @Getter
    private final ChangeSkinCore core = new ChangeSkinCore(this);

    @Getter
    private final BukkitSkinAPI api = new BukkitSkinAPI(this);

    @Getter
    private boolean bungeeCord;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        try {
            bungeeCord = getServer().spigot().getConfig().getBoolean("settings.bungeecord");
        } catch (Exception | NoSuchMethodError ex) {
            logger.warn("Cannot check bungeecord support. You use a non-Spigot build");
        }

        registerCommands();

        try {
            core.load(!bungeeCord);
        } catch (Exception ex) {
            logger.error("Error initializing plugin. Disabling...", ex);
            setEnabled(false);
            return;
        }

        if (bungeeCord) {
            logger.info("BungeeCord detected. Activating BungeeCord support");
            logger.info("Make sure you installed the plugin on BungeeCord too");

            //outgoing
            Messenger messenger = getServer().getMessenger();
            String permissionResultChannel = new NamespaceKey(getName(), PERMISSION_RESULT_CHANNEL).getCombinedName();
            String forwardChannel = new NamespaceKey(getName(), FORWARD_COMMAND_CHANNEL).getCombinedName();
            messenger.registerOutgoingPluginChannel(this, permissionResultChannel);
            messenger.registerOutgoingPluginChannel(this, forwardChannel);

            //incoming
            String updateChannel = new NamespaceKey(getName(), UPDATE_SKIN_CHANNEL).getCombinedName();
            String permissionChannel = new NamespaceKey(getName(), CHECK_PERM_CHANNEL).getCombinedName();
            messenger.registerIncomingPluginChannel(this, updateChannel, new SkinUpdateListener(this));
            messenger.registerIncomingPluginChannel(this, permissionChannel, new CheckPermissionListener(this));
        } else {
            getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        }
    }

    private void registerCommands() {
        getCommand("skin-add").setExecutor(new SkinAddCommandExecutor(this));
        getCommand("skin-set").setExecutor(new SkinSetCommandExecutor(this));
    }

    public UserPreference getLoginSession(UUID id) {
        return loginSessions.get(id);
    }

    public void startSession(UUID id, UserPreference preferences) {
        loginSessions.put(id, preferences);
    }

    public void endSession(UUID id) {
        loginSessions.remove(id);
    }

    public SkinStorage getStorage() {
        return core.getStorage();
    }

    @Override
    public Path getPluginFolder() {
        return getDataFolder().toPath();
    }

    @Override
    public Logger getLog() {
        return logger;
    }

    @Override
    public void sendMessage(CommandSender commandSender, String key) {
        String message = core.getMessage(key);
        if (message != null && commandSender != null) {
            commandSender.sendMessage(message);
        }
    }

    public void sendPluginMessage(PluginMessageRecipient sender, ChannelMessage message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        message.writeTo(out);

        NamespaceKey channel = new NamespaceKey(getName(), message.getChannelName());
        sender.sendPluginMessage(this, channel.getCombinedName(), out.toByteArray());
    }

    @Override
    public boolean hasSkinPermission(CommandSender commandSender, UUID uuid, boolean sendMessage) {
        if (commandSender.hasPermission(getName().toLowerCase() + ".skin.whitelist." + uuid)) {
            return true;
        }

        //disallow - not whitelisted or blacklisted
        if (sendMessage) {
            sendMessage(commandSender, "no-permission");
        }

        return false;
    }
}
