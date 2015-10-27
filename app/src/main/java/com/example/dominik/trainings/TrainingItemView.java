package com.example.dominik.trainings;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Dominik on 2015-10-23.
 */
@EViewGroup(R.layout.training_item)
public class TrainingItemView extends LinearLayout{

    @ViewById(R.id.trainingType)
    TextView type;

    @ViewById(R.id.trainingDuration)
    TextView duration;

    @ViewById(R.id.trainingDistance)
    TextView distance;

    public TrainingItemView(Context context) {
        super(context);
    }

    public void bind(Training training) {
        type.setText(training.getType());
        duration.setText(Double.toString(training.getDuration()));
        distance.setText(Double.toString(training.getDistance()));
    }
}
