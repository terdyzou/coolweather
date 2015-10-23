package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;

public class UpdateSettingsActivity extends Activity {

	private CheckBox autoUpdateBox;
	private EditText updateFrequencyText;
	private Button saveSettings;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_settings);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		autoUpdateBox = (CheckBox) findViewById(R.id.auto_update);
		updateFrequencyText = (EditText) findViewById(R.id.update_frequency);
		saveSettings = (Button) findViewById(R.id.save_settings);

		boolean autoUpdate = prefs.getBoolean("auto_update", false);

		if (autoUpdate) {
			int updateFrequency = prefs.getInt("update_frequency", 8);
			autoUpdateBox.setChecked(true);
			updateFrequencyText.setText(Integer.toString(updateFrequency));
		}

		saveSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editor = prefs.edit();
				if (autoUpdateBox.isChecked()) {
					editor.putBoolean("auto_update", true);
					int hours = 8;
					if (!TextUtils.isEmpty(updateFrequencyText.getText())) {
						String updateFrequency = updateFrequencyText.getText()
								.toString();
						hours = Integer.parseInt(updateFrequency);
						editor.putInt("update_frequency", hours);
					}
					// 重新激活服务
					Intent intent = new Intent(UpdateSettingsActivity.this,
							AutoUpdateService.class);
					intent.putExtra("update_frequency", hours);
					startService(intent);
				} else {
					editor.putBoolean("auto_update", false);
					Intent intent = new Intent(UpdateSettingsActivity.this,
							AutoUpdateService.class);
					stopService(intent);
				}
				editor.commit();

				Intent intent = new Intent(UpdateSettingsActivity.this,
						WeatherActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, WeatherActivity.class);
		startActivity(intent);
	}
}
