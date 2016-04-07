package edu.gcc.whiletrue.pingit;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.ParseException;
import java.util.List;

/**
 * Created by stuart on 2/5/16.
 */

//DialogPreference must be subclassed to use it in the preferences. That is why this file exists
public class ClearPingsDialogPreference extends DialogPreference {
    public ClearPingsDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult){
            try {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Pings");
                query.whereEqualTo("User", ParseUser.getCurrentUser());
                List<ParseObject>pingsList = query.find();
                //Toast.makeText(getContext(), pingsList.size() + " PINGS CLEARED", Toast.LENGTH_SHORT).show();
                for(ParseObject p : pingsList) p.deleteInBackground();
                Toast.makeText(getContext(), R.string.PingsCleared, Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Toast.makeText(getContext(), R.string.UnableToClearPings,Toast.LENGTH_LONG).show();
            }
        }
    }
}
