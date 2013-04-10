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
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;
import com.manuelpeinado.refreshactionitem.RefreshActionItem.RefreshActionListener;

public class StyledActivity extends SherlockListActivity implements RefreshActionListener {
    private RefreshActionItem mSaveButton;
    private Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_styled);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.save, menu);
        MenuItem item = menu.findItem(R.id.save_button);
        mSaveButton = (RefreshActionItem) item.getActionView();
        mSaveButton.setMenuItem(item);
        mSaveButton.setMax(100);
        mSaveButton.setRefreshActionListener(this);
        String[] items = generateRandomItemList();
        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item, android.R.id.text1, items));
        return true;
    }

    private void saveData() {
        mSaveButton.showProgress(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; ++i) {
                    try {
                        Thread.sleep(20);
                        mSaveButton.setProgress(i);
                    } catch (InterruptedException e) {
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSaveButton.showProgress(false);
                        Toast.makeText(getApplicationContext(), "Your data has been saved", Toast.LENGTH_SHORT).show();
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
        saveData();
    }

    public void showBadge(View view) {
        if (mSaveButton.isBadgeVisible()) {
            mSaveButton.hideBadge();
        }
        else {
            mSaveButton.showBadge("5");
        }
    }
}
