# XyBattleHud

`XyBattleHud` 是面向 Paper/Spigot 1.12.2 的战斗伤害飘字插件。它将服务端计算出的伤害数值替换为罕见 Unicode 字符；安装了对应 DragonCore 客户端字体配置和 PNG 后，客户端会把这些字符绘制为伤害图片。

本项目 `v1.0` 专注于一件事：按伤害类型使用不同字形显示伤害数字。

## 特性

- 监听玩家近战与玩家投射物造成的有效伤害。
- 使用原生 `ArmorStand` 显示、上浮并自动清理伤害数字，兼容 1.12.2。
- 伤害类型完全由 `config.yml -> damage-types` 配置，不包含不同权限组字体功能。
- 默认支持普通伤害与暴击伤害两套字形。
- 可用 AttributePlus 的本次攻击消息、攻击者属性和原版下落暴击判断识别类型。
- 可选接入 XyCore 的 `AttributeService`；未安装 XyCore 时会直接读取 AttributePlus API，二者都不存在时仍可显示普通伤害与原版暴击。
- 支持第三方通过实体 metadata `xybattlehud.damage-type` 指定已配置类型。

## 环境

- Java 8 或更高版本。
- Paper/Spigot 1.12.2。
- 可选：AttributePlus，默认属性来源。
- 可选：XyCore。仅作为稳定的 AttributePlus 读取桥，不是硬依赖。
- 客户端可选：DragonCore 字体配置与相应 PNG。XyBattleHud 不读取 DragonCore 文件，也不调用 DragonCore API。

## 安装

1. 将 `XyBattleHud-1.0.jar` 放入服务端 `plugins` 目录并重启。
2. 将 DragonCore 字体定义放入客户端实际加载的字体配置中。字形定义位置由 DragonCore/资源包决定，不是本插件目录。
3. 确认 [config.yml](src/main/resources/config.yml) 中每一位 `digits` 都与 DragonCore 字体配置里的字符一致。
4. 使用 `/xybh info` 检查属性来源与 AttributePlus 事件状态。

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

## 配置

配置文件在 `plugins/XyBattleHud/config.yml`，每项都带中文注释。

`damage-types` 中每个类型包含：

- `priority`：多个属性同时成立时，较大值优先。
- `triggers`：匹配 AttributePlus 当前攻击消息的关键词，例如 `暴击`。
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

把 `字形0` 到 `字形9` 替换成实际配置在 DragonCore 字体文件内的单个字符即可。若 AP 的“伤害属性”只是常驻加成，属性回退会把整次命中归类为该类型；需要精确的本次触发识别时，应设置对应 `triggers`，让它匹配 AttributePlus 的本次攻击消息。

## 命令

所有命令需要 `xybattlehud.admin`，默认为 OP。

- `/xybh reload`：重新读取配置并重新连接可选依赖。
- `/xybh info`：查看版本、属性来源、AP 事件桥和当前飘字数量。
- `/xybh clear`：清除当前全部飘字。
- `/xybh debug [on|off]`：临时输出每次伤害的数值、类型和 AP 消息。重载后恢复配置值。

## 与 XyCore 的关系

XyCore 是软依赖，不能阻止 XyBattleHud 启动。它提供 `XyCore.get().getAttributes()` 的稳定属性读取 API；存在且可用时优先使用，避免本插件直接绑定 AttributePlus 内部实现。XyCore 当前 API 只读取属性值，不携带单次攻击的元素/触发上下文，因此 v1 仍会监听 AttributePlus 事件消息来判断某一击的暴击、撕裂等触发类型。

## 已知范围

- v1 不包含怪物血条、原版伤害粒子压缩、玩家隐藏设置、权限字体组、ProtocolLib 虚拟实体或其他属性插件适配。
- 原生 ArmorStand 的可见距离由服务端实体追踪范围控制；v1 不发送自定义数据包。
- 没有配置的小数点、千位分隔符会以原版字体显示，因此默认禁用小数和分组符号。

## 构建

Windows：

```powershell
.\gradlew.bat clean test jar
```

产物：`build/libs/XyBattleHud-1.0.jar`。

## 许可证

暂未声明许可证。发布或分发前请由仓库维护者补充明确许可证。

