# M0 内容审计报告(GTCEu 查重 + 生活调味料联动)

> **状态:已完成** — 2026-09-07
>
> 审计对象:GregTech CEu Modern 7.5.3(MC 1.20.1 Forge)、Spice of Life: Carrot Edition 1.20.1(模组版本 1.15.1)。
> 效力说明:本报告为**事实查证**(附证据),其中"对本项目的含义"仅为建议;任何设计变更仍以设计文档定案为准。

## 1. 审计方法与证据基准

| 代号 | 来源 |
| --- | --- |
| 【jar】 | 本地 Gradle 缓存官方发布 jar:`~/.gradle/caches/modules-2/files-2.1/com.gregtechceu.gtceu/gtceu-1.20.1/7.5.3/.../gtceu-1.20.1-7.5.3.jar`,内含 `assets/gtceu/lang/{en_us,zh_cn}.json` |
| 【src】 | 反编译源码 jar:`~/.gradle/caches/forge_gradle/deobf_dependencies/.../gtceu-1.20.1-7.5.3_mapped_official_1.20.1-sources.jar`(全部 .java 已全文检索) |
| 【GH】 | [GregTechCEu/GregTech-Modern](https://github.com/GregTechCEu/GregTech-Modern) 默认分支 1.20.1;SoL 胡萝卜版 [Cazsius/Spice-of-Life-Carrot-Edition](https://github.com/Cazsius/Spice-of-Life-Carrot-Edition) 1.20 分支 |
| 【百科】 | MC百科条目交叉验证 |

注意:GTCEu Modern 配方全部由 Java datagen 生成,**jar 内无配方 JSON**,配方方向证据均来自【src】。存在性结论做过 lang(英文键/英文值/中文值)+ 源码双重检索。

## 2. GTCEu Modern 材料查重

### 2.1 查重总表

| 条目 | 结论 | 证据 | 对本项目含义 |
| --- | --- | --- | --- |
| 牛油 tallow | **确认不存在** | 【jar】lang 无 tallow/牛油;【src】材料注册 0 命中(首检命中为 "ntAllowList" 假阳性) | 需新增 |
| 明胶 gelatin | **确认存在**(另有 gelatin_mixture 明胶混合物) | 【jar】`material.gtceu.gelatin`="Gelatin"/"明胶";【src】`UnknownCompositionMaterials.java:438`;现有获取:搅拌机 胶原蛋白+磷酸+水 → 明胶混合物(HV、洁净室)→ 离心机 → 明胶×4(`GrowthMediumRecipes.java:101-114`);现有用途:高压釜制琼脂 | **复用材料**;注意现有路线偏 HV 化工,食品级获取路线需专项设计 |
| 柠檬酸 citric acid | **确认不存在** | 【jar】lang 无;【src】citric 0 命中 | 需新增 |
| 二氧化碳 CO₂ | **确认存在** | 【jar】`material.gtceu.carbon_dioxide`;【src】`FirstDegreeMaterials.java:1133`;产出例:木材气分馏、发酵生物质分馏(`DistillationRecipes.java:63,112-121`) | 复用(发酵副产回收路线可直接对接) |
| 淀粉 starch | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 葡萄糖 glucose | **确认不存在**(糖 Sugar 已有) | lang 无;全源码 0 命中 | 需新增 |
| 乙醇 ethanol | **确认存在** | 【jar】`material.gtceu.ethanol`;【src】发酵生物质蒸馏产乙醇(`DistillationRecipes.java:112`) | 复用 |
| 乙酸 acetic acid | **确认存在**(官方中文名"乙酸") | 【jar】`material.gtceu.acetic_acid`="Acetic Acid"/"乙酸";【src】`OrganicChemistryMaterials.java:346` | 复用;命名上与食醋(食品)区分 |
| 谷氨酸 / 味精 | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 苯甲酸钠 / 山梨酸 | **确认不存在** | lang 无;全源码 0 命中 | 需新增(防腐剂线) |
| 植物油 | **"plant oil" 不存在;等价物种子油 seed oil 确认存在** | 【jar】`material.gtceu.seed_oil`="Seed Oil"/"种子油";【src】提取机压榨原版种子:小麦/甜菜 10mB、南瓜 6mB、西瓜 3mB(`SeparationRecipes.java:491-513`) | 复用 seed oil,本项目食用油直接以其为基础或新增食品级油种并处理关系 |
| 盐 / 糖 | **确认存在**(糖与原版互通,`GTMaterials.java:114` 忽略重复物品) | 【jar】`material.gtceu.salt`、`material.gtceu.sugar`+方糖;【src】甘蔗破碎产糖(`VanillaStandardRecipes.java:343-347`) | 复用 |
| 面粉 flour | **确认存在**(Wheat 材料的 dust 形态) | 【jar】`item.gtceu.wheat_dust`="Flour"/"面粉";【src】小麦+研杵合成(`VanillaStandardRecipes.java:326`);谷物 tag `dough/wheat`、`grain/wheat`(`CustomTags.java:23-26`) | 复用 |
| 面团 dough | **确认存在**(可食用,nutrition 1) | 【src】`GTItems.java:2050-2060`;制法:搅拌机 面粉×2+水 250mB→×3(`MiscRecipeLoader.java:492-507`) | 复用 |
| 黄油 butter | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 奶酪 cheese | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 酵母 yeast | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 乳酸 lactic acid | **确认不存在** | lang 无;全源码 0 命中 | 需新增 |
| 可可可可粉 | **确认存在**;**可可脂 cocoa butter 确认不存在** | 【jar】`material.gtceu.cocoa`="Cocoa"/"可可";【src】破碎机磨可可豆(`VanillaStandardRecipes.java:336-341`) | 可可粉复用;可可脂需新增 |
| 蛋白质(通用材料) | **确认不存在**;相邻物胶原蛋白 Collagen 已存在 | 【src】`UnknownCompositionMaterials.java:433,455` | 蛋白质需新增;评估与 Collagen 衔接 |
| 氨基酸(通用材料) | **确认不存在**(仅氨基苯酚等具体化工品) | 【jar】lang 检索 | 需新增 |
| UU 物质 uu_matter | **确认存在,但本体无实用配方** | 【src】`UnknownCompositionMaterials.java:590`;配方引用仅 1 处且被注释(`MachineRecipeLoader.java:328`) | 可引用;供给途径待专项确认 |

**补充发现**:牛奶流体已存在(`UnknownCompositionMaterials.java:448`,蛋糕配方原料);鱼油已存在(提取机从鳕鱼获取)。

### 2.2 可复用清单(勿重复制造)

明胶、二氧化碳、乙醇、乙酸、种子油、盐、糖、面粉、面团、可可粉、牛奶流体、鱼油、胶原蛋白、UU 物质;全部 13 类机器(§3);装罐机通用灌装逻辑与成型压机"面团→曲奇"先例。

### 2.3 确认需新增清单

牛油、柠檬酸、淀粉、葡萄糖、谷氨酸、味精、苯甲酸钠、山梨酸、黄油、奶酪、酵母、乳酸、可可脂、通用蛋白质、通用氨基酸,以及全部罐头类食品物品(现有物品与配方中均无罐头)。

### 2.4 待专项确认

1. **UUM 供给**:7.5.3 本体无实用产出/消耗配方,终局合成食品若接 UUM,需确认供给方(自建产线或放弃 UUM 路线);
2. 明胶食品级路线与现有 HV 化工路线的关系(同材料双路线 vs 新增食品级材料);
3. 紫色饮料营养值:源码 `nutrition(4)` 为准(4 点),MC百科"8 点"描述与代码不符。

## 3. GTCEu Modern 机器边界

注册证据统一:【src】`com/gregtechceu/gtceu/common/data/GTMachines.java`(单方块)、`common/data/machines/GTMultiMachines.java`(多方块);显示名见【jar】lang `block.gtceu.<电压>_<机器名>`。

| 机器 | 存在性 | 典型配方方向(证据) | 本项目可复用点 |
| --- | --- | --- | --- |
| 装罐机 Canner | 存在(LV 起;`GTMachines.java:183`) | 静态配方:喷漆罐/滤芯/药片装填;**动态逻辑** `CannerLogic.java`:对任意实现流体容器能力的物品灌装/抽取任意流体(**无任何"食物+罐"配方,无罐头物品**) | 罐头容器若实现 `IFluidHandlerItem` 可天然接入;否则新增专属配方类型 |
| 发酵槽 Fermenter | 存在(LV 起;`GTMachines.java:203`,`FERMENTING_RECIPES`) | **7.5.3 仅 1 条配方**:生物质→发酵生物质(`ChemistryRecipes.java:89-92`) | **配方空间近乎空白,食品发酵零冲突,最高优先复用点**(糖水→酵母、牛奶→酸奶、果汁→酒) |
| 打包机 Packer | 存在(`GTMachines.java:219`;另有大型打包机) | 粉尘合包/拆包、线材合卷、干草块↔小麦 | 食品装盒/装箱 |
| 冲压机床 Forming Press | 存在(`GTMachines.java:211`) | **面团+可可豆×2+圆柱模具→曲奇×12**(`MiscRecipeLoader.java:516-523`) | 巧克力板/糖果/黄油块成型(有食品先例) |
| 搅拌机 Mixer | 存在(`GTMachines.java:215`) | 面粉+水→面团、明胶混合物等 | 面团/馅料/奶昔混合 |
| 离心机 Centrifuge | 存在(`GTMachines.java:184`) | 明胶混合物→明胶+磷等分离 | 乳品分离(奶油/脱脂奶) |
| 蒸馏室 Distillery | 存在(`GTMachines.java:193`) | 水→蒸馏水、油类分馏 | 酒类蒸馏、香精提取 |
| 蒸馏塔 Distillation Tower | 存在(多方块;`GTMultiMachines.java:440`) | 生物质系多产物分馏(**CO₂/乙醇/乙酸均产自这条生物链**) | 食品级多产物精馏后期路线 |
| 筛选机 Sifter | 存在(`GTMachines.java:224`) | 砂砾筛分 | 面粉/香料筛分 |
| 提取机 Extractor | 存在(`GTMachines.java:199`) | 种子→种子油、鳕鱼→鱼油 | 榨油、榨汁 |
| 化学反应釜 Chemical Reactor | 存在(`GTMachines.java:188`) | 海量化工配方 | 食品添加剂合成(MSG/防腐剂) |
| 高压釜 Autoclave | 存在(`GTMachines.java:178`) | 明胶+蒸馏水→琼脂、晶体培育 | 高压蒸煮/灭菌 |
| 真空冷冻机 Vacuum Freezer | 存在(**多方块**,`GTMultiMachines.java:511`;无单方块 freezer) | 冰/冷冻流体、冷冻工艺 | 冷冻食品/冰淇淋路线 |

另有相邻发现:**酿造房 Brewery**(GCYM 多方块)可并行运行 BREWING+FERMENTING+FLUID_HEATER 三类配方(`GCYMMachines.java:569`)——大型发酵产线的现成并行载体。

## 4. GTCEu Modern 食品物品现状

全源码检索 `.food(`,仅 4 处——**本体可食用物品共 4 个**:

| 物品 | 营养/饱和 | 效果 | 获取 |
| --- | --- | --- | --- |
| 紫色饮料 purple_drink | 4 / 0.3 | 90% 急迫 I 40s,返还玻璃瓶 | 仅地牢战利品箱,无合成 |
| 面团 dough | 1 | 40% 饥饿 debuff、5% 中毒(生食惩罚) | 搅拌机/工作台合成 |
| 对乙酰氨基酚药片 | 0(药品) | 解化学品中毒 | 装罐机压片 |
| 消辐宁药片 | 0(药品) | 解致癌物 | 装罐机压片 |

另:`GTFoods.java` 已定义**未绑定任何物品**的 `CHOCOLATE` 食品属性(nutrition 4、饱和 0.3、10% 速度 I 10s、alwaysEat)——官方预留位,本项目巧克力产品线可直接借用或自建物品。除上述外无任何食品;lang 中无 sandwich/soda/juice/beer 等词。

## 5. 生活调味料:胡萝卜版联动(Spice of Life: Carrot Edition)

### 5.1 版本与身份

- **1.20.1 Forge 官方版**:仓库 [Cazsius/Spice-of-Life-Carrot-Edition](https://github.com/Cazsius/Spice-of-Life-Carrot-Edition) 的 **1.20 分支**(mc_version=1.20.1、mod_version=1.15.1、Forge 47);CurseForge 文件 `solcarrot-1.20.1-1.15.1.jar`(fileId **4888575**);Modrinth 无官方页面;
- **modId:`solcarrot`**(mods.toml / 主类常量三重确认);
- 编译期依赖写法:`implementation "curse.maven:spice-of-life-carrot-edition-277616:4888575"`,运行期为软依赖;
- 相邻项目澄清:原版生活调味料(惩罚式)止步 1.12.x;辣条版仅 NeoForge 1.21;Onion 版(精神续作,modid `solonion`)机制不同——**均不纳入联动**。

### 5.2 机制(均自 1.20 分支源码确认)

- **统计全自动**:监听 `LivingEntityUseItemEvent.Finish`,凡 `isEdible()` 物品按注册身份计入,**无需任何模组注册我们的食品**;判定粒度为 Item 本体(不分 NBT/组件);
- **无食物组概念**(那是原版 SoL / Diet 的机制);有效计数 = 通过黑白名单 + 营养值 ≥ `minimumFoodValue`(默认 1);
- **生命上限奖励为里程碑制**:配置数组 `milestones`(默认 [5,10,15,20,25])每档 +`heartsPerMilestone`(默认 2 心),实现为 MAX_HEALTH 永久修饰符(UUID `b20d3436-0d39-4868-96ab-d0a4856e68c6`);
- **无衰减、无推迟卡**;唯一损失路径 `resetOnDeath`(默认 false)。

### 5.3 接口与数据

- **有正式公开 API**(`api` 包,标注 stable):
  - `SOLCarrotAPI.getFoodCapability(Player)` → `FoodCapability.getEatenFoodCount()` / `hasEaten(Item)`;
  - capability key `solcarrot:food`,NBT 结构 `{"foodList": ["minecraft:apple", ...]}`(不编译依赖也可兜底直读);
  - `SOLCarrotAPI.syncFoodList(Player)` 可触发同步;
- **注意**:`getEatenFoodCount()` 是**未过滤口径**(作者留有 TODO),与里程碑用的过滤口径可能不一致;严格口径需自行套 `SOLCarrotConfig.shouldCount(item)` 过滤;
- **没有里程碑达成事件**——外部需实时轮询或在进食事件(建议 `EventPriority.LOWEST`)后读取;
- 配置(世界级 serverconfig):`milestones` 数组、`heartsPerMilestone`、`baseHearts`、黑/白名单(支持 glob)、`minimumFoodValue`、`resetOnDeath`、`limitProgressionToSurvival`;
- 调试:`/foodlist size|sync|clear`。

### 5.4 本项目接入要点(定案建议)

1. 检测:`ModList.get().isLoaded("solcarrot")`,软依赖,所有调用点隔离类加载;
2. 我们的食品**无需注册**即自动计入多样性;两条注意:营养值须 ≥1;**不要实现"跳过进食动画直接消耗"的路径**(否则不计入);
3. **多样性乘数读它的数据,不自建口径**(避免双口径困惑);乘数档位**实时锚定其 `milestones` 配置数组**(服主改配置自动跟随),不硬编码;
4. 读取时机:进食事件后 / 登录、切维度后实时重算,**不持久缓存**(`resetOnDeath=true` 时自动回落);
5. config 开关:`carrotSynergyEnabled`、乘数模式(按里程碑档位 vs 按超出上一里程碑的种数线性插值,推荐后者)、作用对象(时长/强度)、`strictFiltering` 口径开关;
6. 不以心数修饰符作为乘数依据(会被其他模组干扰,且"种类数"更贴合膳食多样性语义);
7. 联调:开发环境双模组安装,`/foodlist size` 与我们的调试命令对照。

## 6. 对设计文档的回填

以下结论已回填 [content-direction-brainstorm.md](content-direction-brainstorm.md):

- §4.1 P2 可可链:可可粉复用、可可脂需新增、CHOCOLATE 预留位;
- §5-B:成型压机已有"面团→曲奇"食品配方先例;
- §5-C:明胶复用 + 食品级路线待专项;牛油确认需新增;
- §5-D:发酵槽配方空间空白,M1 优先复用;
- §5-F:罐头容器方案(流体容器 vs 专属配方类型)待专项定案;
- §7.2:modid `solcarrot`、API、接入要点已确认;
- §10:新增 UUM 供给、罐头容器方案两项待确认;M0 两项审计标记完成。

## 7. 补充查证(2026-09-07,M0 后续,证据同 §1)

| 查证项 | 结论 | 证据 |
| --- | --- | --- |
| 石膏(豆腐凝固剂候选) | **确认存在** | 【jar】`material.gtceu.gypsum` = "Gypsum"/"石膏" |
| 氯化镁(豆腐凝固剂候选) | **确认存在** | 【jar】`material.gtceu.magnesium_chloride` = "Magnesium Chloride"/"氯化镁"(另有氧化镁/菱镁矿等镁系材料) |
| 氨(化肥氮路线) | **确认存在** | 【jar】`material.gtceu.ammonia` = "Ammonia"/"氨";氯化铵、甲酸铵亦在 |
| 磷系(化肥磷路线) | **确认存在全链** | 磷灰石 `apatite`(含专属矿脉 `gtceu.jei.ore_vein.apatite_vein`)、磷 `phosphorus`、磷酸盐 `phosphate`、磷酸 `phosphoric_acid`、五氧化二磷、磷酸三钙 |
| 肥料 | **GTCEu 已有肥料物品** | 【jar】`item.gtceu.fertilizer` = "Fertilizer"/"肥料" |
| rice/tea 命名冲突 | **无冲突** | 【jar】lang 与【src】GTMaterials.java 均无 rice/tea 材料/物品(唯一命中为紫色饮料风味文本 "Ice Tea") |

**设计含义**:

1. 大豆线:凝固剂两个候选均可落地——卤水可由氯化镁 + 水(搅拌机)制取为消耗型流体,石膏直接消耗粉尘,两品类并行成立;
2. 温室:化肥无需自建基础物品——可直接复用 GTCEu 肥料;"有机肥料"升级变种(豆粕/豆渣路线)留待温室定案时决策;
3. 作物基座:`rice`、`tea` 命名安全,无需避让。
