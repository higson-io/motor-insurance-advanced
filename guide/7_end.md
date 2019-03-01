<h3>End result</h3>
<p>This is how end product of this tutorial should looks like.
</p>
<h4>Application</h4>
<p>
Screen of main page after it is accessed for the first time. As you can see there are some default values setup.
</p>
<img src="main_page.png">
This is the only view of an application, that is available. All data might be modified and recalculation happens only on few actions, like changing gender or number of accidents.
<h4>Application database</h4>
<p>
Now let's examine application database structure and main bundle root. In this project it is table called <i>PERSISTENCE_BUNDLE</i>
</p>
<img src="h2_bundle.png">
<p>Application with modified data</p>
<img src="main_page_changes.png">
<p>In database, there should be only one entry for the same bundle. Bundle will update it's revision, update column and value(JSON) after recalculation.</p>
<img src="h2_bundle_change.png">
