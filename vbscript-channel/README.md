# vbscript-channel

This is a simple channel plugin that demonstrates how to configure Awasu to run plugins written in different languages.

Create a file called `GLOBAL.INI` in the Awasu installation directory that looks like this:
```
[Scripting File Associations]
.vbs = c:\windows\system32\cscript.exe /nologo "%1"

[Script File Types]
.vbs = VBScript
```

Restart Awasu, then start the New Channel wizard (*File, New Channel*), choose *"Generated by a channel plugin"*, and select the `.vbs` file in this directory.