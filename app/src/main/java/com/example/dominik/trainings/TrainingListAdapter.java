package com.example.dominik.trainings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.dominik.trainings.entities.Training;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

import io.realm.Realm;

@EBean
public class TrainingListAdapter extends BaseAdapter {

    List<Training> trainings;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        trainings = Realm.getDefaultInstance().allObjects(Training.class);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TrainingItemView trainingItemView;
        if (convertView == null) {
            trainingItemView = TrainingItemView_.build(context);
        } else {
            trainingItemView = (TrainingItemView) convertView;
        }

        trainingItemView.bind(getItem(position));

        return trainingItemView;
    }

    @Override
    public int getCount() {
        return trainings.size();
    }

    @Override
    public Training getItem(int position) {
        return trainings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
