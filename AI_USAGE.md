# AI 使用记录

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
