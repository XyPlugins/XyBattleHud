# XyBattleHud

`XyBattleHud` 是面向 Paper/Spigot 1.12.2 的轻量战斗视图插件。伤害数字通过罕见 Unicode 字符映射为客户端图片，并可交给 DragonCore HeadTag 渲染以去掉原版黑影；连击数通过 DragonCore HUD 固定在屏幕位置显示，拾取提示通过 DragonCore HUD 显示真实物品图标、经验/金币图标、名称和数量。

当前版本专注于伤害类型字形、连击显示和拾取提示，不包含怪物血条等无关功能。

## 特性

- 监听玩家近战与玩家投射物造成的有效伤害。
- 使用临时 `ArmorStand` 定位、上浮并自动清理伤害数字，兼容 1.12.2。
- 伤害飘字支持 `armorstand` 和 `dragoncore-headtag` 两种渲染方式；后者可通过 DragonCore `label shadow: false` 去掉字体图片黑影。
- 伤害类型完全由 `config.yml -> damage-types` 配置，不包含不同权限组字体功能。
- 默认支持普通伤害与暴击伤害两套字形。
- 同一玩家连续攻击同一目标时显示连击数，切换目标或超时后重置。
- 普攻连击使用 `连击数_1.png`，暴击连击使用 `连击数_2.png`，数字排列在样式图片前面。
- 连击位置、数字大小、样式图片大小在 DragonCore 连击视图 YML 中调整，计数最大为 `999`。
- 连击采用 DragonCore HUD，不使用 ArmorStand 名称，因此没有字体图片黑底，也不会跟随怪物漂浮。
- 玩家拾取物品后可在右下角显示拾取框、真实物品图标、物品名和本次数量。
- 拾取提示可配置屏幕位置、显示时长、淡入淡出、最大层数、层间距、滑入速度和上移速度。
- 经验获得时可在右下角显示经验提示，显示名和图标都能在 `config.yml` 修改。
- 经验提示内置短时间去重，避免同一笔经验被等级插件重复抛出时显示两条。
- MythicMobs `money` 掉落可显示金币提示，显示名和图标都能在 `config.yml` 改。
- 兼容 XySoulSpace 自动拾取；所有来源使用统一拾取背景，物品品质框默认交给 ItemEffect 全局规则绘制。
- 可用 AttributePlus 的本次属性触发事件、攻击消息、攻击者属性和原版下落暴击判断识别类型。
- 可选接入 XyCore 的 `AttributeService`；未安装 XyCore 时会直接读取 AttributePlus API，二者都不存在时仍可显示普通伤害与原版暴击。
- 支持第三方通过实体 metadata `xybattlehud.damage-type` 指定已配置类型。

## 环境

- Java 8 或更高版本。
- Paper/Spigot 1.12.2。
- 可选：AttributePlus，默认属性来源。
- 可选：XyCore。仅作为稳定的 AttributePlus 读取桥，不是硬依赖。
- DragonCore：伤害字体映射本身不要求服务端 API；启用伤害 HeadTag 去黑影、连击 HUD 和拾取 HUD 时必须安装。
- 可选：XySoulSpace。灵魂入库监听 `XySoulSpaceItemDepositEvent`；1.1.17起的背包直投监听 `XySoulSpaceItemInventoryDeliveryEvent`。
- 可选：AkariLevel。启用经验拾取时，本插件会软监听其经验变更事件。
- 可选：MythicMobs + Vault。启用金币拾取时，本插件会软监听 MythicMobs 的 money 掉落事件。
- 客户端：DragonCore 字体配置、伤害数字 PNG、连击 PNG 和拾取框 PNG。

## 安装

1. 将 `XyBattleHud-1.3.14.jar` 放入服务端 `plugins` 目录并重启。
2. 将 DragonCore 安装到服务端和玩家客户端。DragonCore `2.6.2.9` 在 1.12.2 服务端建议使用 Java 8。
3. 将 DragonCore 字体定义和 PNG 放入客户端实际加载的资源目录。
4. 如果使用默认的 `dragoncore-headtag` 伤害渲染，把 [XyBattleHud伤害飘字.yml](dragoncore/HeadTag/XyBattleHud伤害飘字.yml) 放入 `plugins/DragonCore/HeadTag/`。
5. 将 [XyBattleHud连击视图.yml](dragoncore/XyBattleHud连击视图.yml) 和 [XyBattleHud拾取视图.yml](dragoncore/XyBattleHud拾取视图.yml) 放入 `plugins/DragonCore/Gui/`。
6. 把 `拾取框.png` 放到客户端 `DragonCore/战斗视图/拾取视图/`；ItemEffect 的七个品质 GIF 保持位于 `DragonCore/icon/品质框/`。
7. 如果要显示经验拾取，确认客户端有 `战斗视图/属性图标/经验加成图标.png`，并在 `config.yml` 里改 `pickup.experience`。
8. 如果要显示金币拾取，确认客户端有 `战斗视图/属性图标/金币图标.png`，并在 `config.yml` 里改 `pickup.money`。
9. 确认 [config.yml](src/main/resources/config.yml) 中 `digits` 字符、`display.renderer`、`display.headtag-marker`、`combo.hud-name`、`pickup.hud-name` 与 DragonCore 文件一致。
10. 使用 `/xybh info` 检查伤害渲染、属性来源、AttributePlus 事件、龙核连击 HUD、龙核拾取、AkariLevel 经验桥、MythicMobs 金币桥和灵魂仓库拾取状态。

默认字形采用已验证的艾尔字体字符：

| 类型 | 数字字符 |
| --- | --- |
| `normal` | `鳀霕顬櫠螱藁黉镛铴韮` |
| `critical` | `騜巟獚奛兤榥宺鎤琥怳` |

默认暴击前缀为 `闊`。这些字符必须在 DragonCore 的字体配置中对应到 PNG。

## 工作方式

```text
AttributePlus / Bukkit 伤害事件
        -> XyBattleHud 选择 damage type
        -> 数字 123 替换为配置字形
        -> 服务端临时 ArmorStand 作为位置锚点
        -> armorstand 模式：直接显示原版 CustomName
        -> dragoncore-headtag 模式：CustomName 写入 XYBH_DAMAGE: 标记，由 DragonCore HeadTag label 显示
        -> 客户端字体系统把字符绘制为 PNG
```

服务端不知道 PNG 的路径。DragonCore 在客户端已经注册了字符到图片的映射后，普通聊天、实体名和全息字里出现相同字符都会使用该图片字形。这就是插件无需 DragonCore API 也能显示图片数字的原因。

如果使用 `display.renderer: 'dragoncore-headtag'`，XyBattleHud 会把实体原版名字隐藏，只保留 `display.headtag-marker` 标记给 DragonCore 识别。DragonCore 的 [XyBattleHud伤害飘字.yml](dragoncore/HeadTag/XyBattleHud伤害飘字.yml) 再用 `label` 组件显示去掉标记后的伤害字符，并设置 `shadow: false`，这样就不会出现字体图片后面的黑影。v1.3.10 起，旧配置里没有写 `display.renderer` 时也会默认走 `dragoncore-headtag`；若临时不想用 DragonCore HeadTag，可以把 `display.renderer` 改回 `armorstand`。

连击与伤害数字不同。连击需要固定在玩家屏幕某个位置，因此服务端通过 DragonCore `PacketSender.sendOpenHud` 打开 `XyBattleHud连击视图.yml`，再调用 HUD 里的 `更新连击` 函数。服务端只发送连击数量、是否暴击和显示时长；数字图片路径、位置和大小都由 DragonCore GUI 文件决定。

```text
连续命中同一目标
        -> XyBattleHud 计算连击数，最大 999
        -> 调用 XyBattleHud连击视图.yml 的 更新连击
        -> 客户端 HUD 固定显示数字图片 + 连击数图片
```

拾取视图也不同于伤害数字。XyBattleHud 会把本次拾取的 `ItemStack` 发到 DragonCore 客户端临时物品缓存，再调用 HUD 里的 `创建拾取` 函数。普通背包拾取会传入 `normal`，自动进入灵魂空间会传入 `soul`；新版 HUD 的两种来源共用 `拾取框.png`。`slot` 组件根据缓存 key 渲染真实物品图标，文字组件读取物品名并拼接 `+数量`。如果 DragonCore 的 `ItemEffect.yml` 已配置全局品质框，它会自动作用到 HUD 物品槽；XyBattleHud 内置品质框默认关闭，避免出现双框。

经验拾取不走物品缓存。XyBattleHud 监听 AkariLevel 的经验变更事件，拿到本次增加的经验值后，把可配置的经验名称和图标路径发给 DragonCore HUD。经验使用统一拾取背景，不参与物品品质框匹配。

金币拾取也不走物品缓存。MythicMobs 的 `money 10 1` 会通过 Vault 给击杀者发钱，XyBattleHud 只监听 MythicMobs 的本次 money 掉落事件，拿到金币数量后发给 DragonCore HUD。它不会监听 Vault 余额变化，所以商店、转账、指令发钱不会弹拾取框。

```text
PlayerPickupItemEvent / XySoulSpaceItemDepositEvent
        -> XyBattleHud 计算本次拾取数量
        -> DragonCore putClientSlotItem 缓存 ItemStack
        -> 调用 XyBattleHud拾取视图.yml 的 创建拾取(来源)
        -> 客户端 HUD 显示统一背景和物品槽，ItemEffect 可自动为物品槽叠加品质框
        -> 显示物品图标、名称和数量
```

```text
AkariLevel MemberExpChangeEvent
        -> XyBattleHud 读取本次经验增量
        -> 调用 XyBattleHud拾取视图.yml 的 创建拾取(experience)
        -> 客户端 HUD 显示经验图标、经验名称和数值
```

```text
MythicMobs money 掉落
        -> XyBattleHud 读取本次金币数量
        -> 调用 XyBattleHud拾取视图.yml 的 创建拾取(money)
        -> 客户端 HUD 显示金币图标、金币名称和数值
```

## 配置

配置文件在 `plugins/XyBattleHud/config.yml`，每项都带中文注释。

伤害飘字显示相关：

- `display.renderer`：`dragoncore-headtag` 为无黑影模式，`armorstand` 为旧版原版名字模式；不写时默认 `dragoncore-headtag`。
- `display.headtag-marker`：DragonCore HeadTag 用来识别伤害实体的前缀，默认 `XYBH_DAMAGE:`；修改后需要同步修改 HeadTag YML。
- `display.height-offset`：伤害出现的高度，数值越大越高。
- `display.float-speed`：每 tick 上浮速度，数值越小漂得越慢、越不容易飘太高。

`damage-types` 中每个类型包含：

- `priority`：多个属性同时成立时，较大值优先。
- `triggers`：匹配 AttributePlus 本次实际触发的属性 ID/名称，也兼容攻击消息关键词。例如 AP 暴击触发 ID 为 `crit`。
- `attributes`：攻击者该 AP 属性大于 `attribute.threshold` 时命中。适合撕裂伤害、钝击伤害等可持续读取的属性。
- `vanilla-critical`：为 `true` 时可匹配原版下落暴击。
- `digits`：必须正好十项，依次对应 `0` 到 `9`。
- `symbols`：可选的符号替换，例如小数点。

撕裂类型示例：

```yml
damage-types:
  tearing:
    priority: 80
    triggers: ['撕裂']
    attributes: ['撕裂伤害']
    color: '&f'
    prefix: ''
    suffix: ''
    digits: ['字形0', '字形1', '字形2', '字形3', '字形4', '字形5', '字形6', '字形7', '字形8', '字形9']
    symbols: {}
```

把 `字形0` 到 `字形9` 替换成实际配置在 DragonCore 字体文件内的单个字符即可。若 AP 的“伤害属性”只是常驻加成，属性回退会把整次命中归类为该类型；需要精确的本次触发识别时，应把 AP 属性的触发 ID 写入 `triggers`。

### 连击配置

- `enabled`：是否启用连击。
- `timeout-ticks`：两次命中允许间隔的时间，默认 40 tick，即 2 秒。
- `display-from`：从第几击开始显示，默认第 2 击。
- `max-count`：可调低，但无论配置如何都不会超过 `999`。
- `hud-name`：DragonCore GUI/HUD 文件名，不写 `.yml`。
- `update-function`：HUD 中刷新连击数量的函数名，默认 `更新连击`。
- `clear-function`：HUD 中隐藏连击的函数名，默认 `清除连击`。

每次命中只刷新攻击者自己的固定 HUD，不再向附近玩家发送世界贴图。切换目标、超时或 `/xybh clear` 时会调用 `清除连击` 隐藏 HUD；所有连击共用一个轻量清理任务。

DragonCore 视觉文件在 [dragoncore/XyBattleHud连击视图.yml](dragoncore/XyBattleHud连击视图.yml)。需要调整位置时，优先修改文件里 `Functions -> 取起点X/取起点Y`；需要换数字或连击样式图时修改 `图片` 段。

### 拾取配置

- `enabled`：是否启用右下角拾取提示。
- `soul-space-enabled`：是否显示 XySoulSpace 自动拾取入库提示；关闭后不会显示灵魂空间入库提示。
- `soul-space-frame-enabled`：是否保留灵魂空间来源标记；新版统一背景暂时不产生视觉差异，用于兼容旧 HUD。
- `hud-name`：DragonCore GUI/HUD 文件名，不写 `.yml`。
- `function-name`：HUD 中创建拾取框的函数名，默认 `创建拾取`。
- `cache-prefix`：DragonCore 临时物品缓存前缀，一般不改。
- `position.right`：拾取框距离屏幕右边多少像素；数值越大越往左。
- `position.bottom`：拾取框距离屏幕底部多少像素；数值越大越往上。
- `animation.duration-millis`：单条拾取提示总显示时间。
- `animation.fade-in-millis`：新提示淡入时间，默认 `150` 毫秒；旧配置的 `0` 自动按 `150` 处理，填 `-1` 完全关闭。
- `animation.fade-out-millis`：提示消失前淡出时间，填 `0` 关闭淡出。
- `animation.max-entries`：屏幕最多保留几条提示；新的在最下面，旧的向上叠。
- `animation.stack-spacing`：每层之间的上下间距，单位像素；当前 `90 x 17` 布局默认 `30`，旧默认值 `43` 会自动迁移为 `30`。
- `animation.slide-pixels`：新提示从右侧滑入的距离，填 `0` 关闭滑入。
- `animation.slide-speed`：新提示滑入速度，`1.0` 表示立即到位。
- `animation.stack-move-speed`：旧提示被顶到上一层时的移动速度，稳定队列模式暂不使用。
- `experience.enabled`：是否显示经验拾取提示。
- `experience.akari-level-enabled`：是否接入 AkariLevel 经验事件。
- `experience.provider-plugin`：提供经验事件的插件名，默认 `AkariLevel`。
- `experience.event-class`：经验事件类名，一般不改。
- `experience.player-variable`：事件里的玩家变量名，默认 `member`。
- `experience.amount-variable`：事件里的经验数量变量名，默认 `expAmount`。
- `experience.level-group-variable`：事件里的等级组变量名，默认 `levelGroup`。
- `experience.source-variable`：事件里的来源变量名，默认 `source`。
- `experience.level-group`：只接收某个等级组的经验，留空表示全部。
- `experience.sources`：只接收某些来源字符串，留空表示全部。
- `experience.display-name`：经验提示里显示的名字。
- `experience.icon`：经验提示图标路径。
- `experience.dedupe-millis`：短时间内同玩家同数值经验只显示一次，默认 `250`；填 `0` 关闭。
- `money.enabled`：是否显示 MythicMobs money 掉落提示。
- `money.mythicmobs-enabled`：是否接入 MythicMobs 掉落事件。
- `money.provider-plugin`：MythicMobs 插件名，默认 `MythicMobs`。
- `money.display-name`：金币提示里显示的名字。
- `money.icon`：金币提示图标路径。
- `money.dedupe-millis`：同一只怪同一笔 money 掉落短时间内只显示一次，默认 `250`；填 `0` 关闭。

普通地面拾取使用 1.12.2 的 `PlayerPickupItemEvent`，本次数量按 `掉落堆数量 - event.getRemaining()` 计算。XySoulSpace个人开关开启时，MM掉落直入灵魂空间并通过 `XySoulSpaceItemDepositEvent` 显示灵魂空间来源提示；个人开关关闭时，实际进入背包的数量通过 `XySoulSpaceItemInventoryDeliveryEvent` 显示普通来源提示。两种来源在新版 HUD 中共用统一背景。背包溢出后落地的余量以后被真实捡起时仍走Bukkit事件，不会把未获得数量提前显示。

DragonCore 视觉文件在 [dragoncore/XyBattleHud拾取视图.yml](dragoncore/XyBattleHud拾取视图.yml)。需要调整整体位置时，改 `config.yml -> pickup.position.right/bottom`；统一背景、经验/金币图标和备用品质框路径在视图文件中修改。拾取队列会把新提示放在最下面，旧提示逐层上移，并按各自的创建时间独立淡出。

拾取背景大小在 DragonCore 文件顶部 `布局.拾取框宽度/拾取框高度` 修改。品质框、物品、经验、金币和文字也都以背景左上角为锚点，但大小和坐标已经拆成独立字段，改背景不会再自动拉伸内容。

框内内容大小、坐标在 DragonCore 视图的 `布局` 段分开调：

- `物品X/Y/宽度/高度`：只影响真实物品图标。
- `经验图标X/Y/宽度/高度`：只影响经验提示图标。
- `金币图标X/Y/宽度/高度`：只影响金币提示图标。
- `品质框X/Y/宽度/高度`：只影响物品品质框。
- `文字X/Y/文字大小`：影响名称和数量文本。

### 品质框

当前推荐由 DragonCore 的 `ItemEffect.yml` 绘制品质框。服主当前 `ItemEffect.yml` 已写明前七项会同时作用于 HUD、背包和 ItemTip，所以 `XyBattleHud拾取视图.yml` 默认不再额外画品质框。

如果不用 ItemEffect 给 HUD 物品槽套框，可以把 `XyBattleHud拾取视图.yml` 里的 `图片.品质框启用` 改成 `true`，此时备用品质判断与新版 `ItemEffect.yml` 前七项一致：

- `品质判断词0-6`：在物品名称或 Lore 中查找白描、传神、浮世、极意、气象、青痕、石墨。
- `品质框0-6`：匹配后叠加在左侧物品格的 GIF 路径。
- `品质框启用: false`：默认值。关闭 XyBattleHud 自己的备用品质框，避免和 ItemEffect 同时绘制。
- 未匹配任何品质词时，品质路径保持为空，不创建品质框，不使用默认白框。
- 普通背包和灵魂空间使用相同品质框，不再维护两套整张拾取背景。

示例：

```yml
图片:
  拾取框: 战斗视图/拾取视图/拾取框.png
  品质框启用: false
  品质判断词0: 白描
  品质框0: icon/品质框/白描.gif
```

背景和品质图片需要由客户端资源包提供；服务端不会读取客户端图片，也不会解析物品插件的私有 NBT。

## 命令

所有命令需要 `xybattlehud.admin`，默认为 OP。

- `/xybh reload`：重新读取配置并重新连接可选依赖。
- `/xybh info`：查看版本、伤害渲染模式、属性来源、AP 事件桥、DragonCore 连击 HUD、DragonCore 拾取接口、AkariLevel 经验桥、XySoulSpace 拾取桥和当前显示数量。
- `/xybh clear`：清除当前全部飘字。
- `/xybh debug [on|off]`：临时输出每次伤害的数值、类型和 AP 消息。重载后恢复配置值。

## 与 XyCore 的关系

XyCore 是软依赖，不能阻止 XyBattleHud 启动。它提供 `XyCore.get().getAttributes()` 的稳定属性读取 API；存在且可用时优先使用，避免本插件直接绑定 AttributePlus 内部实现。XyCore 当前 API 只读取属性值，不携带单次攻击的元素/触发上下文，因此 v1 仍会监听 AttributePlus 事件消息来判断某一击的暴击、撕裂等触发类型。

`/xybh` 属于管理/诊断命令，help、reload、info、clear、debug 和权限不足都使用本插件 `config.yml -> messages.prefix`，方便管理员定位来源。当前 XyBattleHud 没有需要展示给玩家的系统玩法聊天结果；伤害数字、连击 HUD、ActionBar 和 Title 类战斗显示不追加聊天前缀。

## 已知范围

- 当前版本不包含怪物血条、原版伤害粒子压缩、玩家隐藏设置、权限字体组、ProtocolLib 虚拟实体或其他属性插件适配。
- `armorstand` 模式下伤害数字由原生 ArmorStand 名字显示，会保留原版文字黑影。
- `dragoncore-headtag` 模式下必须安装 DragonCore 并放入 `XyBattleHud伤害飘字.yml`，否则实体原版名字被隐藏后客户端看不到伤害数字。
- 没有配置的小数点、千位分隔符会以原版字体显示，因此默认禁用小数和分组符号。
- DragonCore 未安装或未成功启用时，请把 `display.renderer` 改为 `armorstand`；连击 HUD、拾取 HUD 和经验/金币 HUD 不会显示。
- XySoulSpace 未安装时不影响普通拾取提示；只是不显示灵魂仓库自动拾取入库提示。
- AkariLevel 未安装或未成功启用时，不影响普通拾取和连击，只是不显示经验拾取提示。
- MythicMobs 或 Vault 未安装时不影响普通拾取、经验和连击，只是不显示 MythicMobs money 金币提示。

## 构建

Windows：

```powershell
.\gradlew.bat clean test jar
```

产物：`build/libs/XyBattleHud-1.3.14.jar`。

## 许可证

暂未声明许可证。发布或分发前请由仓库维护者补充明确许可证。
