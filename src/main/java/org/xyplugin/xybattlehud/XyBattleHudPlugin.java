package org.xyplugin.xybattlehud;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.xyplugin.xybattlehud.attribute.AttributePlusEventBridge;
import org.xyplugin.xybattlehud.attribute.AttributeReader;
import org.xyplugin.xybattlehud.attribute.AttributeReaders;
import org.xyplugin.xybattlehud.command.BattleHudCommand;
import org.xyplugin.xybattlehud.combo.ComboDisplayManager;
import org.xyplugin.xybattlehud.combo.ComboTracker;
import org.xyplugin.xybattlehud.config.PluginSettings;
import org.xyplugin.xybattlehud.damage.DamageListener;
import org.xyplugin.xybattlehud.damage.DamageTypeResolver;
import org.xyplugin.xybattlehud.display.HologramManager;

public final class XyBattleHudPlugin extends JavaPlugin {
    private PluginSettings settings;
    private AttributeReader attributeReader;
    private AttributePlusEventBridge attributePlusBridge;
    private DamageTypeResolver resolver;
    private HologramManager holograms;
    private ComboTracker comboTracker;
    private ComboDisplayManager comboDisplays;
    private boolean runtimeDebug;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            settings = PluginSettings.load(getConfig());
        } catch (RuntimeException failure) {
            getLogger().severe("配置加载失败: " + failure.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        attributePlusBridge = new AttributePlusEventBridge(this);
        holograms = new HologramManager(this);
        comboTracker = new ComboTracker(settings.getCombo().getTimeoutTicks(), settings.getCombo().getMaxCount());
        comboDisplays = new ComboDisplayManager(this);
        reloadServices();
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        BattleHudCommand command = new BattleHudCommand(this);
        PluginCommand pluginCommand = getCommand("xybh");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }
        getLogger().info("XyBattleHud " + getDescription().getVersion() + " 已启用，属性来源: " + attributeReader.getName());
    }

    @Override
    public void onDisable() {
        if (holograms != null) holograms.shutdown();
        if (comboDisplays != null) comboDisplays.shutdown();
    }

    public void reloadPlugin() {
        reloadConfig();
        PluginSettings next = PluginSettings.load(getConfig());
        settings = next;
        runtimeDebug = false;
        comboTracker.clear();
        comboTracker = new ComboTracker(settings.getCombo().getTimeoutTicks(), settings.getCombo().getMaxCount());
        comboDisplays.reload();
        reloadServices();
    }

    private void reloadServices() {
        attributeReader = AttributeReaders.discover(this, settings.isPreferXyCore());
        resolver = new DamageTypeResolver(settings, attributeReader);
        attributePlusBridge.register();
    }

    public PluginSettings getSettings() { return settings; }
    public AttributeReader getAttributeReader() { return attributeReader; }
    public AttributePlusEventBridge getAttributePlusBridge() { return attributePlusBridge; }
    public DamageTypeResolver getResolver() { return resolver; }
    public HologramManager getHolograms() { return holograms; }
    public ComboTracker getComboTracker() { return comboTracker; }
    public ComboDisplayManager getComboDisplays() { return comboDisplays; }

    public void clearDisplays() {
        holograms.clear();
        comboDisplays.clear();
        comboTracker.clear();
    }
    public boolean isDebugEnabled() { return runtimeDebug || (settings != null && settings.isDebug()); }
    public void setRuntimeDebug(boolean runtimeDebug) { this.runtimeDebug = runtimeDebug; }
}
