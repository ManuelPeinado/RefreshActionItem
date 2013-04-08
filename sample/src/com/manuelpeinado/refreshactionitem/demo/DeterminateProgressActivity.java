package com.manuelpeinado.refreshactionitem.demo;

import java.util.Random;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.demo.R;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;

public class DeterminateProgressActivity extends SherlockListActivity implements RefreshActionListener {
    private RefreshActionItem mRefreshActionItem;
    private Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_determinate_progress);
    }

    private void loadData() {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.DETERMINATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; ++i) {
                    try {
                        Thread.sleep(20);
                        mRefreshActionItem.setProgress(i);
                    } catch (InterruptedException e) {
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshActionItem.setDisplayMode(RefreshActionItem.BUTTON);
                        String[] items = generateRandomItemList();
                        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items));
                    }
                });
            }

        }).start();
    }

    private String[] generateRandomItemList() {
        String[] result = new String[100];
        for (int i = 0; i < result.length; ++i) {
            result[i] = Integer.toString(r.nextInt(1000));
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.refresh, menu);
        MenuItem item = menu.findItem(R.id.refresh_button);
        mRefreshActionItem = (RefreshActionItem) item.getActionView();
        mRefreshActionItem.setMenuItem(item);
        mRefreshActionItem.setMax(100);
        mRefreshActionItem.setRefreshActionListener(this);
        loadData();
        return true;
    }

    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        loadData();
    }

    public void setDoughnutStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.DOUGHNUT);
    }

    public void setPieStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.PIE);
    }
}
