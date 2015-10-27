package com.example.dominik.trainings;

import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Dominik on 2015-09-07.
 */

@SharedPref(value=SharedPref.Scope.UNIQUE)
public interface MyPrefs {

    @DefaultString("")
    String heartRateType();

    @DefaultInt(0)
    int locationUpdates();

    @DefaultString("Dominik Bieliński")
    String name();

    @DefaultString("")
    String device();

    @DefaultInt(100)
    int accuracy();

}