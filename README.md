RefreshActionItem
=================

An action bar item that implements this common pattern:

* Initially it shows a refresh button.
* If the button is clicked, a background operation begins and the button turns into a progress indicator.
* When the background operation ends, the button is restored to its initial state.

The progress bar shows a magnitude which represents how far the operation has proceeded. The progress bar can also be made indeterminate, just like the built-in <tt>ProgressBar</tt>.
 
It is possible to add a small badge to the action item. This tells the user that there is new data available.

This library requires [ActionBarSherlock][1], and is thus compatible with Android 2.x and newer. If you don't need 2.x compatibility and thus use the native action bar, check out [this fork][2] of the library.

![Example Image][3]

Try out the sample application:

<a href="https://play.google.com/store/apps/details?id=com.manuelpeinado.refreshactionitem.demo">
  <img alt="Android app on Google Play"
       src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

Or browse the [source code][4] of the sample application for a complete example of use.


Including in your project
-------------------------

If you’re using Eclipse with the ADT plugin you can include RefreshActionItem as a library project. Create a new Android project in Eclipse using the library/ folder as the existing source. Then, open the properties of this new project and, in the 'Android' category, add a reference to the ActionBarSherlock library project. Finally, in your application project properties, add a reference to the created library project.

If you use maven to build your Android project you can simply add a dependency for this library.

```xml
 <dependency>
     <groupId>com.github.manuelpeinado.refreshactionitem</groupId>
     <artifactId>library</artifactId>
     <version>1.0.3</version>
     <type>apklib</type>
 </dependency>
```

Usage
-----

Add an element for the refresh action to your XML menu:

```xml
<item
    android:id="@+id/refresh_button"          
    android:actionViewClass=
        "com.manuelpeinado.refreshactionitem.RefreshActionItem"
    android:showAsAction="always"
    android:title="@string/action_refresh"/>
```

Then, configure the action in the <tt>onCreateOptionsMenu</tt> method of your <tt>SherlockActivity</tt>-derived activity:

```java
@Override public boolean onCreateOptionsMenu(Menu menu) {
    getSupportMenuInflater().inflate(R.menu.main, menu);
    MenuItem item = menu.findItem(R.id.refresh_button);
    mRefreshActionItem = (RefreshActionItem) item.getActionView();
    mRefreshActionItem.setMenuItem(item);
    mRefreshActionItem.setMax(100);
    mRefreshActionItem.setRefreshActionListener(this);
    return true;
}
```

The <tt>setRefreshActionListener</tt> method registers a callback that will be invoked when the refresh button is clicked. Start your background process from this callback and invoke <tt>showProgress(true)</tt> on the action item so that it turns into a progress indicator:

```java
@Override
public void onRefreshButtonClick(RefreshActionItem sender) {
    mRefreshActionItem.showProgress(true);
    startBackgroundTask();
}
```

From your background task, call the action item's <tt>setProgress(int)</tt> method each time some progress is made:

```java
mRefreshActionItem.setProgress(progress);
```
    
Finally, when the background task is complete restore the action item to its original state:

```java
mRefreshActionItem.showProgress(false);
```

### Progress indicator types

By default the action item shows the amount of progress using a wheel. There is an additional style, "pie", which you can activate by calling <tt>setProgressIndicatorType(ProgressIndicatorType.PIE)</tt> on your action item.

Also, if the progress of your background task cannot be easily measured you might prefer to use an indeterminate progress indicator. To achieve this just pass <tt>ProgressIndicatorType.INDETERMINATE</tt> to <tt>setProgressIndicatorType()</tt>.

### Badges

Sometimes it is useful to give the user a visual hint suggesting that there is new data to be loaded. You can easily achieve this by adding a badge to your action item:

```java
mRefreshActionItem.showBadge();
```

The badge shows an exclamation mark by default, but you can specify an alternative text if you desire.


Customization
---------------------
You can easily customize the appearance of your RefreshActionItems. Just define a <tt>refreshActionItemStyle</tt> attribute in your theme and make it reference a custom style where you specify new values for any of the multiple attributes recognized by the library.

The following snippet is extracted from the accompanying sample application. To see it in action open the "Styling" demo in the main menu.

```xml
<style name="AppTheme" parent="Theme.Sherlock.Light">
    <item name="refreshActionItemStyle">@style/CustomRefreshActionItem</item>
</style>

<style name="CustomRefreshActionItem" parent="Widget.RefreshActionItem.Light">
    <item name="progressIndicatorType">pie</item>
    <item name="badgeBackgroundColor">#A4F4</item>
    <item name="badgeTextStyle">@style/BadgeText</item>
    <item name="badgePosition">bottomLeft</item>
</style>

<style name="BadgeText">
    <item name="android:textSize">14dp</item>
    <item name="android:textColor">#7000</item>
</style>
```

Libraries used
--------------------

* [ActionBarSherlock][1] by Jake Wharton.
* [android-viewbadger][5] by Jeff Gilfelt.

Credits
-------

* Cake launcher icon by [IconEden][6].

Who's using it
--------------

* [The New York Times][7]. Experience the world’s finest journalism with The New York Times app for Android.
* [Signos Fodas][8]. With this app you can follow all horoscope signs, updated daily in real time on your Android (Portuguese only).

*Does your app use RefreshActionItem? If you want to be featured on this list drop me a line.*

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




 [1]: http://actionbarsherlock.com
 [2]: https://github.com/ManuelPeinado/RefreshActionItem-Native
 [3]: https://raw.github.com/ManuelPeinado/RefreshActionItem/master/art/readme_pic.png
 [4]: https://github.com/ManuelPeinado/RefreshActionItem/tree/master/sample
 [5]: https://github.com/jgilfelt/android-viewbadger
 [6]: http://www.iconeden.com
 [7]: https://play.google.com/store/apps/details?id=com.nytimes.android
 [8]: https://play.google.com/store/apps/details?id=com.contralabs.app.horoscoposignos
