package com.glide.chujian.util;

public class SoftInputUtil {
    public static String formatPositiveNumberNoDot(String currentInput,String nowInput,int currentIndex){
        if (currentInput.equals("-") || currentInput.equals(".")){
            return nowInput;
        }
        if (nowInput.equals("0")){
            return nowInput;
        }
        return nowInput.substring(0,currentIndex)+currentInput+nowInput.substring(currentIndex);
    }

    public static String formatPositiveNumberWithDot(String currentInput,String nowInput,int currentIndex){
        if (currentInput.equals("-")){
            return nowInput;
        }
        if (nowInput.equals("0") && !currentInput.equals(".")){
            return nowInput;
        }
        if (nowInput.contains(".") && currentInput.equals(".")){
            return nowInput;
        }
        return nowInput.substring(0,currentIndex)+currentInput+nowInput.substring(currentIndex);
    }

    public static String formatNegativeNumberWithDot(String currentInput, String nowInput, int currentIndex){
        if (nowInput.equals("0") && !currentInput.equals(".")){
            return nowInput;
        }
        if (nowInput.contains(".") && currentInput.equals(".")){
            return nowInput;
        }
        if (nowInput.contains("-") && currentInput.equals("-")){
            return nowInput;
        }
        if (!nowInput.equals("") && currentInput.equals("-")){
            return nowInput;
        }
        return nowInput.substring(0,currentIndex)+currentInput+nowInput.substring(currentIndex);
    }

    public static String formatNegativeNumberNoDot(String currentInput, String nowInput, int currentIndex){
        if (currentInput.equals(".")){
            return nowInput;
        }
        if (nowInput.equals("0")){
            return nowInput;
        }
        if (nowInput.contains("-") && currentInput.equals("-")){
            return nowInput;
        }
        if (!nowInput.equals("") && currentInput.equals("-")){
            return nowInput;
        }
        return nowInput.substring(0,currentIndex)+currentInput+nowInput.substring(currentIndex);
    }
    public static String formatStringSoftInput(String currentInput, String nowInput, int currentIndex){
        return nowInput.substring(0,currentIndex)+currentInput+nowInput.substring(currentIndex);
    }
}
