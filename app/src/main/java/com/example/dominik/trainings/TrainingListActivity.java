package com.example.dominik.trainings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dominik.trainings.entities.Training;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import static android.widget.Toast.makeText;

@Fullscreen
@EActivity(R.layout.activity_training_list)
public class TrainingListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @ViewById(R.id.trainingListInActivity)
    ListView trainingListInActivity;

    @Bean
    TrainingListAdapter adapter;

    @AfterViews
    void bindAdapter() {
        trainingListInActivity.setAdapter(adapter);
    }

    @ItemClick(R.id.trainingListInActivity)
    void trainingListItemClicked(Training training) {
        makeText(this, training.getActivityId() + " " + training.getDescription(), Toast.LENGTH_SHORT).show();
        int a =5;
    }
}
