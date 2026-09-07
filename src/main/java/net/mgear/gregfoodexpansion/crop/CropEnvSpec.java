package net.mgear.gregfoodexpansion.crop;

import java.util.EnumSet;

/**
 * 作物环境参数(crop-system-foundation.md §3.2,注册时绑定的代码字段,阶段二温室直接读取)。
 * 温度口径 = 原版生物群系基础温度(-0.5 ~ 2.0);hydrationMin = 下方耕地含水量下限(0-7);
 * lightMin = 生长最低光照(存活线恒为 vanilla 的 8,见 GFECropBlock)。
 */
public record CropEnvSpec(double tempMin, double tempMax, int hydrationMin, int lightMin,
                          EnumSet<CropFlag> flags) {
}
