package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by stuart on 2/5/16.
 */

//DialogPreference must be subclassed to use it in the preferences. That is why this file exists
public class ClearPingsDialogPreference extends DialogPreference {
    public ClearPingsDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
