RefreshActionItem
=================

An action bar item that implements this common pattern:

* Initially it shows a refresh button.
* If the button is clicked, a background operation begins and the button turns into a progress indicator.
* When the background operation ends, the button is restored to its initial state.

The progress bar shows a magnitude which represents how far the operation has proceeded. The progress bar can also be made indeterminate, just like the built-in <tt>ProgressBar</tt>.
 
It is possible to add a small badge to the action item. This tells the user that there is new data available.

This library requires [ActionBarSherlock][1], and is thus compatible with Android 2.x and newer.

![Example Image][2]

Try out the sample application:

<a href="https://play.google.com/store/apps/details?id=com.manuelpeinado.refreshactionitem.demo">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or browse the [source code][3] of the sample application for a complete example of use.


Including in your project
-------------------------

If you’re using Eclipse with the ADT plugin you can include RefreshActionItem as a library project. Create a new Android project in Eclipse using the library/ folder as the existing source. Then, open the properties of this new project and, in the 'Android' category, add a reference to the ActionBarSherlock library project. Finally, in your application project properties, add a reference to the created library project.

If you use maven to build your Android project you can simply add a dependency for this library.

    <dependency>
        <groupId>com.github.manuelpeinado.refreshactionitem</groupId>
        <artifactId>library</artifactId>
        <version>1.0.0</version>
        <type>apklib</type>
    </dependency>

Usage
-----

Add an element for the refresh action to your XML menu:

    <item
        android:id="@+id/refresh_button"          
        android:actionViewClass=
            "com.manuelpeinado.refreshactionitem.RefreshActionItem"
        android:showAsAction="always"
        android:title="@string/action_refresh"/>

Then, configure the action in the <tt>onCreateOptionsMenu</tt> method of your <tt>SherlockActivity</tt>-derived activity:

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.refresh_button);
        mRefreshActionItem = (RefreshActionItem) item.getActionView();
        mRefreshActionItem.setMenuItem(item);
        mRefreshActionItem.setMax(100);
        mRefreshActionItem.setRefreshActionListener(this);
        loadData();
        return true;
    }

The <tt>setRefreshActionListener</tt> method registers a callback that will be invoked when the refresh button is clicked. Start your background process from this callback and invoke <tt>showProgress(true)</tt> on the action item so that it turns into a progress indicator:


    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        mRefreshActionItem.showProgress(true);
        startBackgroundTask();
    }

From your background task, call the action item's <tt>setProgress(int)</tt> method each time some progress is made:

    mRefreshActionItem.setProgress(progress);
    
Finally, when the background task is complete restore the action item to its original state:

    mRefreshActionItem.showProgress(false);

### Progress indicator types

By default the action item shows the amount of progress using a wheel. There is an additional style, "pie", which you can activate by calling <tt>setProgressIndicatorType(ProgressIndicatorType.PIE</tt> on your action item.

Also, if the progress of your background task cannot be easily measured you might prefer to use an indeterminate progress indicator. To achieve this just pass <tt>setProgressIndicatorType(ProgressIndicatorType.INDETERMINATE)</tt> to your action item.

### Badges

Sometimes it is useful to give the user a visual hint suggesting that there is new data to be loaded. You can easily achieve this by adding a badge to your action item:

    mRefreshActionItem.showBadge();

The badge shows an exclamation mark by default, but you can specify an alternative text if you desire.


Customization
---------------------
You can easily customize the appearance of your RefreshActionItems. Just define a <tt>refreshActionItemStyle</tt> attribute in your theme and make it reference a custom style where you can specify new values for any of the multiple attributes recognized by the library.

See the source of the "Styled" activity in the accompanying sample application for a complete working example of this.

Libraries used
--------------------

* [ActionBarSherlock][1] by Jake Wharton.
* [android-viewbadger][5] by Jeff Gilfelt.

Developed By
--------------------

Manuel Peinado Gallego - <manuel.peinado@gmail.com>

<a href="https://twitter.com/mpg2">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/twitter.png" />
</a>
<a href="https://plus.google.com/106514622630861903655">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/google-plus.png" />
</a>
<a href="http://www.linkedin.com/pub/manuel-peinado-gallego/1b/435/685">
  <img alt="Follow me on Twitter"
       src="https://raw.github.com/ManuelPeinado/NumericPageIndicator/master/art/linkedin.png" />

License
-----------

    Copyright 2013 Manuel Peinado

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




 [1]: http://actionbarshelock.com
 [2]: https://raw.github.com/ManuelPeinado/RefreshActionItem/master/art/readme_pic.png
 [3]: https://github.com/ManuelPeinado/RefreshActionItem/tree/master/sample
 [4]: https://github.com/TimotheeJeannin/ProviGen
 [5]: https://github.com/jgilfelt/android-viewbadger