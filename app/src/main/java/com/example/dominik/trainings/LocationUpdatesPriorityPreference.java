package com.example.dominik.trainings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Created by Dominik on 2015-09-07.
 */
public class LocationUpdatesPriorityPreference extends ListPreference {

    public LocationUpdatesPriorityPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        CharSequence[] entries = {"High accuracy","Balanced","Low accuracy"};
        CharSequence[] entryValues = {"100","102","104"};
        setEntries(entries);
        setEntryValues(entryValues);
    }

    public LocationUpdatesPriorityPreference(Context context) {
        this(context, null);
    }

}
