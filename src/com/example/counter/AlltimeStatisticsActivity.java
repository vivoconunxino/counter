package com.example.counter;

import android.os.Bundle;
import android.view.Menu;

import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlltimeStatisticsActivity extends Activity {

	private GraphicalView mChart;
	private StatisticsDataSource statistics;
	private ChartUtils chart_utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		chart_utils = new ChartUtils();

		setContentView(R.layout.activity_statistics);
		statistics = new StatisticsDataSource(this.getApplicationContext());

		openAlltimeChart();
	}

	private void openAlltimeChart(){

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

		//get all counts
		List<GroupCount> group_counts = statistics.getAllCategoriesCountsGroupedByDate();

		ArrayList<Category> categories = statistics.getArrayCategories();
		Map<Integer, ArrayList<GroupCount>> map_category_counts = new HashMap<Integer, ArrayList<GroupCount>>();

		for(Category category:categories)
		{
			map_category_counts.put(category.getId(), new ArrayList<GroupCount>());
		}

		for(GroupCount count:group_counts)
		{
			ArrayList<GroupCount> array_counts = map_category_counts.get(count.getCategoryId());
			if(array_counts!=null)
				array_counts.add(count);
			map_category_counts.put(count.getCategoryId(),array_counts);
		}

		for(Entry<Integer,ArrayList<GroupCount>> entry: map_category_counts.entrySet())
		{
			int id_category = entry.getKey();
			ArrayList<GroupCount> a_counts= entry.getValue();
			TimeSeries counts_date_idcat = new TimeSeries(statistics.getCategoryName(id_category));

			if(a_counts != null)
			{
				for(GroupCount c:a_counts)
				{
					counts_date_idcat.add(c.getDate(), c.getCounts());
				}
			}

			// Adding the category counts statistic to the dataset
			dataset.addSeries(counts_date_idcat);
		}


		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

		multiRenderer.setChartTitle("All categories click count Chart");
		multiRenderer.setXTitle("Days");
		multiRenderer.setYTitle("Count");
		multiRenderer.setZoomButtonsVisible(true);

		// Adding all the category renderers
		// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
		// should be same

		for(Entry<Integer,ArrayList<GroupCount>> entry: map_category_counts.entrySet())
		{
			// Creating XYSeriesRenderer to customize visitsSeries
			XYSeriesRenderer categoryRenderer = new XYSeriesRenderer();
			categoryRenderer.setFillPoints(true);
			categoryRenderer.setLineWidth(2);
			categoryRenderer.setDisplayChartValues(true);

			categoryRenderer.setColor(chart_utils.getColor());
			categoryRenderer.setPointStyle(chart_utils.getPointStyle());

			multiRenderer.addSeriesRenderer(categoryRenderer);
		}

		// Getting a reference to LinearLayout of the activity Layout
		LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_view);

		// Creating a Time Chart
		mChart = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), dataset, multiRenderer,"dd-MMM-yyyy");

		multiRenderer.setClickEnabled(true);
		multiRenderer.setSelectableBuffer(10);

		// Setting a click event listener for the graph
		mChart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Format formatter = new SimpleDateFormat("dd-MMM-yyyy");

				SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();

				if (seriesSelection != null) {
					int series_index = seriesSelection.getSeriesIndex();
					String selected_series="";
					switch(series_index)
					{
						case 0:
						default:
							selected_series = "cat.name";
							break;
					}

					// Getting the clicked Date ( x value )
					long clickedDateSeconds = (long) seriesSelection.getXValue();
					Date clickedDate = new Date(clickedDateSeconds);
					String str_date = formatter.format(clickedDate);

					// Getting the y value
					int amount = (int) seriesSelection.getValue();

					// Displaying Toast Message
					Toast.makeText(
							getBaseContext(),
							selected_series + " on "  + str_date + " : " + amount ,
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Adding the Line Chart to the LinearLayout
		chartContainer.addView(mChart);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}

}
