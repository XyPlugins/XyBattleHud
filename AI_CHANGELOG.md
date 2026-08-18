# AI 更新记录

本文件记录 AI 参与的代码变更，供维护者审查和追溯。

## 2026-08-18 - v1.3.5 经验去重与拾取位置配置

- 根据服主截图确认同一笔 `经验 +10` 被显示成两条拾取提示。
- 在 AkariLevel 经验桥增加轻量去重窗口，默认 `250ms` 内同玩家、同等级组、同经验数值、同显示名和图标只显示一次。
- 新增 `pickup.experience.dedupe-millis` 中文配置，填 `0` 可关闭合并，仍保留可调空间。
- 新增 `pickup.position.right/bottom`，由服务端把右边距和底边距传给 DragonCore `创建拾取`。
- 更新 `XyBattleHud拾取视图.yml`，动态组件使用传入的 `右边距/底边距` 计算位置，保留 v1.3.4 稳定 `index` 叠层逻辑。

## 2026-08-18 - v1.3.4 对齐 Yee 拾取视图

- 回头核对 `YeeCombatView拾取视图.yml`，确认其核心是创建组件后统一执行 `全部向下移动`，直接用 `index * 25` 排列层级。
- 移除 v1.3.3 的“创建序号反推目标层”方案，避免 DragonCore 函数调用链导致显示不稳定。
- XyBattleHud 拾取视图改为新组件 `index=-1`，创建完成后统一 `index+1`，让新条目位于第 0 层、旧条目向上叠。
- 默认关闭开场淡入，只保留结束前淡出，减少刚显示时若隐若现的问题。

## 2026-08-18 - v1.3.3 拾取层距修复

- 根据服主截图确认旧拾取条目已经保留，但多条提示挤在同一区域。
- 放弃依赖组件 `index` 字段和遍历旧组件加层数，改为每条动态组件记录自己的创建序号。
- DragonCore HUD 每帧用 `当前序号 - 组件序号 - 1` 计算目标层数，最新条目固定第 0 层，旧条目自然上移。
- 默认 `pickup.animation.stack-spacing` 从 `25` 调整为 `34`，并在配置/DragonCore 注释中说明挤在一起时调大该值。

## 2026-08-18 - v1.3.2 拾取叠层修复

- 根据服主截图和反馈确认问题不是拾取框颜色或大小，而是新拾取框顶替旧拾取框。
- 定位原因：服务端每次拾取都调用 `sendOpenHud`，DragonCore 会重新打开拾取 HUD，导致旧动态组件被刷新掉。
- 改为每名玩家只首次打开拾取 HUD，后续拾取只调用 `创建拾取` 追加组件，让旧拾取框上移保留。
- 玩家退出和 `/xybh reload` 时清除已打开记录，保证重连或重载后可以重新打开 HUD。

## 2026-08-18 - v1.3.1 拾取队列动画

- 根据服主确认的表现，拾取 HUD 改为“最新在下、旧的上移”的队列叠层。
- DragonCore 拾取视图为每条提示记录出生时间、消失时间、当前层、目标层、淡入淡出时间和移动速度。
- 新增 `PickupAnimationSettings` 与 `pickup.animation` 中文配置段，把显示时长、淡入淡出、最大层数、层间距、滑入距离、滑入速度和上移速度交给配置。
- 服务端只在调用 `创建拾取` 时附带动画参数，不创建额外动画任务，保持拾取高频路径轻量。
- 经验拾取固定走 `experience` 来源，DragonCore HUD 使用头颅框并跳过品质框匹配。
- 版本号提升到 `1.3.1`。

## 2026-08-18 - v1.3.0 经验拾取与来源对调

- 新增 `pickup.experience` 中文配置段，经验提示名、图标、等级组、来源过滤、事件类名和事件变量名都可改。
- 使用 AkariLevel 的 `MemberExpChangeEvent` 做软接入，服务端拿到本次经验增量后再发给 DragonCore 显示。
- 新增 `AkariLevelExpBridge`，通过反射注册事件，不把 AkariLevel 做成编译期硬依赖。
- `dragoncore/XyBattleHud拾取视图.yml` 新增经验模式，普通拾取与经验拾取共用同一套右下角 HUD。
- 根据服主测试反馈，把普通背包和灵魂空间的拾取框视觉映射对调，修正当前资源包的实际显示顺序。
- `BattleHudCommand info` 增加 AkariLevel 经验桥状态，便于现场排查。
- 构建 `gradlew.bat clean test jar` 已通过，版本号提升到 `1.3.0`。

## 2026-08-17 - v1.2.4 品质普通框与灵魂框

- 核对服主 `V0.0.3` 客户端资源包中的 14 张拾取框，确认全部为约 `5:1` 比例，可用于现有 `120 x 22` HUD 背景。
- 将白描、萌黄、气象、极意、归元、传神、浮世分别配置为 `品质普通拾取框N` 与 `品质灵魂拾取框N`。
- 发现群青、无相、鸿蒙图片尚未提供，保持路径为空并回退普通/灵魂默认白描框。
- 根据 DragonCore 现有拾取视图配置验证 `方法.取Yaml值('前缀' & 编号)` 的动态键读取语法。
- 不增加服务端品质事件、物品扫描或依赖；仍在 HUD 创建时只判断一次品质。
- 版本号更新为 `1.2.4`，同步更新 README、更新记录和 AI 使用记录。

## 2026-08-16 - v1.2.3 按物品品质选择拾取框

- 阅读服主提供的 `通用.yml` 和 `默认.yml`，确认品质判断使用 `方法.是否包含(物品, 关键词)`，关键词来自物品名称/Lore。
- 保持品质判断在 DragonCore 客户端完成；XyBattleHud 服务端只继续发送完整 `ItemStack`、数量和 `normal/soul` 来源。
- 在 `dragoncore/XyBattleHud拾取视图.yml` 中增加品质关键词与品质拾取框路径配置，匹配顺序为 `10` 到 `0`。
- 品质图片路径为空时不覆盖来源框，普通拾取仍使用普通框，灵魂空间仍使用灵魂框。
- 保留 `pickup.soul-space-frame-enabled` 的独立开关，不增加 XySoulSpace 或物品插件硬依赖。
- 版本号更新为 `1.2.3`，同步更新 README、CHANGELOG 和 AI 使用记录。

## 2026-08-16 - v1.2.2 灵魂空间专用拾取框

- 分析 XySoulSpace 的 `XySoulSpaceItemDepositEvent`，确认事件会在成功入库后提供玩家、完整物品和 `source`；自动拾取来源为 `pickup`。
- 保持 XySoulSpace 软依赖和现有事件桥，只将 `source=pickup` 的事件标记为 DragonCore HUD 的 `soul` 来源。
- 普通 `PlayerPickupItemEvent` 使用 `normal` 来源，两个来源共用物品缓存、数量、滑入动画和清理逻辑。
- DragonCore `XyBattleHud拾取视图.yml` 根据 `normal/soul` 参数动态选择普通或灵魂空间背景框，不复制整套 HUD。
- 新增 `pickup.soul-space-frame-enabled` 开关：关闭时灵魂空间入库提示回退普通框；`pickup.soul-space-enabled` 继续控制是否显示灵魂空间入库提示。
- 版本号提升到 `1.2.2`，README、更新记录和 AI 使用记录同步更新。

## 2026-08-16 - v1.2.1 拾取 HUD 调用修正与连击固定 HUD

- 根据服主截图中的 DragonCore 报错“方法名：创建拾取”，确认问题出在服务端发送的脚本字符串。
- 将 `sendRunFunction` 里的调用从 `创建拾取('uuid','数量');` 改为 `方法.执行方法('创建拾取','uuid','数量');`。
- 保持 HUD 内部 `局部变量.参数.0/1` 的读取方式不变，只修正触发方式。
- 根据服主截图反馈“连击数不应该悬浮，应该固定在某个位置”，将连击显示从 WorldTexture 改为 DragonCore HUD。
- 新增 `DragonCoreComboBridge`，服务端通过 `sendOpenHud` 与 `sendRunFunction` 调用 `XyBattleHud连击视图.yml` 的 `更新连击/清除连击`。
- 新增 `dragoncore/XyBattleHud连击视图.yml`，用 3 个固定数字位和 1 个连击标签位显示，位置、大小和图片路径由 DragonCore YML 控制。
- 精简 `combo` 服务端配置，移除旧的目标相对坐标、方块尺寸和 WorldTexture 图片路径读取。
- 版本号同步提升到 `1.2.1`，README 和更新记录同步调整。

## 2026-08-14 - v1.2.0 拾取视图

- 根据需求新增右下角拾取提示，服务端只发送 `ItemStack` 缓存和 HUD 函数调用，视觉布局交给 DragonCore YML。
- 参考 `YeeCombatView拾取视图.yml` 的 `slot` 缓存机制，删去品质、来源等复杂参数，仅保留物品、数量和右下角堆叠显示。
- 核对 DragonCore `2.6.2.9` 的 `PacketSender.putClientSlotItem`、`sendOpenHud`、`sendRunFunction` 方法签名，并使用反射保持软依赖。
- 查看 XySoulSpace `1.1.10` 自动拾取实现，确认其普通拾取会取消事件、范围扫描会直接移除地面物品，因此额外软监听 `XySoulSpaceItemDepositEvent`。
- 普通拾取继续监听 Bukkit 1.12.2 `PlayerPickupItemEvent`，按 `stackAmount - remaining` 计算实际拾取数量，避免背包满时误报整组。
- 新增 `pickup` 中文配置段、DragonCore HUD 示例文件和 `/xybh info` 诊断项。
- 增加拾取数量计算单元测试，并执行 `gradlew.bat clean test jar` 验证通过。

## 2026-08-02 - v1.1.2 前缀语义修正

- 根据服主最终确认，`/xybh` 属于管理/排错命令，应保留 XyBattleHud 自身前缀。
- 将命令类前缀切换为 `MessagePrefix.resolveLocal(plugin)`。
- 保留 `resolvePlayer` 用于未来新增玩家玩法提示；当前战斗HUD显示不走聊天前缀。
- 不修改伤害飘字、连击贴图、AP事件桥或DragonCore显示逻辑。

## 2026-08-02 - v1.1.1 XyCore玩家前缀

- 根据服主确认的“可独立插件无XyCore时使用自己前缀”规则，AI辅助新增 `MessagePrefix` 工具。
- 命令反馈优先通过反射读取XyCore `getMessagePrefix()`；没有可用Core时回退到本插件 `messages.prefix`。
- 未把XyCore设为硬依赖，保持XyBattleHud在只有DragonCore/AttributePlus环境下也能启动。
- 本次只统一聊天命令反馈，不改变伤害飘字、连击WorldTexture、ActionBar和Title战斗显示。

## 2026-07-26 - v1.1.0 连击视图

- 根据需求设计同目标连击规则：2 秒超时、切换目标重置、AP 同次事件去重、上限 `999`。
- 增加 `ComboTracker` 状态管理及单元测试，覆盖计数、重复事件、超时、切换目标、可配置上限和绝对 `999` 上限。
- 反编译核对 DragonCore `2.6.2.9` 的 `CoreAPI.setPlayerWorldTexture` 与移除接口签名。
- 使用 WorldTexture 分别发送最多三张数字图片和一张连击样式图片，数字位于样式图前。
- 控制性能开销：每名攻击者一条状态、每名攻击者一组活跃显示、32 格发送范围、全局单一清理任务。
- 重写默认配置的连击部分并补齐中文注释，更新命令诊断、README 和发布记录。
- 在 Paper 1.12.2、Java 8、DragonCore 2.6.2.9、AttributePlus 3.3.3.5、XyCore 0.3.3 的隔离服验证插件正常启用，`/xybh info` 显示龙核连击可用。

## 2026-07-26 - v1.0.1 AP 暴击触发修复

- 反编译确认 AttributePlus 的 `Crit` 组件仅在本次暴击成功时将触发 ID `crit` 写入攻击上下文。
- 增加 `AttrAttributeTriggerEvent.After` 反射监听，缓存本次命中的触发 ID 和属性名称。
- 修正仅依赖 AP 消息事件导致暴击仍显示普通字体的问题。

## 2026-07-26 - v1.0 初始重构

- 分析 GTDamage 2.0.112 的配置、反编译行为与 AttributePlus 事件接入方式。
- 分析 DragonCore 字体配置，确认图片数字依赖客户端“字符到 PNG”映射，不需要服务端读取 DragonCore 字体文件。
- 根据需求创建 XyBattleHud 的独立 Gradle 项目、中文配置、文档和基础单元测试。
- 采用原生 ArmorStand 作为 1.12.2 显示实现。
- 使用 XyCore 公共 `AttributeService` 作为软接入；失败时反射访问 AttributePlus 公共 API。
- 为 AttributePlus 的攻击与消息事件实现反射注册，避免将 AttributePlus 作为编译期强依赖。

## 人工确认项

- 默认普通/暴击字形来自提供的 DragonCore 字体定义。
- 撕裂、钝击等未来类型的具体字符和 PNG 尚未提供，v1 仅提供配置化扩展能力与文档模板。
