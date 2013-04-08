/*
 * Copyright (C) 2013 Manuel Peinado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manuelpeinado.refreshactionitem.demo;

import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.demo.R;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;

public class NoButtonActivity extends SherlockListActivity {
    private RefreshActionItem mRefreshActionItem;
    private Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        mRefreshActionItem.setDisplayMode(RefreshActionItem.GONE);
        load(null);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent parentActivityIntent = new Intent(this, HomeActivity.class);
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;
        }
        return false;
    }

    public void load(View view) {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.DETERMINATE, true);
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
                        mRefreshActionItem.setDisplayMode(RefreshActionItem.GONE);
                        String[] items = generateRandomItemList();
                        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items));
                    }
                });
            }
        }).start();
    }

    public void setDoughnutStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.DOUGHNUT);
    }

    public void setPieStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.PIE);
    }
}
