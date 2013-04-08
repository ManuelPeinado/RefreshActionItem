package com.manuelpeinado.refreshactionitem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.manuelpeinado.refreshactionitem.R;

public class RefreshActionItem extends FrameLayout implements OnClickListener, OnLongClickListener {
    public static final int MODE_BUTTON = 0;
    public static final int MODE_DETERMINATE = 1;
    public static final int MODE_INDETERMINATE = 2;
    public static final int MODE_GONE = 3;
    public static final int STYLE_DOUGHNUT = ProgressIndicator.STYLE_DOUGHNUT;
    public static final int STYLE_PIE = ProgressIndicator.STYLE_PIE;
    private ImageView mRefreshButton;
    private ProgressBar mProgressIndicatorIndeterminate;
    private ProgressIndicator mProgressIndicatorDeterminate;
    private int mMax = 100;
    private int mProgress;
    private int mDisplayMode = MODE_BUTTON;
    private RefreshActionListener mRefreshButtonListener;
    private MenuItem mMenuItem;
    
    public interface RefreshActionListener {
        void onRefreshButtonClick(RefreshActionItem sender);
    }
    
    public RefreshActionItem(Context context) {
        this(context, null);
    }

    public RefreshActionItem(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.refreshActionStyle);
    }

    @SuppressWarnings("deprecation")
    public RefreshActionItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.rai__view_refresh_action_item, this);
        mRefreshButton = (ImageView)findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(this);
        mRefreshButton.setOnLongClickListener(this);
        mProgressIndicatorIndeterminate = (ProgressBar)findViewById(R.id.indeterminate_progress_indicator);
        mProgressIndicatorDeterminate = (ProgressIndicator)findViewById(R.id.determinate_progress_indicator);
        updateChildrenVisibility();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshActionItem, 
                                                      defStyle, R.style.Widget_RefreshActionItem_Dark);
        int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RefreshActionItem_refreshActionItemIcon:
                    Drawable refreshButtonIcon = a.getDrawable(R.styleable.RefreshActionItem_refreshActionItemIcon);
                    mRefreshButton.setImageDrawable(refreshButtonIcon);
                    break;
                case R.styleable.RefreshActionItem_progressIndicatorForegroundColor:
                    int color = a.getColor(R.styleable.RefreshActionItem_progressIndicatorForegroundColor, 0);
                    mProgressIndicatorDeterminate.setForegroundColor(color);
                    break;
                case R.styleable.RefreshActionItem_progressIndicatorBackgroundColor:
                    color = a.getColor(R.styleable.RefreshActionItem_progressIndicatorBackgroundColor, 0);
                    mProgressIndicatorDeterminate.setBackgroundColor(color);
                    break;
                case R.styleable.RefreshActionItem_refreshActionItemBackground:
                    Drawable drawable = a.getDrawable(R.styleable.RefreshActionItem_refreshActionItemBackground);
                    mRefreshButton.setBackgroundDrawable(drawable);
                    break;
            }
        }        
        a.recycle();
    }
    
    public void setRefreshActionListener(RefreshActionListener listener) {
        this.mRefreshButtonListener = listener;
    }
    
    public void setMenuItem(MenuItem menuItem) {
        this.mMenuItem = menuItem;
    }

    private void updateChildrenVisibility() {
        switch (mDisplayMode) {
        case MODE_BUTTON:
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressIndicatorIndeterminate.setVisibility(View.GONE);
            mProgressIndicatorDeterminate.setVisibility(View.GONE);
            break;
        case MODE_INDETERMINATE:
            mRefreshButton.setVisibility(View.GONE);
            mProgressIndicatorIndeterminate.setVisibility(View.VISIBLE);
            mProgressIndicatorDeterminate.setVisibility(View.GONE);
            updateProgressIndicatorValue();
            break;
        case MODE_DETERMINATE:
            mRefreshButton.setVisibility(View.GONE);
            mProgressIndicatorIndeterminate.setVisibility(View.GONE);
            mProgressIndicatorDeterminate.setVisibility(View.VISIBLE);
            updateProgressIndicatorValue();
            break;
        }
    }

    
    /**
     * Calls {@link #setDisplayMode(int, boolean) passing <tt>false</tt> as the second parameter 
     */
    public void setDisplayMode(int mode) {
        setDisplayMode(mode, false);
    }
    
    /**
     * Change the display mode of this progress bar. Supported modes are "refresh button",
     * "determinate progress" and "indeterminate progress"
     * @param mode One of {@link RefreshActionItem#MODE_BUTTON}, {@link RefreshActionItem#MODE_DETERMINATE} or {@link RefreshActionItem#MODE_INDETERMINATE}
     */
    public void setDisplayMode(int mode, boolean resetProgress) {
        if (resetProgress) {
            setProgress(0);
        }
        if (mDisplayMode == mode) {
            return;
        }
        mDisplayMode = mode;
        setVisibility(mode == MODE_GONE ? GONE : VISIBLE);
        if (mode != MODE_GONE) {
            updateChildrenVisibility();
        }
    }


    /**
     * Return the display mode of this progress bar 
     * @return One of {@link DisplayMode#MODE_BUTTON}, {@link DisplayMode#MODE_DETERMINATE} or {@link DisplayMode#MODE_INDETERMINATE}
     */
    public int getDisplayMode() {
        return mDisplayMode;
    }

    /**
     * Return the upper limit of this progress bar's range.
     * @return a positive integer
     * @see #setMax(int)
     * @see #getProgress()
     */
    public synchronized int getMax() {
        return mMax;
    }
    
    /**
     * Set the current progress to the specified value. If the progress bar is not in 
     * determinate mode the view is not changed.
     * @param progress the new progress, between 0 and {@link #getMax()}
     * @see #setDisplayMode(DisplayMode)
     * @see #getMode()
     * @see #getProgress()
     * @see #incrementProgressBy(int) 
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress != mProgress) {
            mProgress = progress;
            updateProgressIndicatorValue();
        }
    }
    
    private void updateProgressIndicatorValue() {
        mProgressIndicatorDeterminate.setValue(mProgress / (float)mMax);
    }

    /**
     * Increase the progress bar's progress by the specified amount.
     * @param diff the amount by which the progress must be increased
     * @see #setProgress(int) 
     */
    public synchronized final void incrementProgressBy(int diff) {
        setProgress(mProgress + diff);
    }
    
    /**
     * Set the range of the progress bar to 0...<tt>max</tt>
     * @param max the upper range of this progress bar
     * @see #getMax()
     * @see #setProgress(int) 
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            max = 0;
        }
        if (max != mMax) {
            mMax = max;
            if (mProgress > mMax) {
                mProgress = mMax;
            }
            updateProgressIndicatorValue();
        }
    }

    public void setDeterminateIndicatorStyle(int style) {
        mProgressIndicatorDeterminate.setStyle(style);
    }
    
    public void getDeterminateIndicatorStyle() {
        mProgressIndicatorDeterminate.getStyle();
    }
    
    @Override
    public void onClick(View v) {
        if (mRefreshButtonListener != null) {
            mRefreshButtonListener.onRefreshButtonClick(this);
        }
    }
    
    @Override
    public boolean onLongClick(View v) {
        if (mMenuItem == null || TextUtils.isEmpty(mMenuItem.getTitle())) {
            return true;
        }
        final int[] screenPos = new int[2];
        final Rect displayFrame = new Rect();
        getLocationOnScreen(screenPos);
        getWindowVisibleDisplayFrame(displayFrame);
        final Context context = getContext();
        final int width = getWidth();
        final int height = getHeight();
        final int midy = screenPos[1] + height / 2;
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        Toast cheatSheet = Toast.makeText(context, mMenuItem.getTitle(), Toast.LENGTH_SHORT);
        if (midy < displayFrame.height()) {
            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT,
                                  screenWidth - screenPos[0] - width / 2, height);
        } else {
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        }
        cheatSheet.show();
        return true;
    }
}
