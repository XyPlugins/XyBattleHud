package org.xyplugin.xybattlehud.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.xyplugin.xybattlehud.XyBattleHudPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class BattleHudCommand implements CommandExecutor, TabCompleter {
    private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "XyBattleHud" + ChatColor.DARK_GRAY + "] ";
    private final XyBattleHudPlugin plugin;

    public BattleHudCommand(XyBattleHudPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("xybattlehud.admin")) {
            sender.sendMessage(PREFIX + ChatColor.RED + "你没有权限。");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(java.util.Locale.ROOT);
        switch (sub) {
            case "reload":
                try {
                    plugin.reloadPlugin();
                    sender.sendMessage(PREFIX + ChatColor.GREEN + "配置已重载。");
                } catch (RuntimeException failure) {
                    sender.sendMessage(PREFIX + ChatColor.RED + "重载失败: " + failure.getMessage());
                }
                return true;
            case "info":
                sender.sendMessage(PREFIX + ChatColor.WHITE + "v" + plugin.getDescription().getVersion()
                        + ChatColor.GRAY + " | 属性来源: " + plugin.getAttributeReader().getName()
                        + " | AP事件: " + (plugin.getAttributePlusBridge().isAvailable() ? "可用" : "不可用")
                        + " | 飘字: " + plugin.getHolograms().size());
                return true;
            case "clear":
                plugin.getHolograms().clear();
                sender.sendMessage(PREFIX + ChatColor.GREEN + "已清除全部伤害飘字。");
                return true;
            case "debug":
                boolean enabled = args.length < 2 ? !plugin.isDebugEnabled() : "on".equalsIgnoreCase(args[1]);
                plugin.setRuntimeDebug(enabled);
                sender.sendMessage(PREFIX + ChatColor.YELLOW + "调试日志已" + (enabled ? "开启" : "关闭") + "。");
                return true;
            default:
                sender.sendMessage(PREFIX + ChatColor.GRAY + "/xybh reload | info | clear | debug [on|off]");
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
}

