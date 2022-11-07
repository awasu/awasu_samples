# Awasu samples

This repo contains examples that demonstrate how to extend Awasu.

Full documentation is in the *For developers* section of the [online help](https://awasu.com/help/).

### `python-channel`

[This](python-channel/) is the simplest example of all, and demonstrates how to write a channel plugin that generates a fixed RSS feed.

### `python-channel2`

[This](python-channel2/) extends `python-channel` by adding parameters to the plugin that lets the user to configure the generated RSS feed.

### `vbscript-channel`

[This](vbscript-channel/) is a simple channel plugin that demonstrates how to configure Awasu to run plugins written in different languages.

### `python-app-plugin`

[This](python-app-plugin/) is an example of an application plugin. It is similar to `python-channel2`, but since it is an application plugin rather than a channel plugin, it also needs to generate an HTML page when the user opens it from the Control Center.

### `cpp-app-plugin`

[This](cpp-app-plugin/) is a C++ version of `python-app-plugin`.

### `java-app-plugin`

[This](java-app-plugin/) is a more sophisticated example of an application plugin, that lets the user subscribe to multiple channels. It generates RSS feeds by querying a database, and returning the rows found as a feed.
