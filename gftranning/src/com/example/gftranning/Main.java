package com.example.gftranning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
		v2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Main.this, TestGameActivity2.class);
				startActivity(intent);
			}
		});
	}

}
