RefreshActionItem
=================

An action item for [ActionBarSherlock][1] that implements the following common pattern: 

* Initially a refresh button is shown.
* When the button is clicked, a background operation begins and the button turns into a progress indicator.
* When the background operation ends, the button is restored to its initial state.

The progress bar displays how far the operation has proceeded. It is possible to choose between two styles: "wheel" and "pie". 

The progress bar can also be made indeterminate, just like the built-in <tt>ProgressBar</tt>.

The refresh button can be made initially invisible, which makes this action item behave like a replacement for the built-in indeterminate action bar progress indicator.
 
The action item also supports adding a small badge that indicates that there is new data available.

![Example Image][2]

Try out the sample application:

<a href="https://play.google.com/store/apps/details?id=com.manuelpeinado.refreshactionitem.demo">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or browse the [source code][3] of the sample application for a complete example of use.


Including in your project
-------------------------

If you’re using Eclipse with the ADT plugin you can include MultiChoiceAdaptar as a library project. Create a new Android project in Eclipse using the library/ folder as the existing source. Then, open the properties of this new project and, in the 'Android' category, add a reference to the ActionBarSherlock library project. Finally, in your application project properties, add a reference to the created library project.

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

The <tt>setRefreshActionListener</tt> method registers a callback that will be invoked when the refresh button is clicked. Start your background process from this callback and change the display mode of the action item so that it shows a progress indicator:


    @Override
    public void onRefreshButtonClick(RefreshActionItem sender) {
        mRefreshActionItem.setDisplayMode(RefreshActionItem.PROGRESS);
        startBackgroundTask();
    }

From your background task, call the action item's <tt>setProgress(int)</tt> method each time some progress is made:

    mRefreshActionItem.setProgress(progress);
    
If the progress of your background task cannot be easily measured you might prefer to use an indeterminate progress indicator. Just pass <tt>RefreshActionItem.INDETERMINATE</tt> to <tt>setDisplayMode()</tt>.

Finally, when the background task is complete restore the action item to its original state:

    mRefreshActionItem.setDisplayMode(RefreshActionItem.BUTTON);

### Badges

Sometimes it is useful to give the user a visual hint suggesting that there is new data to be loaded. You can easily achieve this by adding a badge to your action item:

    mRefreshActionItem.showBadge();

The badge shows an exclamation mark by default, but you can specify an alternative text if you desire.

### Hidden mode

If the data refresh cycle of your application is controlled internally and not directly by the user, it might not make sense to show a refresh action in your action bar. This won't prevent you from using the library, though: just set the display mode <tt>RefreshActionItem.HIDDEN</tt> in <tt>onCreateOptionsMenu()</tt>.

Customization
---------------------

You can use a **custom background** (drawable or color) for the selected items of your list. To do so, add an item named <code>multiChoiceAdapterStyle</code> to your theme, and have it reference an additional style which you define like this:

    <style name="MyCustomMultiChoiceAdapter">
        <item name="selectedItemBackground">@color/my_custom_selected_item_background</item>
    </style>

See the sample application for a complete example.

You can also customize the way the adapter behaves when an item is clicked and **the action mode was already active**. Just add the following item to your style:

    <style name="MyCustomMultiChoiceAdapter">
        <item name="itemClickInActionMode">selectItem</item>
    </style>
    
Two values are supported:

* <tt>selectItem</tt>. Changes the selection state of the clicked item, just as if it had been long clicked. This is what the native MULTICHOICE_MODAL mode of List does, and what almost every app does, and thus the default value.
* <tt>openItem</tt>. Opens the clicked item, just as if it had been clicked outside of the action mode. This is what the native Gmail app does.


Libraries used
--------------------

* The library includes some code (mainly XML dimension resources) extracted from the ActionBarSherlock library.
* Badges are implemented using the android-viewbadger library by Jeff Gilfelt.
* The sample app uses the ActionBarSherlock library by Jake Wharton.

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
