package com.example.testinggrounds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.preference.Preference;

public class ButtonPreference extends Preference {

    public ButtonPreference(Context context) {
        super(context);
    }

    public ButtonPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    private void setOnClickListener(View preferenceView) {
        if (preferenceView != null && preferenceView instanceof ViewGroup) {
            ViewGroup widgetFrameView = ((ViewGroup)preferenceView.findViewById(android.R.id.widget_frame));
            if (widgetFrameView != null) {
                // find the button
                Button theButton = null;
                int count = widgetFrameView.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = widgetFrameView.getChildAt(i);
                    if (view instanceof Button) {
                        theButton = (Button)view;
                        break;
                    }
                }

                if (theButton != null) {
                    // set the OnClickListener
                    theButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // do whatever you want to do
                        }
                    });
                }
            }
        }
    }
}
