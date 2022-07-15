package com.github.sachin.prilib.utils;

import com.github.sachin.prilib.Prilib;

public class RandomUtils {

    public static double getDouble(int min,int max){
        return Prilib.getInstance().getThreadLocalRandom().nextDouble(min,max);
    }

    public static int getInt(int min,int max){
        return Prilib.getInstance().getThreadLocalRandom().nextInt(min,max);
    }

    public static boolean getChance(Double chance){
        return  getDouble(0,1) <= chance;
    }


}
