package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import com.parse.ParseUser;

/**
 * Created by nalta on 4/4/2016.
 */
public class ChangeDisplayNamePreference extends EditTextPreference {
    public ChangeDisplayNamePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(String text) {
        super.setText(text);

        ParseUser.getCurrentUser().put("friendlyName",text);
    }
}
