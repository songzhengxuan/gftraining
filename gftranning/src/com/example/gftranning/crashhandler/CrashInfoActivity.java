package com.example.gftranning.crashhandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

public class CrashInfoActivity extends Activity {

	public static final String EXTRA_KEY_STRING_STACKTRACE = "st";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
		textView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		setContentView(textView);
		textView.setMovementMethod(new ScrollingMovementMethod());

		String strace = getIntent().getStringExtra(EXTRA_KEY_STRING_STACKTRACE);
		if (TextUtils.isEmpty(strace)) {
			strace = "no trace";
		}
		textView.setText(strace);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		finish();
	}

}
