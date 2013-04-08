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
import com.readystatesoftware.viewbadger.BadgeView;

public class RefreshActionItem extends FrameLayout implements OnClickListener, OnLongClickListener {
    
    // Display modes
    public static final int BUTTON = 0;
    public static final int DETERMINATE = 1;
    public static final int INDETERMINATE = 2;
    public static final int HIDDEN = 3;
    // Determinate progress indicator styles
    public static final int DOUGHNUT = ProgressIndicator.STYLE_DOUGHNUT;
    public static final int PIE = ProgressIndicator.STYLE_PIE;
    
    private ImageView mRefreshButton;
    private ProgressBar mProgressIndicatorIndeterminate;
    private ProgressIndicator mProgressIndicatorDeterminate;
    private int mMax = 100;
    private int mProgress;
    private int mDisplayMode = BUTTON;
    private RefreshActionListener mRefreshButtonListener;
    private MenuItem mMenuItem;
    private BadgeView mBadge;
    private int mBadgeBackgroundColor = -1;
    private int mBadgeTextStyle;
    private int mBadgePosition;
    
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
                case R.styleable.RefreshActionItem_badgeBackgroundColor:
                    mBadgeBackgroundColor = a.getColor(R.styleable.RefreshActionItem_badgeBackgroundColor, -1);
                    break;
                case R.styleable.RefreshActionItem_badgeTextAppearance:
                    mBadgeTextStyle = a.getResourceId(R.styleable.RefreshActionItem_badgeTextAppearance, 0);
                    break;
                case R.styleable.RefreshActionItem_badgePosition:
                    mBadgePosition = a.getInt(R.styleable.RefreshActionItem_badgePosition, 0);
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
    
    /**
     * Adds an exclamation icon to the refresh button. This is intended to suggest
     * the user that new data is available.
     * <p>The badge is only shown in the {@link BUTTON} display mode
     * <p>If the display mode is not <tt>BUTTON</tt>, nothing is done.
     * @see #showBadge(String)
     */
    public void showBadge() {
        showBadge("!");
    }

    /**
     * Adds a badge icon with a give text to the refresh button. This is intended to suggest
     * the user that new data is available, including how many items
     * <p>The badge is only shown in the {@link BUTTON} display mode
     * <p>If the display mode is not <tt>BUTTON</tt>, nothing is done.
     * @param text Text that is drawn inside the badge icon
     * @see #showBadge() 
     */
    public void showBadge(String text) {
        hideBadge();
        mBadge = new BadgeView(getContext(), mRefreshButton);
        mBadge.setBadgePosition(mBadgePosition);
        if (mBadgeTextStyle != 0) {
            mBadge.setTextAppearance(getContext(), mBadgeTextStyle);
        }
        if (mBadgeBackgroundColor != -1) {
            mBadge.setBadgeBackgroundColor(mBadgeBackgroundColor);
        }
        mBadge.setText(text);
        if (mDisplayMode == BUTTON) {
            // Otherwise the badge won't be shown until be return to BUTTON mode
            mBadge.show(true);
        }
    }
    
    /**
     * Hides the badge associated to this action item.
     * <p>If the display mode is not <tt>BUTTON</tt> or the badge is not visible, nothing is done.
     * @see #showBadge()
     * @see #showBadge(String) 
     * @see #isBadgeVisible()
     */
    public void hideBadge() {
        if (mBadge == null) {
            return;
        }
        mBadge.hide(true);
        mBadge = null;
    }
    
    /**
     * Returns whether this action item has a visible badge.
     * @see #showBadge()
     * @see #showBadge(String) 
     * @see #hideBadge()
     */
    public boolean isBadgeVisible() {
        return mBadge != null;
    }

    private void updateChildrenVisibility() {
        switch (mDisplayMode) {
        case BUTTON:
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressIndicatorIndeterminate.setVisibility(View.GONE);
            mProgressIndicatorDeterminate.setVisibility(View.GONE);
            break;
        case INDETERMINATE:
            mRefreshButton.setVisibility(View.GONE);
            mProgressIndicatorIndeterminate.setVisibility(View.VISIBLE);
            mProgressIndicatorDeterminate.setVisibility(View.GONE);
            updateProgressIndicatorValue();
            break;
        case DETERMINATE:
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
     * @param mode One of {@link RefreshActionItem#BUTTON}, {@link RefreshActionItem#DETERMINATE} or {@link RefreshActionItem#INDETERMINATE}
     */
    public void setDisplayMode(int mode, boolean resetProgress) {
        if (mBadge != null) {
            if (mode != BUTTON) {
                // Hide badge temporarily until we return to BUTTON mode 
                mBadge.hide(false);
            }
            else {
                // If badge was hidden temporarily we restore it back to visible 
                mBadge.show(false);
            }
        }
        if (resetProgress) {
            setProgress(0);
        }
        if (mDisplayMode == mode) {
            return;
        }
        mDisplayMode = mode;
        setVisibility(mode == HIDDEN ? GONE : VISIBLE);
        if (mode != HIDDEN) {
            updateChildrenVisibility();
        }
    }


    /**
     * Return the display mode of this progress bar 
     * @return One of {@link DisplayMode#BUTTON}, {@link DisplayMode#DETERMINATE} or {@link DisplayMode#INDETERMINATE}
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
