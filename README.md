# Greg Food Expansion

基于 Minecraft 1.20.1 Forge 的食品方向扩展附属模组，规划围绕 GregTech CEu Modern 展开玩法设计。

> **⚠ 立项阶段（骨架）**：项目刚完成初始化，当前仓库只包含可编译的工程骨架（构建脚本、模组入口与开发环境），尚无任何可游玩内容，也尚未接入 GregTech CEu Modern 依赖。玩法方向、机器清单、数值与资源 ID 均未定案，设计文档将随内容立项逐步补入 `docs/design/`。
>
> **⚠ 本项目大量使用 AI 辅助开发**：代码、设计文档、语言条目与部分贴图资源均在 AI 协助下完成，全部产出经人工审核后合入。AI 生成的数值、结构规则与接口约定一律以设计文档中标注为"已定案"的章节为准，未标注的内容不代表最终设计。欢迎审阅源码与反馈问题。

## 项目定位

围绕 GregTech CEu Modern 体系的**食品工业**方向扩展，覆盖从原料处理到深加工食品的产业链玩法。首批机器清单与机制仍在讨论中，未定案前不写入本节。

姊妹项目：

- [Greg Steam Expansion](https://github.com/MinazukiGear/gregsteamexpansion)（蒸汽时代扩展）
- [Greg ULV Expansion](https://github.com/MinazukiGear/gregulvexpansion)（超低压时代扩展）

本项目与其同源同构，工程骨架、构建脚本与开发流程大量沿用上述项目。

## 开发环境

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.20 |
| Java | 17 |
| Gradle | 8.1.1（项目 Wrapper） |

## 开始开发

```powershell
.\gradlew.bat genIntellijRuns   # 生成 IDEA 运行配置（JDK 17）
.\gradlew.bat runClient         # 启动开发客户端
.\gradlew.bat build -x test     # 构建发布 JAR（build/libs/）
.\gradlew.bat runData           # 重新生成数据（资源/配方/语言）
```

若系统默认 Java 不是 17，先设置 `$env:JAVA_HOME` 指向 JDK 17。

## 项目信息

- Mod ID：`gregfoodexpansion`
- 入口类：`net.mgear.gregfoodexpansion.GregFoodExpansion`
- 当前版本：`0.1-Alpha-1.20.1`（未发布）
- 许可：All Rights Reserved

## 致谢

工程骨架与开发流程沿用姊妹项目 [Greg Steam Expansion](https://github.com/MinazukiGear/gregsteamexpansion) 与 [Greg ULV Expansion](https://github.com/MinazukiGear/gregulvexpansion)。其余设计参考将随内容定案补充。
