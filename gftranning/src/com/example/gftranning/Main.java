
package com.example.gftranning;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.gftranning.RandomSequence.Builder;

public class Main extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
                Intent intent = new Intent(Main.this, GameActivity.class);
                intent.putExtra(GameActivity.EXTRA_INT_TEST_COUNT,
                        Integer.parseInt(countEdit.getText().toString()));
                intent.putExtra(GameActivity.EXTRA_INT_TEST_DISTANCE,
                        Integer.parseInt(distanceEdit.getText().toString()));
                startActivity(intent);
            }
        });
    }

}
