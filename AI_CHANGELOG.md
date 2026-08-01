# AI 更新记录

本文件记录 AI 参与的代码变更，供维护者审查和追溯。

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
