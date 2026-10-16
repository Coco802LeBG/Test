package com.coco802.addon;

import com.coco802.addon.modules.AutoMsg;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class Coco802Addon extends MeteorAddon {
    public static final Category CATEGORY = new Category("Coco802_");

    @Override
    public void onInitialize() {
        Modules.get().add(new AutoMsg());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.coco802.addon";
    }
}
