package com.tutors.varsity.ui.event;

/**
 * Created by Landon on 5/21/15.
 */
public class LineThicknessPicked {

    private int lineThickness;
    public LineThicknessPicked(int size) {
        this.lineThickness = size;
    }

    public int getLineThickness() {
        return lineThickness;
    }
}
