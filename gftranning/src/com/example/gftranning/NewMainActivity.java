
package com.example.gftranning;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NewMainActivity extends Activity implements OnClickListener {

    ProgressBar mProgressBar;

    TextView mProgressText;

    View mBtnNew;

    View mBtnQuickStart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressText = (TextView) findViewById(R.id.progress_text);

        mBtnNew = findViewById(R.id.new_game);
        mBtnQuickStart = findViewById(R.id.quick_start);
        mBtnNew.setOnClickListener(this);
        mBtnQuickStart.setOnClickListener(this);

        loadProgress();
    }

    private void loadProgress() {

    }

    @Override
    public void onClick(View arg0) {
        if (arg0 == mBtnNew) {

        } else if (arg0 == mBtnQuickStart) {

        }
    }

}
