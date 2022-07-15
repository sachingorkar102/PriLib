package com.github.sachin.prilib.utils;

import com.google.common.base.Enums;
import com.google.common.base.Predicates;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ItemUtils {


//
    public static List<Material> getMaterialsFromStrings(List<String> list, Predicate<Material> check){
        List<Material> materials = new ArrayList<>();

        for(String str : list){
            Material mat = Enums.getIfPresent(Material.class,str).orNull();
            if(mat !=null && check.test(mat)){
                materials.add(mat);
            }
        }
        return materials;
    }


}
