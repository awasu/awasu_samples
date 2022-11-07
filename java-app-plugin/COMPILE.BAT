@ECHO off

REM compile the plugin
javac -d out *.java
ECHO.

REM create the JAR
jar cvfm SampleJavaAppPlugin.jar ^
    manifest.mf ^
    -C out .
