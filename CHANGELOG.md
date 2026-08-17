# 更新记录

## v1.2.4 - 2026-08-17

- 品质拾取框改为普通背包与灵魂空间两套独立图片路径。
- 已直接配置白描、萌黄、气象、极意、归元、传神、浮世的普通/灵魂拾取框。
- `待锻造` 沿用白描拾取框；未匹配品质词和暂未提供图片的群青、无相、鸿蒙都回退默认白描框。
- 当 `pickup.soul-space-frame-enabled` 关闭时，灵魂空间入库会发送 `normal` 来源，因此自动使用对应品质的普通框。

## v1.2.3 - 2026-08-16

- 拾取视图新增按物品品质切换拾取框的能力。
- 复用 DragonCore ItemTip 的品质关键词，客户端直接从缓存的 `ItemStack` 名称/Lore 判断品质。
- 品质框匹配优先级为 `10 -> 0`；品质图片路径留空时回退到普通拾取框或灵魂空间拾取框。
- 新增 `图片.品质拾取框启用`、`品质判断词0-10` 和 `品质拾取框0-10` 中文配置。
- 不增加服务端品质 API，不改变普通拾取数量计算和 XySoulSpace 联动。

## v1.2.2 - 2026-08-16

- 普通拾取进入玩家背包时使用普通拾取框；XySoulSpace 自动拾取成功入库时可使用灵魂空间专用拾取框。
- 新增 `pickup.soul-space-frame-enabled`：关闭后灵魂空间入库仍显示提示，但回退使用普通拾取框；`soul-space-enabled` 仍负责整体关闭灵魂空间入库提示。
- DragonCore 拾取 HUD 现在接收 `normal/soul` 来源参数，并在 `图片 -> 普通拾取框/灵魂空间拾取框` 两个路径之间切换。
- 默认灵魂空间图片路径为 `战斗视图/拾取视图/灵魂拾取框.png`。

## v1.2.1 - 2026-08-16

- 修复拾取视图的 DragonCore 调用方式：服务端现在通过 `方法.执行方法('创建拾取', ...)` 触发 HUD 函数，不再把 `创建拾取(...)` 当作原始方法名发送给 DragonCore。
- 连击显示从 DragonCore WorldTexture 改为固定 DragonCore HUD：不再跟随怪物漂浮，只刷新攻击者自己的屏幕 HUD。
- 新增 [dragoncore/XyBattleHud连击视图.yml](dragoncore/XyBattleHud连击视图.yml)，连击位置、数字大小、图片路径都在该文件里用中文注释调整。
- `combo` 配置精简为 `hud-name`、`update-function`、`clear-function`，服务端只负责计数、暴击状态和显示时长。
- 切换目标或连击低于起显次数时会立即清除旧连击 HUD，避免旧连击数残留。

## v1.2.0 - 2026-08-14

- 新增右下角拾取视图：玩家拾取后显示拾取框、真实物品图标、物品名和本次数量。
- 新增 DragonCore `PacketSender.putClientSlotItem` 与 `sendRunFunction` 反射桥，保持 DragonCore 软依赖。
- 新增 [dragoncore/XyBattleHud拾取视图.yml](dragoncore/XyBattleHud拾取视图.yml)，可直接放入 `plugins/DragonCore/Gui/` 使用。
- 普通拾取监听 1.12.2 `PlayerPickupItemEvent`，按掉落堆数量减去 `event.getRemaining()` 计算实际拾取数量。
- 软接入 XySoulSpace `XySoulSpaceItemDepositEvent`；自动拾取进入灵魂仓库时也会显示同一套拾取提示。
- `/xybh info` 增加 DragonCore 拾取接口与 XySoulSpace 拾取桥状态。
- 默认配置新增 `pickup` 中文注释段，保持设置简洁，不加入品质、来源、权限组等复杂逻辑。
- 新增拾取数量计算单元测试，版本号更新为 `1.2.0`。

## v1.1.2 - 2026-08-02

- 按服主最终确认收回 `/xybh` 命令前缀：help、reload、info、clear、debug 和权限不足全部保留 XyBattleHud 自身前缀。
- 保留 `MessagePrefix.resolvePlayer` 作为未来玩家玩法聊天提示的可选入口，但当前 BattleHud 没有需要展示给玩家的系统玩法结果。
- 本次不改变伤害飘字、连击 WorldTexture、AttributePlus 事件桥、ActionBar、Title 和战斗显示性能。
- 更新 README、AI 使用记录、AI 更新记录和版本号。

## v1.1.1 - 2026-08-02

- 命令聊天反馈前缀改为优先读取XyCore 0.3.11+ 的 `messages.prefix`。
- 保持XyCore软依赖：未安装、未启用或API不可用时，继续使用本插件 `messages.prefix` 独立运行。
- 新增轻量 `MessagePrefix` 工具，通过反射读取Core前缀，避免把XyCore作为硬依赖。
- 战斗显示、ActionBar、Title、伤害字形和连击图片不强制追加聊天前缀。
- 同步更新README、AI记录、默认配置注释和版本号。

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
