package net.mgear.gregfoodexpansion.data;

import net.mgear.gregfoodexpansion.registry.GFERegistration;

/**
 * en_us 语言条目通过 {@link GFERegistration#REGISTRATE} 的 addRawLang 登记并由 runData 生成;
 * zh_cn 手工维护于 src/main/resources/assets/gregfoodexpansion/lang/zh_cn.json。
 */
public final class GFELang {
    private GFELang() {}

    public static void init() {
        // 创造模式标签标题由 GFECreativeModeTabs 的 addLang 生成;首个内容条目随 M1 内容落地。
    }

    private static void add(String key, String value) {
        GFERegistration.REGISTRATE.addRawLang(key, value);
    }
}
