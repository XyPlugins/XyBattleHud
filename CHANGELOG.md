# 更新记录

## v1.1.0 - 2026-07-26

- 新增同一玩家连续攻击同一目标的连击计数，切换目标或超时自动重置。
- 连击数字排列在样式图片前；普攻使用 `连击数_1.png`，暴击使用 `连击数_2.png`。
- 使用 DragonCore WorldTexture 显示连击，避免 ArmorStand 字体图片黑底。
- 连击位置、数字大小、样式图片大小、超时和起显次数均可通过中文注释配置调整。
- 连击最大值硬限制为 `999`，达到后不再增长，也不会生成四位数字贴图。
- 同一攻击者只保留最新一组连击，贴图仅发送给同世界 32 格内玩家，并共用一个清理任务。
- `/xybh info` 增加 DragonCore 连击接口状态，`reload` 与 `clear` 同步管理连击状态和贴图。

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
