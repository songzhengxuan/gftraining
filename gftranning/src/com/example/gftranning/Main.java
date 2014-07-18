
package com.example.gftranning;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ScoreRecoder recorder = new ScoreRecoder();
        
        ArrayList<Integer> lists = new ArrayList<Integer>();
        lists.add(1010);
        lists.add(2010);
        lists.add(3010);
        
        Set<String> string = recorder.parseToRecords(lists);
        List<Integer> lists2 = recorder.parseToScoreNumber(string);
        Log.d("hello", "" + lists.equals(lists2));
        

        View v1 = findViewById(R.id.text1);
        v1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Main.this, TestActivity.class);
                startActivity(intent);
            }
        });
        View v2 = findViewById(R.id.text2);
        final EditText countEdit = (EditText) findViewById(R.id.test_count_edit);
        final EditText distanceEdit = (EditText) findViewById(R.id.test_distance_edit);
        v2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Main.this, TestGameActivity2.class);
                intent.putExtra(TestGameActivity2.EXTRA_INT_TEST_COUNT,
                        Integer.parseInt(countEdit.getText().toString()));
                intent.putExtra(TestGameActivity2.EXTRA_INT_TEST_DISTANCE,
                        Integer.parseInt(distanceEdit.getText().toString()));
                startActivity(intent);
            }
        });
    }

}
