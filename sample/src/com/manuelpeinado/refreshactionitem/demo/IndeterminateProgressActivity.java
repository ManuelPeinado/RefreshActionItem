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
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.demo.R;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;

public class IndeterminateProgressActivity extends SherlockListActivity implements RefreshActionListener {
    private RefreshActionItem mRefreshActionItem;
    private Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indeterminate_progress);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadData() {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.INDETERMINATE);
        new Thread(new Runnable() {
            @Override public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        mRefreshActionItem.setDisplayMode(RefreshActionItem.BUTTON);
                        String[] items = generateRandomItemList();
                        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items ));
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
        mRefreshActionItem = (RefreshActionItem)item.getActionView();
        mRefreshActionItem.setMenuItem(item);
        mRefreshActionItem.setRefreshActionListener(this);
        loadData();
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
    
    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        loadData();
    }
}
