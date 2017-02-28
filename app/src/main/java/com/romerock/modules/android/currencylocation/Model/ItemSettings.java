package com.romerock.modules.android.currencylocation.Model;

import java.util.Comparator;

/**
 * Created by Ebricko on 16/12/2016.
 */

public class ItemSettings {
    public String tittle;

    public int getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(int imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return tittle;
    }

    public void setTitle(String title) {
        this.tittle = title;
    }

    private int imageUrl;

    public ItemSettings(String title, int imageUrl, boolean status, boolean selected){

        this.tittle = title;
        this.imageUrl = imageUrl;
        this.statusEnable=status;
        this.selected=selected;
    }

    public boolean isStatusEnable() {
        return statusEnable;
    }

    public void setStatusEnable(boolean statusEnable) {
        this.statusEnable = statusEnable;
    }

    private boolean statusEnable;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;


    public static Comparator<ItemSettings> ItemByName = new Comparator<ItemSettings>() {

        public int compare(ItemSettings s1, ItemSettings s2) {
            String ItemSettingsName1 = s1.getTitle().toUpperCase();
            String ItemSettingsName2 = s2.getTitle().toUpperCase();

            //ascending order
            return ItemSettingsName1.compareTo(ItemSettingsName2);

            //descending order
            //return ItemSettingsName2.compareTo(ItemSettingsName1);
        }};


    
}
