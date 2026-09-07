package net.mgear.gregfoodexpansion.data;

import com.tterrag.registrate.providers.RegistrateItemTagsProvider;

/**
 * 三层标签策略的落点(compatibility-boundary.md §3):
 * ① forge 通用语义标签(crops/raw_meats/dairy/grain 等);
 * ② forge 形态标签(sliced_meat/minced_meat 等);
 * ③ gregfoodexpansion 内部私有分组——语义上属于通用族的一律进 ①/② 层,不进本层。
 * 本模组加工配方只引用标签,不硬编码物品 id。
 */
public final class GFEItemTags {
    private GFEItemTags() {}

    public static void init(RegistrateItemTagsProvider provider) {
        // forge:crops/<crop>、forge:seeds/<crop>、forge:grain/<crop> 随 M1 作物落地(crop-system-foundation.md §7)。
    }
}
