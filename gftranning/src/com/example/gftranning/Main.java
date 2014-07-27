
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
        new Thread() {
            public void run() {
                Socket socket = new Socket();
                InetAddress addr;
                try {
                    addr = InetAddress.getByName("www.baidu.com");
                    socket.connect(new InetSocketAddress(addr, 80));
                    OutputStream ous = socket.getOutputStream();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Builder seqBuilder = new RandomSequence.Builder();
        seqBuilder.setRange(8).setRepeatDistance(1).setRepeatRatio(0.3);
        ISequenceSource ss = seqBuilder.build();
        for (int i = 0; i < 10; ++i) {
            Log.e("hello", "i:" + ss.getNext());
        }

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
