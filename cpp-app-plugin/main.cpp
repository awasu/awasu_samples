/*
 * COPYRIGHT:   (c) Awasu Pty. Ltd. 2002 (all rights reserved).
 *              Unauthorized use of this code is prohibited.
 *
 * LICENSE:     This software is provided 'as-is', without any express
 *              or implied warranty.
 *
 *              In no event will the author be held liable for any damages
 *              arising from the use of this software.
 *
 *              Permission is granted to anyone to use this software
 *              for any non-commercial purpose and to alter it and
 *              redistribute it freely, subject to the following restrictions:
 *
 *              - The origin of this software must not be misrepresented;
 *                you must not claim that you wrote the original software.
 *                If you use this software, an acknowledgement is requested
 *                but not required.
 *
 *              - Altered source versions must be plainly marked as such,
 *                and must not be misrepresented as being the original software.
 *                Altered source is encouraged to be submitted back to
 *                the original author so it can be shared with the community.
 *                Please share your changes.
 *
 *              - This notice may not be removed or altered from any
 *                source distribution.
 */

#include <windows.h>

#include <iostream>
#include <sstream>
#include <cassert>

using namespace std ;

/* --- GLOBAL DEFINITIONS --------------------------------------------- */

// This is our plugin ID. It must correspond to the value configured in the .plugin file.
static const char* APP_PLUGIN_ID = "SampleCppPlugin" ;

// This is the INI file section that Awasu will pass system parameters to us.
static const char* SYSTEM_PARAMETERS_SECTION_NAME = "System" ;

// This is the INI file section that Awasu will pass global plugin parameters to us.
static const char* PLUGIN_PARAMETERS_SECTION_NAME = "PluginParameters" ;

// This is the INI file section that Awasu will pass per-channel parameters to us.
static const char* CHANNEL_PARAMETERS_SECTION_NAME = "ChannelParameters" ;

/* --- LOCAL DATA ----------------------------------------------------- */

// local functions:
static void generateMainPage( const char* pConfigFilename ) ;
static void processRequest( const char* pConfigFilename, const char* pRequestName, const char* pParamString ) ;

/* --- MAIN ----------------------------------------------------------- */

int
main( int argc, char* argv[] )
{
    // get the name of the INI file
    if ( argc != 2 ) {
        cerr << "Missing plugin INI file." << endl ;
        exit( 1 ) ;
    }
    const char* pConfigFilename = argv[1] ;

    // process the request
    char cmd[ 1024 ] ;
    DWORD n = GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "Command", "", cmd, sizeof(cmd), pConfigFilename ) ;
    if ( n == 0 ) {
        cerr << "Missing script command." << endl ;
        exit( 1 ) ;
    }
    if ( _stricmp(cmd,"GenerateMainPage") == 0 )
        generateMainPage( pConfigFilename ) ;
    else if ( _stricmp(cmd,"ProcessRequest") == 0 ) {
        char pluginRequest[ 1024 ] ;
        GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "PluginRequest", "", pluginRequest, sizeof(pluginRequest), pConfigFilename ) ;
        char paramString[ 1024 ] ;
        GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "ParamString", "", paramString, sizeof(paramString), pConfigFilename ) ;
        processRequest( pConfigFilename, pluginRequest, paramString ) ;
    }
    else {
        cerr << "Unknown script command: " << cmd << endl ;
        exit( 1 ) ;
    }

    return 0 ;
}

/* -------------------------------------------------------------------- */

// This method will generate the plugin's main page i.e. the one that will
// be displayed when it is opened from the Control Center.

static void
generateMainPage( const char* pConfigFilename )
{
    assert( pConfigFilename != NULL ) ;

    // get the app settings
    char appServerUrl[ 1024 ] ;
    DWORD n = GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "AppServerUrl", "", appServerUrl, sizeof(appServerUrl), pConfigFilename ) ;
    if ( n == 0 ) {
        cerr << "No app server URL was specified." << endl ;
        exit( 1 ) ;
    }
    char appPluginServerUrl[ 1024 ] ;
    n = GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "AppPluginServerUrl", "", appPluginServerUrl, sizeof(appPluginServerUrl), pConfigFilename ) ;
    if ( n == 0 ) {
        cerr << "No app server URL was specified." << endl ;
        exit( 1 ) ;
    }

    // get the plugin settings
    char domainName[ 1024 ] ;
    GetPrivateProfileString( PLUGIN_PARAMETERS_SECTION_NAME, "DomainName", "", domainName, sizeof(domainName), pConfigFilename ) ;

    // generate a URL that will let the user subscribe to a channel that
    // we will generate. We embed some information in URL path and parameters
    // for demonstration purposes. These will get passed in to processRequest()
    // when the channel is updated.
    char apiToken[ 80 ] ;
    GetPrivateProfileString( SYSTEM_PARAMETERS_SECTION_NAME, "ApiToken", "", apiToken, sizeof(apiToken), pConfigFilename ) ;
    stringstream urlBuf ;
    urlBuf << appServerUrl << "/channels/subscribe" ;
    urlBuf << "?token=" << apiToken ;
    urlBuf << "&silent=1" ;
    urlBuf << "&url=" << appPluginServerUrl << "/" << APP_PLUGIN_ID << "/foo/bar" << "?p1=Hello%26p2=World" ;

    // generate the main page
    cout << "<html>" << endl ;
    cout << "<body>" << endl ;
    cout << "<h2> Sample C++ Plugin </h2>" << endl ;
    cout << "<p> This is the main page for the sample C++ plugin." << endl ;
    cout << "<p> The plugin settings are:" << endl ;
    cout << "<ul>" << endl ;
    cout << "    <li> DomainName: " << domainName << endl ;
    cout << "</ul>" << endl ;
    cout << "<p> Click <a href='" << urlBuf.str() << "'>here</a> to subscribe to a test channel." << endl ;
    cout << "</body>" << endl ;
    cout << "</html>" << endl ;
}

/* -------------------------------------------------------------------- */

// This method is called when Awasu tries to access the URL we generated
// previously in generateMainPage() to get the latest channel feed.

static void
processRequest( const char* pConfigFilename, const char* pPluginRequest, const char* pParamString )
{
    assert( pConfigFilename != NULL ) ;
    assert( pPluginRequest != NULL ) ;
    assert( pParamString != NULL ) ;

    // get the plugin settings
    char domainName[ 1024 ] ;
    DWORD n = GetPrivateProfileString( PLUGIN_PARAMETERS_SECTION_NAME, "DomainName", "", domainName, sizeof(domainName), pConfigFilename ) ;
    if ( n == 0 )
    {
        cerr << "No domain name was specified." << endl ;
        exit( 1 ) ;
    }

    // get the channel settings
    int nItems = GetPrivateProfileInt( CHANNEL_PARAMETERS_SECTION_NAME, "nItems", 15, pConfigFilename ) ;
    char itemTitleStem[ 1024 ] ;
    GetPrivateProfileString( CHANNEL_PARAMETERS_SECTION_NAME, "ItemTitleStem", "Item Title", itemTitleStem, sizeof(itemTitleStem), pConfigFilename ) ;
    bool generateDescriptions = ( GetPrivateProfileInt( CHANNEL_PARAMETERS_SECTION_NAME, "GenerateDescriptions", true, pConfigFilename ) != 0 ) ;

    // put together a channel description
    stringstream descriptionBuf ;
    descriptionBuf << "<p> This RSS feed was generated by the sample C++ plugin." << endl ;
    descriptionBuf << "<p >The script was invoked with the following parameters:" << endl ;
    descriptionBuf << "<ul>" << endl ;
    descriptionBuf << "    <li> pluginRequest: " << pPluginRequest << endl ;
    descriptionBuf << "    <li> paramString: " << pParamString << endl ;
    descriptionBuf << "</ul>" << endl ;
    descriptionBuf << "<p> The plugin settings are:" << endl ;
    descriptionBuf << "<ul>" << endl ;
    descriptionBuf << "    <li> DomainName: " << domainName << endl ;
    descriptionBuf << "</ul>" << endl ;
    descriptionBuf << "<p> The channel settings are:" << endl ;
    descriptionBuf << "<ul>" << endl ;
    descriptionBuf << "    <li> nItems: " << nItems << endl ;
    descriptionBuf << "    <li> ItemTitleStem: " << itemTitleStem << endl ;
    descriptionBuf << "    <li> GenerateDescriptions: " << generateDescriptions << endl ;
    descriptionBuf << "</ul>" << endl ;

    // generate the RSS feed
    cout << "<rss>" << endl ;
    cout << "<channel>" << endl ;
    cout << endl ;
    cout << "<title>Sample C++ Plugin</title>" << endl ;
    cout << "<link>" << domainName << "</link>"<< endl ;
    cout << "<description><![CDATA[" << descriptionBuf.str() << "]]></description>"<< endl ;
    cout << endl ;
    for ( int i=1 ; i <= nItems ; ++i )
    {
        cout << "<item>"<< endl ;
        cout << "   <title>" << itemTitleStem << "-" << i << "</title>"<< endl ;
        cout << "   <link>" << domainName << "/item-" << i << ".html</link>"<< endl ;
        if ( generateDescriptions )
            cout << "   <description>This is the description for item " << i << "</description>"<< endl ;
        cout << "</item>"<< endl ;
        cout << endl ;
    }
    cout << "</channel>"<< endl ;
    cout << "</rss>"<< endl ;
}
