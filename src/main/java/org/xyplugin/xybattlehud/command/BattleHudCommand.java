package org.xyplugin.xybattlehud.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;
import org.xyplugin.xybattlehud.util.MessagePrefix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BattleHudCommand implements CommandExecutor, TabCompleter {
    private final XyBattleHudPlugin plugin;

    public BattleHudCommand(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xybattlehud.admin")) {
            sender.sendMessage(prefix() + ChatColor.RED + "你没有权限。");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(java.util.Locale.ROOT);
        switch (sub) {
            case "reload":
                try {
                    plugin.reloadPlugin();
                    sender.sendMessage(prefix() + ChatColor.GREEN + "配置已重载。");
                } catch (RuntimeException failure) {
                    sender.sendMessage(prefix() + ChatColor.RED + "重载失败: " + failure.getMessage());
                }
                return true;
            case "info":
                sender.sendMessage(prefix() + ChatColor.WHITE + "v" + plugin.getDescription().getVersion()
                        + ChatColor.GRAY + " | 伤害渲染: " + plugin.getSettings().getDamageRenderer().getConfigValue()
                        + ChatColor.GRAY + " | 属性来源: " + plugin.getAttributeReader().getName()
                        + " | AP事件: " + (plugin.getAttributePlusBridge().isAvailable() ? "可用" : "不可用")
                        + " | 龙核连击HUD: " + (plugin.getComboDisplays().isAvailable() ? "可用" : "不可用")
                        + " | 龙核拾取: " + (plugin.getPickupDisplays().isAvailable() ? "可用" : "不可用")
                        + " | 灵魂仓库拾取: " + (plugin.getSoulSpacePickupBridge().isAvailable() ? "可用" : "不可用")
                        + " | 经验事件: " + (plugin.getAkariLevelExpBridge().isAvailable() ? "可用" : "不可用")
                        + " | 金币事件: " + (plugin.getMythicMobsMoneyBridge().isAvailable() ? "可用" : "不可用")
                        + " | 显示: " + (plugin.getHolograms().size() + plugin.getComboDisplays().size()));
                return true;
            case "clear":
                plugin.clearDisplays();
                sender.sendMessage(prefix() + ChatColor.GREEN + "已清除伤害飘字和连击显示。");
                return true;
            case "debug":
                boolean enabled = args.length < 2 ? !plugin.isDebugEnabled() : "on".equalsIgnoreCase(args[1]);
                plugin.setRuntimeDebug(enabled);
                sender.sendMessage(prefix() + ChatColor.YELLOW + "调试日志已" + (enabled ? "开启" : "关闭") + "。");
                return true;
            default:
                sender.sendMessage(prefix() + ChatColor.GRAY + "/xybh reload | info | clear | debug [on|off]");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("xybattlehud.admin")) return Collections.emptyList();
        List<String> options = args.length == 2 && "debug".equalsIgnoreCase(args[0])
                ? Arrays.asList("on", "off") : Arrays.asList("reload", "info", "clear", "debug");
        String input = args.length == 0 ? "" : args[args.length - 1].toLowerCase(java.util.Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (String option : options) if (option.startsWith(input)) result.add(option);
        return result;
    }

    private String prefix() {
        return MessagePrefix.resolveLocal(plugin);
    }
}
