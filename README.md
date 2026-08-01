# XyBattleHud

`XyBattleHud` 是面向 Paper/Spigot 1.12.2 的轻量战斗视图插件。伤害数字通过罕见 Unicode 字符映射为客户端图片，连击数通过 DragonCore WorldTexture 显示。

当前版本专注于伤害类型字形和连击显示，不包含怪物血条等无关功能。

## 特性

- 监听玩家近战与玩家投射物造成的有效伤害。
- 使用原生 `ArmorStand` 显示、上浮并自动清理伤害数字，兼容 1.12.2。
- 伤害类型完全由 `config.yml -> damage-types` 配置，不包含不同权限组字体功能。
- 默认支持普通伤害与暴击伤害两套字形。
- 同一玩家连续攻击同一目标时显示连击数，切换目标或超时后重置。
- 普攻连击使用 `连击数_1.png`，暴击连击使用 `连击数_2.png`，数字排列在样式图片前面。
- 连击位置、数字大小、样式图片大小可配置，计数最大为 `999`。
- 连击采用 DragonCore WorldTexture，不使用 ArmorStand 名称，因此没有字体图片黑底。
- 可用 AttributePlus 的本次属性触发事件、攻击消息、攻击者属性和原版下落暴击判断识别类型。
- 可选接入 XyCore 的 `AttributeService`；未安装 XyCore 时会直接读取 AttributePlus API，二者都不存在时仍可显示普通伤害与原版暴击。
- 支持第三方通过实体 metadata `xybattlehud.damage-type` 指定已配置类型。

## 环境

- Java 8 或更高版本。
- Paper/Spigot 1.12.2。
- 可选：AttributePlus，默认属性来源。
- 可选：XyCore。仅作为稳定的 AttributePlus 读取桥，不是硬依赖。
- DragonCore：伤害字体映射本身不要求服务端 API；启用连击图片显示时必须安装。
- 客户端：DragonCore 字体配置、伤害数字 PNG 和连击 PNG。

## 安装

1. 将 `XyBattleHud-1.1.2.jar` 放入服务端 `plugins` 目录并重启。
2. 将 DragonCore 安装到服务端和玩家客户端。DragonCore `2.6.2.9` 在 1.12.2 服务端建议使用 Java 8。
3. 将 DragonCore 字体定义和 PNG 放入客户端实际加载的资源目录。
4. 确认 [config.yml](src/main/resources/config.yml) 中 `digits` 字符和 `combo.images` 图片路径与客户端资源一致。
5. 使用 `/xybh info` 检查属性来源、AttributePlus 事件和龙核连击状态。

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
        -> 服务端 ArmorStand CustomName
        -> 客户端字体系统把字符绘制为 PNG
```

服务端不知道 PNG 的路径。DragonCore 在客户端已经注册了字符到图片的映射后，普通聊天、实体名和全息字里出现相同字符都会使用该图片字形。这就是插件无需 DragonCore API 也能显示图片数字的原因。

连击与伤害数字不同。连击需要控制多个独立图片的位置和大小，因此服务端通过 DragonCore `CoreAPI.setPlayerWorldTexture` 发送数字 PNG 与样式 PNG 的路径。它不会读取客户端文件；客户端收到路径后自行加载对应图片。

## 配置

配置文件在 `plugins/XyBattleHud/config.yml`，每项都带中文注释。

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
- `position`：连击整体相对目标的位置。
- `size`：数字和样式图片的宽高，单位为方块。
- `images`：数字图片文件夹以及 `连击数_1.png`、`连击数_2.png` 的客户端路径。

每次命中只保留攻击者最新的一组连击贴图。插件只向同世界 32 格内玩家发送，最多发送三位数字和一张样式图；所有连击共用一个清理任务。

## 命令

所有命令需要 `xybattlehud.admin`，默认为 OP。

- `/xybh reload`：重新读取配置并重新连接可选依赖。
- `/xybh info`：查看版本、属性来源、AP 事件桥、DragonCore 连击接口和当前显示数量。
- `/xybh clear`：清除当前全部飘字。
- `/xybh debug [on|off]`：临时输出每次伤害的数值、类型和 AP 消息。重载后恢复配置值。

## 与 XyCore 的关系

XyCore 是软依赖，不能阻止 XyBattleHud 启动。它提供 `XyCore.get().getAttributes()` 的稳定属性读取 API；存在且可用时优先使用，避免本插件直接绑定 AttributePlus 内部实现。XyCore 当前 API 只读取属性值，不携带单次攻击的元素/触发上下文，因此 v1 仍会监听 AttributePlus 事件消息来判断某一击的暴击、撕裂等触发类型。

`/xybh` 属于管理/诊断命令，help、reload、info、clear、debug 和权限不足都使用本插件 `config.yml -> messages.prefix`，方便管理员定位来源。当前 XyBattleHud 没有需要展示给玩家的系统玩法聊天结果；伤害数字、连击图片、ActionBar 和 Title 类战斗显示不追加聊天前缀。

## 已知范围

- 当前版本不包含怪物血条、原版伤害粒子压缩、玩家隐藏设置、权限字体组、ProtocolLib 虚拟实体或其他属性插件适配。
- 伤害数字仍由原生 ArmorStand 显示，其可见距离由服务端实体追踪范围控制。
- 没有配置的小数点、千位分隔符会以原版字体显示，因此默认禁用小数和分组符号。
- DragonCore 未安装或未成功启用时，伤害飘字仍可工作，但连击图片不会显示。

## 构建

Windows：

```powershell
.\gradlew.bat clean test jar
```

产物：`build/libs/XyBattleHud-1.1.1.jar`。

## 许可证

暂未声明许可证。发布或分发前请由仓库维护者补充明确许可证。
