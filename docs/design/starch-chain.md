# 淀粉糖浆链(Starch & Syrup Chain)

> **状态:已定案并实装(M1 首版)** — 2026-09-08
>
> M1 首个闭环清单项(content-direction-brainstorm §M1)中最后一个大缺口。GTCEu 无
> 淀粉/葡萄糖材料(m0-content-audit §2.3 已证),全链自建。本链同时是 biochem-chain
> 味精链(§2)的上游与千叶豆腐真实原料的供应方(soybean-chain §5 欠账清算)。

## 1. 链路总览 [已定案]

```
玉米 ×3 + 水 500
  → 搅拌机·湿磨取淀粉 → 淀粉尘 ×4(LV,EUt 8)
  ├→ 化学反应釜·糊化液化(淀粉1 + 水250)→ 玉米糖浆 250 mB(LV,EUt 16,物理无试剂)
  └→ 化学反应釜·酸解糖化(淀粉2 + 水500 + 盐酸100)→ 葡萄糖 ×2(MV,EUt 30)
```

- **出率**:湿磨 ~70%(玉米×3→淀粉×4);酸解低出成 1:1(淀粉×2→葡萄糖×2)——
  biochem §2.1 定案"酸解低产出/酶解高出成"的 M1 落点;
- 盐酸为 GTCEu 既有化学品(消耗投入,非催化剂),反应釜流体输入 3 路上限内
  (GTRecipeTypes.java:180 maxIO 2/2/3/2,已核对)。

## 2. 材料与表单 [已定案]

| 材料 | 表单 | 语义 | 消费点 |
| --- | --- | --- | --- |
| 淀粉 starch | 粉尘 | 玉米湿磨产物,白色粉末 | 千叶豆腐(已切)、M3 味精链起点、淀粉肠掺混(meat-chain §5,后补) |
| 葡萄糖 glucose | 粉尘 | 淀粉完全水解产物 | M3 味精链上游(谷氨酸发酵)、柠檬酸发酵原料(biochem §3) |
| 玉米糖浆 corn_syrup | 流体 | 淀粉部分水解中间品 | 食品甜味剂;饮品/烘焙 M2 消费点预留(预留期不算空动因:M1 清单明示立项) |

- 注册走 GTFEMaterials 自有 registry(dust 材料自动生成粉尘物品,GTCEu Builder
  `.dust()` 已核对);中文条目入 zh_cn.json(`material.gregfoodexpansion.*`)。

## 3. 范围线(M1)[已定案]

- **只做**:湿磨 + 物理糊化液化 + 酸解糖化;
- **不做**:酶解糖化(糖化酶耐久催化,高出成 ×3~4)——留 M3 随酶制剂家族接入
  (biochem §4),届时替换/并列酸解配方;玉米油(湿磨副产胚芽榨油)留油脂线扩展;
- 谷氨酸发酵(HV)、味精 = M3,不在本期。

## 4. 千叶豆腐切换 [已定案 2026-09-08]

soybean-chain §5 的"暂用精制面粉代淀粉"欠账就此清算:千叶豆腐坯配方
`dust Wheat → dust starch`(GFESoybeanRecipes),工艺语义归正(大豆蛋白+淀粉)。

## 5. 实现进度

- 2026-09-08:`GTFEMaterials` 增 3 材料(STARCH/GLUCOSE/CORN_SYRUP)、
  `data/GFEStarchRecipes` 3 条配方(湿磨/液化/酸解)、千叶豆腐切淀粉、
  zh_cn 语言 3 条、addon 接线;compileJava + 离线 runData 通过
  (GT 机器配方为运行时注册,无 JSON 落盘,与既往链一致;实机行为随 runClient 联调);
- 待办:营养/出率实机调参;酶解路线(M3);玉米糖浆消费点(M2 饮品/烘焙)。

## 参考

- [味精链上游](biochem-chain.md) §2(糖化双路线)、[大豆加工线](soybean-chain.md) §5(千叶豆腐)
- [m0 内容审计](m0-content-audit.md) §2.3(淀粉/葡萄糖不存在,需新增)
