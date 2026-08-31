# AI 使用记录

## 1.3.9

本次修改由 AI 根据服主反馈“本机类似写法的字体漂浮似乎没有黑影，希望去除伤害字体黑影”辅助完成。

已确认的实现边界：

- 黑影来自原版 `ArmorStand` 实体名字渲染，不是 DragonCore 字体图片本身。
- 新增 `display.renderer: dragoncore-headtag`，服务端仍用临时 ArmorStand 负责位置、上浮和清理，但隐藏原版名字。
- 服务端把实体名写成 `XYBH_DAMAGE:` 加伤害字符，DragonCore HeadTag 负责匹配并用 `label shadow: false` 显示真正伤害文字。
- 保留 `display.renderer: armorstand` 作为旧模式；没有安装 DragonCore 或没有放 HeadTag 文件时可切回旧模式。
- 这版需要同时替换服务端 jar，并把 `dragoncore/HeadTag/XyBattleHud伤害飘字.yml` 放入 `plugins/DragonCore/HeadTag/`。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.8

本次修改由 AI 根据服主截图反馈“带品质检测的灵魂框和人物框似乎搞反了”辅助完成。

已确认的实现边界：

- 普通背包品质框现在使用 `品质普通拾取框N`。
- 灵魂空间品质框现在使用 `品质灵魂拾取框N`。
- 基础白描默认框保持原来的反向兼容，因为当前资源包里 `白描拾取框.png` 是箱子，`白描灵魂拾取框.png` 是头像。
- 本次只改 DragonCore 拾取视图和说明，不改服务端拾取事件监听逻辑。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.7

本次修改由 AI 根据服主提供的微调版 `config.yml` 和 `XyBattleHud拾取视图.yml` 辅助完成。

已确认的实现边界：

- 金币提示不再和经验提示共用 `拾取经验图标`。
- `XyBattleHud拾取视图.yml` 中 `拾取金币图标` 只影响金币图标大小和坐标。
- `拾取经验图标` 仍只影响经验图标。
- 默认配置采用服主当前测试值 `pickup.position.right: 50`。
- 金币图标初始坐标和大小沿用服主微调过的 `13 x 12`、`+4/+4`，后续可以单独改。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.6

本次修改由 AI 根据服主说明“金币使用 Vault，并在 MythicMobs 中配置 `money 10 1`”辅助完成。

已确认的实现边界：

- 金币提示只监听 MythicMobs 的 money 掉落事件，不监听 Vault 余额变化。
- 这样击杀怪物掉落金币会显示拾取框，商店消费、后台转账、管理员指令发钱不会误触发。
- `pickup.money.display-name` 和 `pickup.money.icon` 可在 `config.yml` 修改。
- 客户端需要准备 `战斗视图/属性图标/金币图标.png`，或把配置改成已有图标路径。
- 金币与经验一样走 DragonCore 文本模式，不占用物品缓存。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.5

本次修改由 AI 根据服主截图反馈“实际只有 10 的经验，但重复显示了两次；拾取提示位置能不能在配置里调”辅助完成。

已确认的实现边界：

- 不改拾取队列层叠逻辑，继续使用 v1.3.4 对齐 Yee 的稳定 `index` 方案。
- 经验去重只作用于 AkariLevel 经验提示，默认 `250ms` 内同玩家同数值同显示配置只显示一次。
- `pickup.experience.dedupe-millis: 0` 可关闭合并；如果现场仍重复，可以略微调大。
- 拾取 HUD 的右边距和底边距改为 `config.yml -> pickup.position.right/bottom` 控制。
- 这版需要同时替换服务端 jar 和 `plugins/DragonCore/Gui/XyBattleHud拾取视图.yml`。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.4

本次修改由 AI 根据服主反馈“这一版问题更大，开始显示若隐若现；先回头看看 Yee 的拾取视图怎么写”辅助完成。

已确认的实现边界：

- 参考 `YeeCombatView拾取视图.yml` 的稳定队列方式：用组件 `index` 控制层级，位置直接按 `index * stack-spacing` 计算。
- 不再使用 v1.3.3 的创建序号反推目标层函数。
- 默认关闭开场淡入，只保留结尾淡出；如果服务器旧配置里 `fade-in-millis` 还是 `150`，建议手动改成 `0`。
- 新组件创建时先设为 `-1`，创建完成后统一上移，最新条目最终落在第 0 层。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.3

本次修改由 AI 根据服主反馈“拾取提示叠在一起了”的截图辅助完成。

已确认的实现边界：

- 不改变拾取框图片和颜色。
- DragonCore 拾取视图改用创建序号计算每条提示所在层，避免旧组件层数没有正确增加时重叠。
- 默认层间距改为 `34`，如果服主客户端 GUI 缩放或拾取框尺寸更大，可以继续在 `config.yml -> pickup.animation.stack-spacing` 调大。
- 现有服务器配置不会被 jar 自动覆盖，需要手动把 `stack-spacing` 从 `25` 改成 `34` 或更大。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.2

本次修改由 AI 根据服主截图反馈的“现在只会有一个拾取框，新拾取会顶替旧拾取框；正确效果应是拾取两个物品就显示两个拾取框，旧的在上面”的问题辅助完成。

已确认的实现边界：

- 不修改拾取框颜色、图片和视觉排版。
- 修复点在服务端 DragonCore 调用顺序：不再每次拾取都重新打开 HUD。
- 每名玩家首次出现拾取提示时打开 `XyBattleHud拾取视图`，后续拾取只执行 `创建拾取`，让 DragonCore HUD 内已有组件继续存在并上移。
- 玩家退出和 `/xybh reload` 会清理打开记录。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.1

本次修改由 AI 根据服主提出的“拾取提示应层层叠加，最新的在下面，旧的慢慢上移并各自淡出；淡入淡出时间要可配置；经验只能用人物头颅拾取框”的需求辅助完成。

已确认的实现边界：

- 拾取队列动画在 DragonCore HUD 内完成，XyBattleHud 服务端只传入动画配置参数。
- 新提示固定创建在第 0 层，也就是右下角最下面；已有提示的层数加 1，并按 `stack-move-speed` 平滑上移。
- 每条提示保存自己的出生时间和消失时间，因此物品、灵魂空间、经验提示都会按各自生命周期独立淡入淡出。
- `pickup.animation` 可调整总时长、淡入、淡出、最大条数、层间距、滑入距离、滑入速度和上移速度。
- 经验提示固定使用头颅拾取框，不进入品质框匹配，也不使用灵魂空间箱子框。
- 仍保持 DragonCore、XySoulSpace、AkariLevel 软依赖，不新增硬依赖。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.3.0

本次修改由 AI 根据服主提出的“给拾取视图引入经验拾取，并且经验值、显示名、图标、来源筛选都要能改；同时修正普通背包与灵魂空间拾取框显示反了”的需求辅助完成。

已确认的实现边界：

- 经验提示不是去读 DragonCore 字体，而是由服务端拿到本次经验增量后再交给 DragonCore HUD 显示。
- AkariLevel 只作为软依赖接入；没有安装时，经验提示自动跳过，不会影响伤害、连击和普通拾取。
- `pickup.experience.display-name`、`pickup.experience.icon`、`pickup.experience.level-group`、`pickup.experience.sources` 都可以在 `config.yml` 里改。
- 事件类名和变量名也在配置里：默认读取 AkariLevel 事件的 `member`、`expAmount`、`levelGroup`、`source`。
- DragonCore 拾取视图新增 `experience` 模式，经验提示不走物品槽缓存，只显示图标和文字。
- 按服主当前客户端资源包的实际视觉，普通背包和灵魂空间拾取框的来源映射已对调。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.2.4

本次修改由 AI 根据服主放入 `V0.0.3` DragonCore 资源包的品质拾取框图片辅助完成。

已确认的配置：

- 普通背包拾取使用 `品质普通拾取框N`，灵魂空间自动入库使用 `品质灵魂拾取框N`。
- 已配置品质：待锻造/白描、萌黄、气象、极意、归元、传神、浮世。
- 待锻造没有单独图片，按原 `通用.yml` 规则复用白描框。
- 没有品质标识的物品，以及群青、无相、鸿蒙图片未提供的品质，都会回退默认白描框或白描灵魂框。
- `pickup.soul-space-frame-enabled: false` 时服务端会按普通来源发送灵魂空间入库，因此同品质会显示普通框。
- 品质判断仍只在 DragonCore 创建该条拾取提示时执行一次，不在每帧渲染时重复检查。

客户端资源：

- 14 张品质框已位于 `DragonCore/战斗视图/拾取视图/`。
- 默认框直接使用 `白描拾取框.png` 和 `白描灵魂拾取框.png`，不再依赖旧版基础框。

验证记录：

- 已执行 `gradlew.bat clean test jar`，源码编译、现有单元测试和 `XyBattleHud-1.2.4.jar` 构建通过。

## 1.2.3

本次修改由 AI 根据服主提供的 DragonCore `通用.yml`、`默认.yml` 和“按拾取物品品质显示不同拾取框”的需求辅助完成。

已确认的实现方式：

- `通用.yml` 的 `品质判断词0-10` 是对物品名称/Lore 的关键词匹配，不是服务端独立的品质 API。
- XyBattleHud 已经把完整 `ItemStack` 放入 DragonCore 客户端缓存，因此拾取 HUD 可以直接复用 `方法.是否包含(物品, 品质关键词)`。
- 服务端不增加品质扫描、NBT 解析或物品插件依赖，拾取高频路径保持轻量。
- `dragoncore/XyBattleHud拾取视图.yml` 中品质框匹配从 `10` 到 `0`，数字越大优先级越高。
- 品质图片路径留空时回退到当前来源框：普通背包用普通框，灵魂空间用灵魂框。
- `品质判断词10` 默认留空，因为服主提供的 `SDFASF` 看起来是占位词；需要鸿蒙/传承品质时再填写真实关键词。

配置步骤：

1. 将品质拾取框 PNG 放入客户端 `DragonCore/战斗视图/拾取视图/品质/`。
2. 在 `XyBattleHud拾取视图.yml` 的 `图片` 段填写 `品质拾取框0-10` 路径。
3. 如果某个品质还没有图片，保持该路径为空即可，不会导致原有拾取框消失。

验证记录：

- 已执行 `gradlew.bat clean test jar`，源码编译、现有单元测试和 `XyBattleHud-1.2.3.jar` 构建通过。

## 1.2.2

本次修改由 AI 根据服主提出的“背包拾取与灵魂空间入库使用不同拾取框，灵魂空间框带独立开关”需求辅助完成。

已确认的实现边界：

- XySoulSpace 成功入库后会发出 `XySoulSpaceItemDepositEvent`；XyBattleHud 只处理其 `source=pickup` 的自动入库，不会在手动存入、管理员发放或其他插件 API 存入时误弹拾取框。
- 普通背包拾取传递 `normal`，自动进入灵魂空间传递 `soul`；两者使用同一个 DragonCore HUD、同一个物品缓存和同一套数量计算。
- `pickup.soul-space-enabled: false` 会完全关闭灵魂空间自动入库提示。
- `pickup.soul-space-frame-enabled: false` 不关闭提示，只让灵魂空间入库使用普通拾取框。
- DragonCore 文件的 `图片.普通拾取框` 与 `图片.灵魂空间拾取框` 分别指向两张 PNG；默认灵魂空间图片路径为 `战斗视图/拾取视图/灵魂拾取框.png`。

验证记录：

- 已执行 `gradlew.bat clean test jar`，源码编译、现有单元测试和 `XyBattleHud-1.2.2.jar` 构建均通过。

## 1.2.1

本次修改由 AI 根据服主截图中的 DragonCore 报错，以及“连击数应固定在屏幕位置”的反馈辅助完成。

已确认的实现边界：

- 问题不在 DragonCore 拾取 HUD 文件本身，而在服务端发给 `sendRunFunction` 的脚本字符串。
- 之前发送的是 `创建拾取('uuid','数量');`，DragonCore 会把它当成非法方法名。
- 现在改成 `方法.执行方法('创建拾取','uuid','数量');`，让 HUD 内部函数由龙核脚本正常调度。
- 连击显示不再使用 DragonCore WorldTexture，也不再根据受击目标坐标计算位置。
- XyBattleHud 只负责统计连击、判断是否暴击，并调用 `XyBattleHud连击视图.yml` 的 `更新连击/清除连击`。
- 连击数字图片路径、`连击数_1/2.png`、位置和大小都在 DragonCore 连击视图 YML 中配置。
- 切换目标或连击重新回到第 1 击时会清除旧 HUD，避免旧连击数残留。
- 本次不改拾取框布局、物品缓存、XySoulSpace 联动或普通拾取数量计算。

验证记录：

- 已执行 `gradlew.bat clean test jar`，源码编译、现有单元测试和 `XyBattleHud-1.2.1.jar` 构建均通过。

## 1.2.0

本次修改由 AI 根据服主提出的“拾取后右下角显示拾取框、真实物品图标、名称和数量，并确认是否需要联动 XySoulSpace”的需求辅助完成。

已确认的实现边界：

- XyBattleHud 不读取客户端 PNG，也不读取 DragonCore 字体 YML；拾取视图通过 DragonCore HUD 的 `slot` 组件渲染真实 `ItemStack` 图标。
- 服务端只做两件事：把本次拾取物品写入 DragonCore 客户端临时缓存，并调用 `XyBattleHud拾取视图.yml` 中的 `创建拾取` 函数。
- 普通拾取使用 Bukkit 1.12.2 的 `PlayerPickupItemEvent`。该事件在新版 API 中被标记过时，但对目标环境 `Paper/Spigot 1.12.2` 是正确入口。
- 本次数量按 `掉落堆数量 - event.getRemaining()` 计算；背包只捡走一部分时不会误显示整组数量。
- XySoulSpace 自动拾取会取消普通拾取事件，或通过范围扫描直接移除掉落物；因此本版本软监听 `XySoulSpaceItemDepositEvent`，只展示 `source=pickup` 的入库。
- DragonCore、XySoulSpace 都保持软依赖。未安装 DragonCore 时伤害飘字仍工作；未安装 XySoulSpace 时普通拾取提示仍工作。
- DragonCore HUD 配置只保留背景、物品槽和名称数量三类组件；不加入品质、来源、权限字体组、复杂动画或每玩家任务。
- 拾取 HUD 示例文件位于 `dragoncore/XyBattleHud拾取视图.yml`，默认引用客户端 `战斗视图/拾取视图/拾取框.png`。
- `/xybh info` 增加龙核拾取与灵魂仓库拾取桥状态，便于服主现场排查。

验证记录：

- 已执行 `gradlew.bat clean test jar`，源码编译、现有伤害/连击测试、新增拾取数量测试和 `XyBattleHud-1.2.0.jar` 构建均通过。

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
## 1.1.0

本次修改由 AI 根据服主提出的“添加轻量连击数，数字在连击图前，普攻和暴击使用不同连击图片，最大999”的需求辅助完成。

已确认的实现边界：

- 连击只统计同一玩家连续攻击同一目标；切换目标或超过配置时间会重置。
- 连击数最大硬限制为 `999`，不会生成四位数字贴图。
- 普攻使用 `连击数_1.png`，暴击使用 `连击数_2.png`。
- 连击使用 DragonCore WorldTexture，不使用 ArmorStand 名称，因此不会出现字体黑底。
- 不创建每玩家常驻任务；所有连击显示共用单一清理任务。

验证记录：

- 已执行 `gradlew.bat clean test jar`，构建通过。

## 1.0.1

本次修改由 AI 根据服主测试反馈的“AP 暴击仍显示默认字体”问题辅助完成。

- 反编译确认 AttributePlus 暴击触发 ID 为 `crit`。
- 监听 AttributePlus 本次属性触发事件，用 `triggers: ['crit', '暴击']` 判断真正暴击。
- 保留原版跳劈暴击识别。

## 1.0

本次修改由 AI 根据 GTDamage 行为、DragonCore 字体机制和服主简化需求辅助完成。

- 从 GTDamage 重构为 XyBattleHud。
- 只保留属性伤害字形显示核心。
- XyCore、AttributePlus、DragonCore 均为软依赖。
- 默认普通伤害和暴击伤害两套字符映射。
