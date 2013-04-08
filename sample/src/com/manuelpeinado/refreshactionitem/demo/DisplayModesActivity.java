package com.manuelpeinado.refreshactionitem.demo;

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
