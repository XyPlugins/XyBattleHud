# 更新记录

## v1.3.12 - 2026-09-02

- 修复拾取背景缩小到 `80 x 18` 后，物品、品质框、经验、金币和文字仍按旧 `169 x 41` 坐标计算而与背景分离的问题。
- DragonCore 文件新增简洁的 `布局.拾取框宽度/拾取框高度`；所有子组件以同一个背景左上角为锚点，并按背景尺寸等比例定位。
- 新提示默认增加 `150ms` 淡入，减少同批多条提示同时硬切出现的感觉。
- 默认层间距从 `43` 调整为 `22`，适配 `18` 高度的紧凑拾取框。
- HUD 会把旧配置中的淡入 `0` 和层间距 `43` 自动迁移为新默认值，只替换 YML 也能生效；淡入填 `-1` 可关闭。
- 保持队列、品质判断、XySoulSpace、经验和金币事件逻辑不变。

## v1.3.11 - 2026-09-01

- 拾取 HUD 适配新版 `169 x 41` 的 `战斗视图/拾取视图/拾取框.png`，普通背包、灵魂空间、经验和金币统一使用该背景。
- 品质不再替换整张拾取背景；命中品质时只在左侧物品格叠加独立的 `30 x 30` 品质框。
- 品质关键词和 GIF 路径与新版 `ItemEffect.yml` 前七项同步：白描、传神、浮世、极意、气象、青痕、石墨。
- 没有品质的物品不显示品质框；经验和金币也不参与品质判断。
- 默认拾取队列层间距改为 `43`，HUD 会自动限制最低值，避免新版背景上下重叠。
- 兼容 XySoulSpace 1.1.17 的MM掉落直接投递模式。
- 新增软监听 `XySoulSpaceItemInventoryDeliveryEvent`；玩家关闭个人灵魂入库后，MM掉落直接进入背包时显示普通来源拾取提示。
- 提示数量严格使用XYSS报告的实际入包数量，不包含背包装不下并重新落地的余量。
- 灵魂空间直投入库继续监听 `XySoulSpaceItemDepositEvent` 并显示灵魂空间来源拾取提示。
- 事件类仍通过XySoulSpace自己的类加载器反射接入，不增加编译期或硬依赖；旧版XYSS没有新事件时自动跳过。
- `pickup.soul-space-enabled: false` 只关闭灵魂入库提示，不再连带关闭背包直投提示。

## v1.3.10 - 2026-09-01

- 修正旧 `config.yml` 没有 `display.renderer` 时仍回退到 `armorstand` 的问题。
- 现在缺省渲染方式与默认配置保持一致：未填写 `display.renderer` 时自动使用 `dragoncore-headtag`。
- 文档补充说明：需要退回原版名字显示时，手动填写 `display.renderer: 'armorstand'`。

## v1.3.9 - 2026-09-01

- 新增 `display.renderer`，伤害飘字可在旧版 `armorstand` 和新版 `dragoncore-headtag` 之间切换。
- `dragoncore-headtag` 模式下，服务端仍创建临时 ArmorStand 负责位置和上浮，但隐藏原版名字，并用 `display.headtag-marker` 写入给 DragonCore 识别的标记。
- 新增 `dragoncore/HeadTag/XyBattleHud伤害飘字.yml`，通过 DragonCore `label shadow: false` 显示伤害字符，避免原版实体名字的黑影。
- `/xybh info` 增加当前伤害渲染模式，方便现场确认是否已经切到 HeadTag。
- README、默认配置、AI 使用记录和 AI 更新记录同步补充去黑影说明。

## v1.3.8 - 2026-08-19

- 修复带品质检测的普通背包框与灵魂空间框被反向套用的问题。
- 普通背包品质提示现在使用 `品质普通拾取框N`，灵魂空间品质提示使用 `品质灵魂拾取框N`。
- 保持基础白描默认框的兼容逻辑不变：当前资源包里基础白描两张文件名与视觉仍是反的。
- README、DragonCore 拾取视图注释和 AI 记录同步说明“基础白描反向、品质框按文件名正常”的规则。

## v1.3.7 - 2026-08-18

- DragonCore 拾取视图将金币图标拆为独立的 `拾取金币图标` 组件，金币大小和坐标不再影响经验图标。
- 保留并采用服主微调过的拾取视图参数：经验/金币图标默认 `13 x 12`，位置使用当前调好的 `+4/+4`。
- 默认 `pickup.position.right` 调整为 `50`，与服主当前测试配置保持一致。
- README、AI 使用记录和 AI 更新记录同步说明金币图标的独立调整位置。

## v1.3.6 - 2026-08-18

- 新增 MythicMobs `money` 掉落拾取提示，适配 `Drops: - money 10 1` 这类 Vault 金币掉落。
- 金币提示不监听 Vault 余额变化，只软监听 MythicMobs 掉落事件，避免商店、转账、指令发钱误触发。
- 新增 `pickup.money` 中文配置，可调整启用开关、显示名、图标路径和重复合并时间。
- DragonCore 拾取视图新增 `money` 文本模式，金币与经验一样显示为图标、名称和 `+数量`。
- `/xybh info` 增加金币事件状态。

## v1.3.5 - 2026-08-18

- 修复 AkariLevel 某些情况下同一笔经验提示重复显示两次的问题。
- 新增 `pickup.experience.dedupe-millis`，短时间内同玩家同数值经验只显示一次；填 `0` 可关闭合并。
- 新增 `pickup.position.right` 与 `pickup.position.bottom`，拾取 HUD 右下角位置现在可直接在服务端配置中调整。
- DragonCore 拾取视图读取服务端传入的位置参数，不再需要手改 `w-8` / `h-74`。

## v1.3.4 - 2026-08-18

- 对照 YeeCombatView 拾取视图，拾取队列回退为稳定的 `index` 叠层写法。
- 新条目创建时先设为 `index=-1`，全部组件创建完成后统一 `index+1`；最新条目最终为第 0 层，旧条目向上叠。
- 取消开场淡入的默认效果，只保留结束前淡出，避免拾取框刚出现时若隐若现。
- 默认 `pickup.animation.stack-spacing` 回到 `25`，更贴近 Yee 的已验证配置。

## v1.3.3 - 2026-08-18

- 修复拾取条目保留后仍挤在一起的问题：DragonCore HUD 改为按创建序号计算目标层数。
- 默认 `pickup.animation.stack-spacing` 从 `25` 调整为 `34`，更适合当前拾取框在游戏内的显示高度。
- DragonCore 拾取视图注释补充：如果多条提示仍然挤在一起，继续调大 `stack-spacing`。

## v1.3.2 - 2026-08-18

- 修复拾取 HUD 每次拾取都重新打开导致旧拾取框被新拾取框顶替的问题。
- DragonCore 拾取 HUD 现在每名玩家只首次打开，后续拾取只调用 `创建拾取` 追加动态组件。
- 玩家退出或 `/xybh reload` 后会清除已打开记录，避免重连和重载后 HUD 不重新打开。

## v1.3.1 - 2026-08-18

- 拾取视图动画改为队列式叠层：新提示在最下面，旧提示向上移动，并按各自时间独立淡出。
- 新增 `pickup.animation` 中文配置，可调整显示时长、淡入淡出、最大层数、层间距、滑入距离、滑入速度和上移速度。
- 经验拾取固定使用带头颅的普通背包拾取框，不参与物品品质框匹配。
- 服务端只把动画参数传给 DragonCore HUD，不增加额外高频任务。
- 版本号提升到 `1.3.1`，构建已验证通过。

## v1.3.0 - 2026-08-18

- 拾取视图新增经验提示，经验值、显示名、图标、等级组和来源过滤都放进 `config.yml`。
- 经验事件插件名、事件类名、玩家变量名、经验变量名、等级组变量名和来源变量名也可在配置中修改。
- 经验拾取通过 AkariLevel 事件软接入，不增加编译期硬依赖；没有安装 AkariLevel 时自动跳过。
- DragonCore 拾取视图新增经验模式，普通物品和经验提示共用同一套右下角 HUD。
- 按服主当前资源包习惯，对调了普通背包与灵魂空间拾取框的视觉映射。
- `/xybh info` 现在会额外显示 AkariLevel 经验桥状态。
- 版本号提升到 `1.3.0`，构建已验证通过。

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
