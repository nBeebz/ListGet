package com.example.nav.listget.Activities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.example.nav.listget.R;

public class CheckableLinearLayout extends LinearLayout implements Checkable {

    private static final int CHECKABLE_CHILD_INDEX = 0;
    private CheckedTextView child;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = (CheckedTextView) getChildAt(CHECKABLE_CHILD_INDEX);
    }

    @Override
    public boolean isChecked() {
        return child.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked) {
            child.setCheckMarkDrawable(R.drawable.icon_check);
        } else {
            child.setCheckMarkDrawable(R.drawable.icon_check_off);
        }
        child.setChecked(checked);
    }

    @Override
    public void toggle() {
        child.toggle();
    }

}
