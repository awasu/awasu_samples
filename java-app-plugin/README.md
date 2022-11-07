# java-app-plugin

This is a more sophisticated example of an application plugin that lets the user subscribe to multiple channels. It generates RSS feeds by querying a database and returning the rows found as a feed.

Run the `COMPILE.BAT` script to compile the plugin, then copy the following files to Awasu's `AppPlugins/` directory:
* `SampleJavaAppPlugin.jar`
* `SampleJavaAppPlugin.plugin`
* `sqlite-jdbc-3.39.3.0.jar `
* `demo.db`

Restart Awasu, then open the plugin from the *Plugins* tab of the Control Center.
