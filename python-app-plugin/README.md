# python-app-plugin

This is an example of an application plugin. It is similar to [`python-channel2`](../python-channel2/), but since it is an application plugin rather than a channel plugin, it also needs to generate an HTML page when the user opens it from the Control Center.

Copy the `.plugin` and `.py` files to Awasu's `AppPlugins/` directory.

Restart Awasu, then open the plugin from the *Plugins* tab of the Control Center.

If Windows doesn't already recognize `.py` files as Python scripts, create a file called `GLOBAL.INI` in the Awasu installation directory that looks like this:
```
[Scripting File Associations]
.py = "C:\Program Files\Python310\python.exe" "%1" %*

[Script File Types]
.py = Python
```
and restart Awasu.
