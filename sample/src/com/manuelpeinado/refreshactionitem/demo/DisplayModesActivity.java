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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.demo.R;
import com.manuelpeinado.refreshactionitem.RefreshActionItem;

public class DisplayModesActivity extends SherlockActivity {

    private RefreshActionItem mRefreshActionItem;
    private ViewGroup mDeterminateButtons1;
    private ViewGroup mDeterminateButtons2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_modes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDeterminateButtons1 = (ViewGroup)findViewById(R.id.determinate_buttons_1);
        mDeterminateButtons2 = (ViewGroup)findViewById(R.id.determinate_buttons_2);
        hideDeterminateButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.refresh, menu);
        MenuItem item = menu.findItem(R.id.refresh_button);
        mRefreshActionItem = (RefreshActionItem)item.getActionView();
        mRefreshActionItem.setMenuItem(item);
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
    
    public void showButton(View view) {
        if (mRefreshActionItem.isBadgeVisible()) {
            mRefreshActionItem.hideBadge();
        }
        else {
            mRefreshActionItem.showBadge();
        }

        mRefreshActionItem.setDisplayMode(RefreshActionItem.BUTTON);
        hideDeterminateButtons();
    }

    private void hideDeterminateButtons() {
        mDeterminateButtons1.setVisibility(View.GONE);
        mDeterminateButtons2.setVisibility(View.GONE);
    }

    public void showIndeterminate(View view) {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.INDETERMINATE);
        hideDeterminateButtons();
    }
    
    public void showDeterminate(View view) {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.DETERMINATE);
        mDeterminateButtons1.setVisibility(View.VISIBLE);
        mDeterminateButtons2.setVisibility(View.VISIBLE);
    }
    
    public void incrementProgress(View view) {
        mRefreshActionItem.incrementProgressBy(10);
    }

    public void decrementProgress(View view) {
        mRefreshActionItem.incrementProgressBy(-10);
    }
    
    public void setDoughnutStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.DOUGHNUT);
    }

    public void setPieStyle(View view) {
        mRefreshActionItem.setDeterminateIndicatorStyle(RefreshActionItem.PIE);
    }
}
