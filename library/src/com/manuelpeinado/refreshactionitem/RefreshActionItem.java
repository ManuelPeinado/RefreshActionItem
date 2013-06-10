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
package com.manuelpeinado.refreshactionitem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
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

/**
 * An action bar item implementing a common pattern: initially a refresh button
 * is shown, and when the button is clicked a background operation begins and
 * the button turns into a progress indicator until the operation ends, at which
 * point the button is restored to its initial state.
 * <p>
 * The progress indicator can be determinate or indeterminate. If the
 * determinate mode is used, it is possible to choose between two styles:
 * "wheel" and "pie".
 * <p>
 * It is also possible to have the refresh button be invisible initially, which
 * makes this action item behave like a replacement for the built-in
 * indeterminate action bar progress indicator (with the benefit that with this
 * action item the progress can be determinate).
 * <p>
 * The action item also supports adding a small badge that indicates that there
 * is new data available.
 */
public class RefreshActionItem extends FrameLayout implements OnClickListener, OnLongClickListener {
    private ImageView mRefreshButton;
    private ProgressBar mProgressIndicatorIndeterminate;
    private ProgressIndicator mProgressIndicator;
    private RefreshActionListener mRefreshButtonListener;
    private BadgeView mBadge;
    private int mBadgeBackgroundColor = -1;
    private int mBadgeTextStyle;
    private int mBadgePosition;
    // Please note that the state can be "showing progress" and "showing badge" simultaneously, in that case
    // the badge remains hidden until we stop showing progress
    private boolean mShowingBadge;
    private MenuItem mMenuItem;
    private boolean mShowingProgress;
    private int mMax = 100;
    private int mProgress = 0;
    private ProgressIndicatorType mProgressIndicatorType;

    public interface RefreshActionListener {
        void onRefreshButtonClick(RefreshActionItem sender);
    }

    public RefreshActionItem(Context context) {
        this(context, null);
    }

    public RefreshActionItem(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.refreshActionItemStyle);
    }

    @SuppressWarnings("deprecation")
    public RefreshActionItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.rai__action_item, this);
        mRefreshButton = (ImageView) findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(this);
        mRefreshButton.setOnLongClickListener(this);
        mProgressIndicatorIndeterminate = (ProgressBar) findViewById(R.id.indeterminate_progress_indicator);
        mProgressIndicator = (ProgressIndicator) findViewById(R.id.determinate_progress_indicator);
        updateChildrenVisibility();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RefreshActionItem, defStyle, R.style.Widget_RefreshActionItem_Dark);
        int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.RefreshActionItem_progressIndicatorType:
                mProgressIndicatorType = ProgressIndicatorType.values()[a.getInt(attr, 0)];
                if (mProgressIndicatorType == ProgressIndicatorType.PIE) {
                    mProgressIndicator.setPieStyle(true);
                }
                break;
            case R.styleable.RefreshActionItem_refreshActionItemIcon:
                Drawable refreshButtonIcon = a.getDrawable(attr);
                mRefreshButton.setImageDrawable(refreshButtonIcon);
                break;
            case R.styleable.RefreshActionItem_progressIndicatorForegroundColor:
                int color = a.getColor(attr, 0);
                mProgressIndicator.setForegroundColor(color);
                break;
            case R.styleable.RefreshActionItem_progressIndicatorBackgroundColor:
                color = a.getColor(attr, 0);
                mProgressIndicator.setBackgroundColor(color);
                break;
            case R.styleable.RefreshActionItem_refreshActionItemBackground:
                Drawable drawable = a.getDrawable(attr);
                mRefreshButton.setBackgroundDrawable(drawable);
                break;
            case R.styleable.RefreshActionItem_badgeBackgroundColor:
                mBadgeBackgroundColor = a.getColor(attr, -1);
                break;
            case R.styleable.RefreshActionItem_badgeTextStyle:
                mBadgeTextStyle = a.getResourceId(attr, 0);
                break;
            case R.styleable.RefreshActionItem_badgePosition:
                mBadgePosition = a.getInt(attr, 0);
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
        if (menuItem.getIcon() != null) {
            mRefreshButton.setImageDrawable(mMenuItem.getIcon());
        }
    }

    /**
     * Adds an exclamation icon to the refresh button. This is intended to
     * suggest the user that new data is available.
     * <p>
     * The badge is only shown in the {@link BUTTON} display mode
     * <p>
     * If the display mode is not <tt>BUTTON</tt>, nothing is done.
     * 
     * @see #showBadge(String)
     */
    public void showBadge() {
        showBadge("!");
    }

    /**
     * Adds a badge icon with a give text to the refresh button. This is
     * intended to suggest the user that new data is available, including how
     * many items
     * <p>
     * The badge is only shown in the {@link BUTTON} display mode
     * <p>
     * If the display mode is not <tt>BUTTON</tt>, nothing is done.
     * 
     * @param text
     *            Text that is drawn inside the badge icon
     * @see #showBadge()
     */
    public void showBadge(String text) {
        hideBadge();
        if (mBadge == null) {
            mBadge = new BadgeView(getContext(), mRefreshButton);
            mBadge.setBadgePosition(mBadgePosition);
            if (mBadgeTextStyle != 0) {
                mBadge.setTextAppearance(getContext(), mBadgeTextStyle);
            }
            if (mBadgeBackgroundColor != -1) {
                mBadge.setBadgeBackgroundColor(mBadgeBackgroundColor);
            }
        }
        mShowingBadge = true;
        mBadge.setText(text);
        if (!mShowingProgress) {
            // Otherwise the badge will be shown as soon as we stop showing progress
            mBadge.show(true);
        }
    }

    /**
     * Hides the badge associated to this action item.
     * <p>
     * If the display mode is not <tt>BUTTON</tt> or the badge is not visible,
     * nothing is done.
     * 
     * @see #showBadge()
     * @see #showBadge(String)
     * @see #isBadgeVisible()
     */
    public void hideBadge() {
        if (mBadge == null || !mShowingBadge) {
            return;
        }
        mShowingBadge = false;
        if (!mShowingProgress) {
            // If showing progress the badge is already hidden
            mBadge.hide(true);
        }
    }

    /**
     * Returns whether this action item has a visible badge.
     * @see #showBadge()
     * @see #showBadge(String)
     * @see #hideBadge()
     */
    public boolean isBadgeVisible() {
        return mBadge != null && mShowingBadge;
    }
    
    private void updateChildrenVisibility() {
        if (!mShowingProgress) {
            mRefreshButton.setVisibility(View.VISIBLE);
            mProgressIndicatorIndeterminate.setVisibility(View.GONE);
            mProgressIndicator.setVisibility(View.GONE);
            return;
        }
        if (mProgressIndicatorType == ProgressIndicatorType.INDETERMINATE) {
            mRefreshButton.setVisibility(View.GONE);
            mProgressIndicatorIndeterminate.setVisibility(View.VISIBLE);
            mProgressIndicator.setVisibility(View.GONE);
            updateProgressIndicatorValue();
            return;
        }
        mRefreshButton.setVisibility(View.GONE);
        mProgressIndicatorIndeterminate.setVisibility(View.GONE);
        mProgressIndicator.setVisibility(View.VISIBLE);
        updateProgressIndicatorValue();
    }

    /**
     * Return the upper limit of this progress bar's range.
     * 
     * @return a positive integer
     * @see #setMax(int)
     * @see #getProgress()
     */
    public synchronized int getMax() {
        return mMax;
    }

    /**
     * Changes the state of the action item between the modes "showing refresh button" and "showing progress"
     * @param show
     */
    public void showProgress(boolean show) {
        if (show == mShowingProgress) {
            return;
        }
        if (isBadgeVisible()) {
            if (show) {
                // Hide badge temporarily until we stop showing progress
                mBadge.hide(false);
            } else {
                // If badge was hidden temporarily we restore it back to visible
                mBadge.show(false);
            }
        }
        setProgress(0);
        mShowingProgress = show;
        updateChildrenVisibility();
    }

    /**
     * Set the current progress to the specified value. If the progress bar is
     * not in determinate mode the view is not changed.
     * 
     * @param progress
     *            the new progress, between 0 and {@link #getMax()}
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
        mProgressIndicator.setValue(mProgress / (float) mMax);
    }

    /**
     * Increase the progress bar's progress by the specified amount.
     * 
     * @param diff
     *            the amount by which the progress must be increased
     * @see #setProgress(int)
     */
    public synchronized final void incrementProgressBy(int diff) {
        setProgress(mProgress + diff);
    }
    
    /**
     * Set the range of the progress bar to 0...<tt>max</tt>
     * 
     * @param max
     *            the upper range of this progress bar
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

    /**
     * This has no effect if the action item has indeterminate progress
     * @param style One of {@link ProgressIndicatorType#WHEEL}, {@link ProgressIndicatorType#PIE}
     *              or {@link ProgressIndicatorType#INDETERMINATE}
     */
    public void setProgressIndicatorType(ProgressIndicatorType style) {
        if (style == mProgressIndicatorType) {
            return;
        }
        mProgressIndicatorType  = style;
        if (style == ProgressIndicatorType.PIE) {
            mProgressIndicator.setPieStyle(true);
        }
        else if (style == ProgressIndicatorType.WHEEL) {
            mProgressIndicator.setPieStyle(false);
        }
        updateChildrenVisibility();
    }

    public ProgressIndicatorType getProgressIndicatorType() {
        return mProgressIndicatorType;
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
            cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT, screenWidth - screenPos[0] - width / 2, height);
        } else {
            cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        }
        cheatSheet.show();
        return true;
    }
}
