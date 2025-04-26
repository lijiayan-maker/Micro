package com.mycro.micro.bean;


import java.io.Serializable;

/**
 * 风格实体类，需要序列化
 */
public class StyleBean implements Serializable {


    private String styleName;
    private int imageResourceId;

    public StyleBean() {

    }

    public StyleBean(String styleName, int imageResourceId) {
        this.styleName = styleName;
        this.imageResourceId = imageResourceId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    @Override
    public String toString() {
        return "StyleBean{" +
                "styleName='" + styleName + '\'' +
                ", imageResourceId=" + imageResourceId +
                '}';
    }

}
