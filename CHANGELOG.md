# 更新记录

## v1.0.1 - 2026-07-26

- 监听 AttributePlus `AttrAttributeTriggerEvent.After`，直接读取本次实际触发的属性 ID 与名称。
- AP 暴击现在通过触发 ID `crit` 精确识别，不再依赖暴击消息是否开启或事件先后顺序。
- 调试日志增加 `apTriggers`，便于配置撕裂、钝击等后续伤害类型。

## v1.0 - 2026-07-26

首次发布 `XyBattleHud`。

- 从 GTDamage 的宽泛功能集合重构为独立的属性伤害字形显示插件。
- 保留玩家伤害飘字、按类型字符替换、ArmorStand 上浮动画、重载与诊断命令。
- 默认配置普通与暴击两套 DragonCore 字体字符。
- 增加可配置的伤害类型优先级、AP 消息触发词、AP 属性检测和原版暴击识别。
- XyCore 改为可选 AttributeService 读取桥，AttributePlus 也是软依赖。
- 移除权限字体组、怪物血条、伤害粒子压缩、ArcartX/PaiUI/Adyeshach/ProtocolLib 显示实现及无关属性适配。
