# AI 使用记录

## 2026-08-02 / 1.1.2

- 根据服主最终确认，AI 将 XyBattleHud 的 `/xybh` 管理命令反馈改回本插件前缀。
- XyBattleHud 当前没有“玩家获得/开启/完成”类玩法聊天结果，因此不主动使用 XyCore 玩家前缀。
- 保留 `MessagePrefix.resolvePlayer` 给未来如果新增玩家玩法提示时使用；现有命令通过 `resolveLocal` 固定本地前缀。
- 本次不触碰伤害显示和连击显示路径，避免给战斗高频逻辑增加无关开销。

## 2026-08-02 / 1.1.1

- AI根据服主确认的Xy系列消息前缀规则，协助将XyBattleHud命令聊天反馈改为优先读取XyCore前缀。
- XyBattleHud保持可独立运行；未安装、未启用或旧版XyCore不可用时，继续使用本插件 `config.yml -> messages.prefix`。
- 本次不改变伤害飘字、连击图片、ActionBar和Title类战斗显示，避免在战斗HUD中强行显示插件聊天前缀。
- 控制台日志继续保留XyBattleHud插件名。
