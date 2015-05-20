package com.tutors.varsity.ui.event;

public class ColorPicked {

    private int colorId;
    public ColorPicked(int resourceId) {
        this.colorId = resourceId;
    }

    public int getColorId() {
        return colorId;
    }
}
