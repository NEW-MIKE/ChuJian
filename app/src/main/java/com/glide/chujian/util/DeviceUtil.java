package com.glide.chujian.util;

import android.util.Log;

import com.glide.chujian.model.GuiderModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;


public class DeviceUtil {
    public static boolean isLabel(@NotNull ArrayList<GuiderModel.AllDevice> allDevices, @NotNull String name) {
        int size = allDevices.size();
        for (int i = 0; i < size;i++){
            if (allDevices.get(i).mLabel.equals(name)){
                ArrayList<GuiderModel.Device> devices = allDevices.get(i).mDevices;
                for (int j = 0; j < devices.size(); j++){
                    if (devices.get(j).mName.equals(name)){
                        Log.e("TAG", "isLabel: "+"false");
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    public static boolean isLabelConfigured(@NotNull ArrayList<GuiderModel.AllDevice> allDevices, @NotNull String label) {
        for (int i = 0; i < allDevices.size();i++){
            if (allDevices.get(i).mLabel.equals(label)){
                return allDevices.get(i).mConfigured;
            }
        }
        return false;
    }

    public static ArrayList<String> parseDevices(@NotNull ArrayList<GuiderModel.AllDevice> allDevices) {
        ArrayList<String> data = new ArrayList();
        ArrayList<String>  configuredNoDevice = new ArrayList();
        ArrayList<String>  configuredWithDevice = new ArrayList();
        ArrayList<String>  unconfiguredDevice = new ArrayList();

        for (int i = 0; i < allDevices.size(); i++){
            ArrayList<GuiderModel.Device>  devices = allDevices.get(i).mDevices;
            if (allDevices.get(i).mConfigured){
                if (devices.size() == 0){
                    configuredNoDevice.add(allDevices.get(i).mLabel);
                }
                else{
                    for (int j = 0; j < devices.size(); j++){
                        configuredWithDevice.add(devices.get(j).mName);
                    }
                }
            }
            else
            {
                unconfiguredDevice.add(allDevices.get(i).mLabel);
                for (int j = 0; j < devices.size();j++){
                    unconfiguredDevice.add(devices.get(j).mName);
                }
            }
        }
        data.addAll((Collection)configuredWithDevice);
        data.addAll((Collection)configuredNoDevice);
        data.addAll((Collection)unconfiguredDevice);
        return data;
    }

}
