package com.example.counter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

	/** Views we will want to modify, as attributes they will be more accessible
	*/
	private TextView txt_date;
	private TextView txt_time;
	private TextView txt_counter;
	private StatisticsDataSource statistics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Debug.enable();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt_date = (TextView) findViewById(R.id.txt_date);
		txt_time = (TextView) findViewById(R.id.txt_time);
		txt_counter = (TextView) findViewById(R.id.txt_counter);
		
		//init counter
		statistics = new StatisticsDataSource(this.getApplicationContext());
		int total = statistics.getTotalCounts();
		txt_counter.setText(Integer.toString(total));
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem submenu = menu.findItem(R.id.statistics);
		getMenuInflater().inflate(R.menu.sub_menu, submenu.getSubMenu());
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id)
		{
		case R.id.categories:
			runCategoriesActivity();
			return true;
		case R.id.exit:
			finish();
			return true;
		case R.id.alltime_statistics:
			runStatistics();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/** Launches the Categories Activity
	 */
	private void runCategoriesActivity() {
		Intent i = new Intent(this,CategoriesActivity.class);
		startActivity(i);
	}

	/** Launches the Categories Activity
	*/
	private void runStatistics() {
		Intent i = new Intent(this,AlltimeStatisticsActivity.class);
		startActivity(i);
	}

	/** function called when the counter button is pressed
	 * adds +1 to the counter totals
	 */
	public void runCounter(View view) {
		Integer count = Integer.parseInt(txt_counter.getText().toString());
		count++;
		txt_counter.setText(count.toString());

		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formatted_date = df.format(c.getTime());
		txt_date.setText(formatted_date);
		
		SimpleDateFormat tf = new SimpleDateFormat("hh:mm:ss a");
		String formatted_time = tf.format(c.getTime());
		txt_time.setText(formatted_time);

		int id_category = statistics.getCategory();
		statistics.saveCount(c.getTime(),id_category);
	}

	@Override
	protected void onResume() {
		statistics.open();
		int total = statistics.getTotalCounts();
		txt_counter.setText(Integer.toString(total));
		super.onResume();
	}

	@Override
	protected void onPause() {
		statistics.close();
		super.onPause();
	}

	@Override
	public void onStop(){
		statistics.close();
		super.onStop();
	}

	@Override
	public void onDestroy(){
		statistics.close();
		super.onDestroy();
	}
}
