package activity;

import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;
import android.R;
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
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {

	private LinearLayout weatherInfoLayout;

	/**
	 * 用于显示城市名
	 * */
	private TextView cityNameText;

	/**
	 * 用于显示发布时间
	 * */
	private TextView publishText;

	/**
	 * 用于显示天气描述信息
	 * */
	private TextView weatherDespText;

	/**
	 * 用于显示气温1
	 * */
	private TextView temp1Text;

	/**
	 * 用于显示气温2
	 * */
	private TextView temp2Text;

	/**
	 * 用于显示当前日期
	 * */
	private TextView currentDateText;

	/**
	 * 用于切换城市按钮
	 * */
	private Button switvhCity;

	/**
	 * 用于更新天气按钮
	 * */
	private Button refreshWeather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.coolweather.app.R.layout.weather_layout);
		// 初始化各控件
		weatherInfoLayout = (LinearLayout) findViewById(com.coolweather.app.R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(com.coolweather.app.R.id.city_name);
		publishText = (TextView) findViewById(com.coolweather.app.R.id.publish_text);
		weatherDespText = (TextView) findViewById(com.coolweather.app.R.id.weather_desp);
		temp1Text = (TextView) findViewById(com.coolweather.app.R.id.temp1);
		temp2Text = (TextView) findViewById(com.coolweather.app.R.id.temp2);
		currentDateText = (TextView) findViewById(com.coolweather.app.R.id.current_data);
		switvhCity = (Button) findViewById(com.coolweather.app.R.id.switch_city);
		refreshWeather = (Button) findViewById(com.coolweather.app.R.id.refresh_weather);
		switvhCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

		String countyCode = getIntent().getStringExtra("county_code");

		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);

		} else {
			// 没有县级代号时就直接显示本地天气
			showWeather();
		}

	}

	/**
	 * 查询县级代号所对应的天气代号
	 */
	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFormServer(address, "countyCode");

	}

	private void queryFormServer(String address, String string) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 查询天气代号所对应的天气。
	 * */

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather./com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFormServer(address, "weatherCode");

	}

	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * 
	 * */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息，并显示在界面上。
	 * */

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case com.coolweather.app.R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case com.coolweather.app.R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weatther_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);

			}
			break;
		default:
			break;
		}
	}

}
