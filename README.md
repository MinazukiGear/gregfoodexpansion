# Greg Food Expansion

基于 Minecraft 1.20.1 Forge 的**生物工业主线**附属模组,围绕 GregTech CEu Modern 展开玩法设计。

> **⚠ 设计重置中(代码未开始实装)**:2026-09-09 完成设计 v2.0 全量重写——旧版设计文档、
> 内容代码与贴图已整体废弃删除(git 历史可考古)。新设计概要见
> [`docs/design/overview.md`](docs/design/overview.md),当前仓库为工程骨架 + 完整设计文档集。
>
> **⚠ 本项目大量使用 AI 辅助开发**:设计文档与后续代码、贴图均在 AI 协助下完成,产出经人工审核合入。
> 一切以设计文档标注"方向已定案/已定案"的内容为准,未标注数值均为草案。

## 设计概要(详见 docs/design/)

- **生物工业主线**:作物/畜牧 → 食材处理 → 发酵·酶·菌种 → 化工输出(与 GT 主线必需咬合)+ 食品终端;
- **电压跨度**:ULV 手工起步,LV–ZPM 钥匙制铺排,UV 以元素膳造厂收口(营养闭合);**不强制开启
  GTCEu 高档模式**——默认环境内容至 UV,整合包开启高档(`machines.highTierContent=true`)后自动
  追加注册 UHV 风味/UEV 质构/UIV 缓释配料与 OpV 分子料理编译器、MAX 造粮机;
- **多方块优先 + 单一用途**:全跨度 22 台新增多方块(切配工坊/烹饪工坊/温室/大型磨坊/隧道烤炉/
  畜牧工坊/禽类养殖场/酿窖/分割产线/中央厨房/水产养殖场/育种实验室/大型温室/大型灌装厂/气调保鲜仓/
  中央处理厂/生物反应器/固定化酶厂/HPP 处理舱/元素膳造厂/分子料理编译器/造粮机),每台只承载一类
  工艺用途(时间转化类工艺以"酿窖"共仓模块化),**零新增单方块机器**,其余复用 GTCEu 机型;
- **无新生物**:模组不注册任何新实体;新增动物源(鸭/鹅/鹌鹑/虾/蟹)的产品由养殖场多方块
  **虚空生成**(饲料进→产品出,镜像姊妹项目采矿厂/钻井语法);
- **手工层**:工作台刀具组 + 篝火/熔炉 + 陶罐(前期炊事道具),约 50 道无电手做菜基线;
  陶罐为纯前期道具,电力时代由烹饪工坊/酿窖完全替代;
- **数量定位**:真实植物作物 350~500 种(开放终态)、菜肴 2,000~4,000 道——数量看齐并超越潘马斯农场,
  深度差异化:每样东西有工业归属、品位响应、增益身份与**真实谱系来源**(转录制,不发明菜肴);
- **意义存续**:双轴并行(合成解决"有没有",天然育种解决"好不好")、独家锁定、菜单全龄化——
  任何档位的设计资产终身有效;
- **内容即数据**:三张核心数据表(作物/组合表/菜谱登记)+ 内容 lint 构建期断言 + 模板化贴图管线。

里程碑与批次闸门见 [`docs/design/milestones.md`](docs/design/milestones.md)。

## 姊妹项目

- [Greg Steam Expansion](https://github.com/MinazukiGear/gregsteamexpansion)(蒸汽时代扩展)
- [Greg ULV Expansion](https://github.com/MinazukiGear/gregulvexpansion)(超低压时代扩展)

本项目与其同源同构,工程骨架、构建脚本与开发流程大量沿用上述项目。

## 开发环境

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.4.20 |
| Java | 17 |
| GregTech CEu Modern | 7.5.3(必需前置) |
| Gradle | 8.1.1(项目 Wrapper) |

EMI、Jade、JECh(拼音搜索)、精妙背包/存储、Modern UI、GTM Things(连同其必需的 AE2 和 AE2 的前置 GuideME)仅作为开发客户端测试工具由 Gradle 运行时加载,不是本模组前置,也不会打包进发布 JAR。

## 开始开发

```powershell
.\gradlew.bat genIntellijRuns   # 生成 IDEA 运行配置(JDK 17)
.\gradlew.bat runClient         # 启动开发客户端
.\gradlew.bat build -x test     # 构建发布 JAR(build/libs/)
.\gradlew.bat runData           # 重新生成数据(资源/配方/语言)
```

若系统默认 Java 不是 17,先设置 `$env:JAVA_HOME` 指向 JDK 17。

## 项目信息

- Mod ID:`gregfoodexpansion`
- 入口类:`net.mgear.gregfoodexpansion.GregFoodExpansion`
- 当前版本:`0.1-Alpha-1.20.1`(未发布;设计 v2 重置期)

## 许可证

本模组采用双许可:

- **代码**:[LGPL-3.0](LICENSE)(GNU Lesser General Public License v3.0),见 `LICENSE`
- **素材**(贴图、模型、音效、语言文件等非代码资源):[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/),全文见 `LICENSE.assets`

## 致谢

工程骨架与开发流程沿用姊妹项目 [Greg Steam Expansion](https://github.com/MinazukiGear/gregsteamexpansion) 与 [Greg ULV Expansion](https://github.com/MinazukiGear/gregulvexpansion)。其余设计参考将随内容定案补充。

## 已知上游问题

开发客户端同时加载 GTCEu 内嵌 LDLib 与 EMI 时可能遇到 Mixin 初始化竞态(`MixinTargetAlreadyLoadedException` / `EmiPlugin was loaded too early`),参见 [GregTechCEu/GregTech#2917](https://github.com/GregTechCEu/GregTech/issues/2917);不影响未安装 EMI 的环境。
