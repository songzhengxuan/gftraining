
package com.example.gftranning;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class NewMainActivity extends Activity implements OnClickListener {

    View mBtnNew;

    View mBtnQuickStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        mBtnNew = findViewById(R.id.new_game);
        mBtnQuickStart = findViewById(R.id.quick_start);
        mBtnNew.setOnClickListener(this);
        mBtnQuickStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == mBtnNew) {

        } else if (arg0 == mBtnQuickStart) {

        }
    }

}
