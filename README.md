# Greg Food Expansion

基于 Minecraft 1.20.1 Forge 的农业与食品工业化方向扩展附属模组，围绕 GregTech CEu Modern 展开玩法设计。

> **⚠ 开发中（M1 里程碑主体已实现）**：农业与食品加工的首批内容已可游玩——首批 13 种作物与野生作物、食材形态层（切片/切丝/切块/绞碎/研磨/压延）、切配机与通用烹饪机（全 9 电压档）、隧道式烤炉（首个多方块）、烘焙与烧烤体系、精制档菜肴 70+ 道；大豆链、罐头线与增益联动等后续批次开发中。
>
> **⚠ 本项目大量使用 AI 辅助开发**：代码、设计文档、语言条目与部分贴图资源均在 AI 协助下完成，全部产出经人工审核后合入。AI 生成的数值、结构规则与接口约定一律以设计文档中标注为"已定案"的章节为准，未标注的内容不代表最终设计。欢迎审阅源码与反馈问题。

## 当前内容（M1）

- **作物**：首批 13 种（大豆/玉米/水稻/大麦/花生/番茄/洋葱/辣椒/白菜/葡萄/咖啡/茶/啤酒花），含环境参数生长模型与野生作物生成；
- **手工层**：切配工具（菜刀/削皮刀/研钵/擀面杖）与烹饪工具（厨刀/炒锅/蒸笼），工作台即可产出基础档食物；
- **机器**：切配机与通用烹饪机（全 9 电压档，编程电路切模式）、隧道式烤炉（多方块，三档炉温/段数并行）；
- **食材形态**：40+ 种（肉/鱼/蛋/蔬果/米面制品，生熟分品）；30+ 种烹饪菜肴（煮/蒸/炒/炸/烤/烧烤口味串）；
- **回收系统**：废食用油精炼产线（蒸馏室×2 + 化学反应釜，有损回收）。

## 项目定位

**从田间到餐桌的农业与食品工业化**。作物是食品工业的"矿石"——正如 GTCEu 为化工做了原料大拓展，本模组为农业与食品做同等力度且持续增长的拓展：原料层（作物/畜牧/天然食材）→ 半成品层（面粉/酱油/淀粉等工业化初加工）→ 终端层（菜肴/零食/饮品/罐头/功能食品），所有原料最终汇入工业化的加工链，在 GT 进度中承担明确功能（探索补给、增益供给、终局解决方案）。

方向级决议与 M1 范围已定案，总纲见 [`content-direction-brainstorm.md`](docs/design/content-direction-brainstorm.md)。M1 首个闭环：首批 13 种作物、切配机与手工工具组、通用烹饪机、隧道式烤炉与烘焙工业化、大豆链（豆腐/酱油）、泡菜与酒（复用 GTCEu 发酵槽）、淀粉糖浆链、手工罐头、饱足增益与生活调味料（胡萝卜版）联动框架；数值细节以各设计文档标注"已定案"的章节为准。

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
| GregTech CEu Modern | 7.5.3（必需前置） |
| Gradle | 8.1.1（项目 Wrapper） |

EMI、Jade、JECh（拼音搜索）、精妙背包/存储、Modern UI、GTM Things（连同其必需的 AE2 和 AE2 的前置 GuideME）仅作为开发客户端测试工具由 Gradle 运行时加载，不是本模组前置，也不会打包进发布 JAR。

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

## 许可证

本模组采用双许可：

- **代码**：[LGPL-3.0](LICENSE)（GNU Lesser General Public License v3.0），见 `LICENSE`
- **素材**（贴图、模型、音效、语言文件等非代码资源）：[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/)，全文见 `LICENSE.assets`

## 致谢

工程骨架与开发流程沿用姊妹项目 [Greg Steam Expansion](https://github.com/MinazukiGear/gregsteamexpansion) 与 [Greg ULV Expansion](https://github.com/MinazukiGear/gregulvexpansion)。其余设计参考将随内容定案补充。

## 已知上游问题

开发客户端同时加载 GTCEu 内嵌 LDLib 与 EMI 时可能遇到 Mixin 初始化竞态（`MixinTargetAlreadyLoadedException` / `EmiPlugin was loaded too early`），参见 [GregTechCEu/GregTech#2917](https://github.com/GregTechCEu/GregTech/issues/2917)；不影响未安装 EMI 的环境。
