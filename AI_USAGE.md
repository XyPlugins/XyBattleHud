# AI 使用记录

## 2026-08-02 / 1.1.1

- AI根据服主确认的Xy系列消息前缀规则，协助将XyBattleHud命令聊天反馈改为优先读取XyCore前缀。
- XyBattleHud保持可独立运行；未安装、未启用或旧版XyCore不可用时，继续使用本插件 `config.yml -> messages.prefix`。
- 本次不改变伤害飘字、连击图片、ActionBar和Title类战斗显示，避免在战斗HUD中强行显示插件聊天前缀。
- 控制台日志继续保留XyBattleHud插件名。
