# 设计方案(Design Documents)

本目录存放 GregFood Expansion 的设计文档(设计 v2.0,2026-09-09 全量重置版)。

## 阅读顺序

1. [overview.md](overview.md) — **总纲**:定位、主线三问、十条设计原则、跨度与数量总账(必读)
2. [tier-map.md](tier-map.md) — 电压铺排:ULV–ZPM 钥匙制、UV–OpV 顶档闭合、MAX 终局
3. [machines.md](machines.md) — 机器体系:多方块优先+单一用途,22 台多方块 + GT 复用清单
4. 内容体系:crops.md(作物)/ livestock.md(肉蛋奶)/ food-processing.md(食材处理与配料宇宙)/ fermentation-biochem.md(发酵·酶·菌种)
5. 菜肴体系:cuisine-matrix.md(A 群矩阵)/ signature-cuisine.md(B 群签名与菜系包)/ gains-nutrition.md(增益·SoL)
6. 支撑体系:gt-interlocks.md(化工咬合)/ content-pipeline.md(管线与治理)/ milestones.md(里程碑)

## 约定

- 文件名小写英文连字符;头部标注状态(方向已定案 / 数值待收口 / 已实装)与日期;
- **内容清单不写进文档**——一律入数据表(见 content-pipeline.md),文档只存规则;
- 数值未标注"已定案"即为草案;实装前按收口制逐项定案回写;
- 旧版设计(2026-09-07)已整体废弃,git 历史可考古;与新文档冲突处以新文档为准。
