// COPYRIGHT:   (c) Awasu Pty. Ltd. 2002 (all rights reserved).
//              Unauthorized use of this code is prohibited.
//
// LICENSE:     This software is provided 'as-is', without any express
//              or implied warranty.
//
//              In no event will the author be held liable for any damages
//              arising from the use of this software.
//
//              Permission is granted to anyone to use this software
//              for any non-commercial purpose and to alter it and
//              redistribute it freely, subject to the following restrictions:
//
//              - The origin of this software must not be misrepresented;
//                you must not claim that you wrote the original software.
//                If you use this software, an acknowledgement is requested
//                but not required.
//
//              - Altered source versions must be plainly marked as such,
//                and must not be misrepresented as being the original software.
//                Altered source is encouraged to be submitted back to
//                the original author so it can be shared with the community.
//                Please share your changes.
//
//              - This notice may not be removed or altered from any
//                source distribution.

import java.sql.* ;

public class SampleJavaAppPlugin
{

    // --- MAIN -------------------------------------------------------

    public static void
    main( String[] args )
    {
        try {
            doMain( args ) ;
        }
        catch( Exception xcptn ) {
            System.err.println( APP_PLUGIN_ID + " error:" ) ;
            System.err.println( "  " + xcptn.toString() ) ;
            System.exit( 1 ) ;
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static void
    doMain( String[] args ) throws Exception
    {
        // get the name of the INI file
        if ( args.length != 1 )
            throw new Exception( "Missing plugin INI file." ) ;
        iniFiles configFile = new iniFiles( args[0] ) ;
        configFile.loadIni() ;

        // process the requested command
        String cmd = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "Command" ) ;
        if ( cmd.compareToIgnoreCase( "GenerateMainPage" ) == 0 )
            generateMainPage( configFile ) ;
        else if ( cmd.compareToIgnoreCase( "ProcessRequest" ) == 0 ) {
            String pluginRequest = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "PluginRequest" ) ;
            String paramString = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "ParamString" ) ;
            processRequest( configFile, pluginRequest, paramString ) ;
        }
        else
            throw new Exception( "Unknown script command: " + cmd ) ;
    }

    // ----------------------------------------------------------------

    private static void
    generateMainPage( iniFiles configFile ) throws Exception
    {
        // NOTE: We get here when the user opens the plugin from the Control Center
        // and Awasu asks us to generate the HTML for the plugin's main page.

        // get the app settings
        String appServerUrl = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "AppServerUrl" ) ;
        if ( appServerUrl.isEmpty() )
            throw new Exception( "No app server URL was specified." ) ;
        String appPluginServerUrl = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "AppPluginServerUrl" ) ;
        if ( appPluginServerUrl.isEmpty() )
            throw new Exception( "No app plugin server URL was specified." ) ;
        String apiToken = configFile.getValue( SYSTEM_PARAMETERS_SECTION_NAME, "ApiToken" ) ;

        // generate the main page
        System.out.println( "<html>" ) ;
        System.out.println( "<body>" ) ;
        System.out.println( "<h2>Sample Java Plugin</h2>" ) ;
        System.out.println( "<p>This is the main page for the sample Java plugin." ) ;
        System.out.println( "<p>Click on one of the following links to subscribe to a channel that will monitor the specified database table." ) ;
        System.out.println( "<ul>" ) ;
        System.out.println( "  <li>" + generateChannelLink( appServerUrl, appPluginServerUrl, apiToken, "simple_rss_items" ) ) ;
        System.out.println( "  <li>" + generateChannelLink( appServerUrl, appPluginServerUrl, apiToken, "customer_sales" ) ) ;
        System.out.println( "</ul>" ) ;
        System.out.println( "</body>" ) ;
        System.out.println( "</html>" ) ;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static String
    generateChannelLink( String appServerUrl, String appPluginServerUrl, String apiToken, String tableName )
    {
        // generate a channel URL
        StringBuffer buf = new StringBuffer() ;
        buf.append( "<a href='" ) ;
        buf.append( appServerUrl + "/channels/subscribe" ) ;
        buf.append( "?token=" + apiToken ) ;
        buf.append( "&silent=1" ) ;
        buf.append( "&url=" + appPluginServerUrl + "/" + APP_PLUGIN_ID + "/" + tableName ) ;
        buf.append( "'>" ) ;
        buf.append( tableName ) ;
        buf.append( "</a>" ) ;
        return buf.toString() ;
    }

    // ----------------------------------------------------------------

    private static void
    processRequest( iniFiles configFile, String pluginRequest, String paramString ) throws Exception
    {
        // NOTE: We get here when Awasu tries to access the URL's we generated
        // previously in generateMainPage() to get the latest channel feed.
        // These URL's were constructed in such a way that the pluginRequest is
        // the name of the table we are to monitor.
        String tableName = pluginRequest ;

        // extract our request parameters
        String databasePath = configFile.getValue( PLUGIN_PARAMETERS_SECTION_NAME, "DatabasePath" ) ;
        int maxRows = configFile.getIntValue( CHANNEL_PARAMETERS_SECTION_NAME, "MaxRows" ) ;

        // connect to the database
        Connection conn = DriverManager.getConnection(
            "jdbc:sqlite:" + databasePath
        ) ;

        // generate the RSS feed
        if ( tableName.compareToIgnoreCase( "simple_rss_items" ) == 0 )
            querySimpleRssItems( conn, maxRows ) ;
        else if ( tableName.compareToIgnoreCase( "customer_sales" ) ==  0)
            queryCustomerSales( conn, maxRows ) ;
        else
            throw new Exception( "Unknown table name: " + tableName ) ;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static void
    querySimpleRssItems( Connection conn, int maxRows ) throws Exception
    {
        // prepare the SQL query
        String sql ;
        if ( maxRows < 0 )
            sql = "SELECT * FROM simple_rss_items" ;
        else
            sql = "SELECT * FROM simple_rss_items LIMIT " + Integer.toString(maxRows) ;
        Statement stmt = conn.createStatement() ;
        ResultSet rs = stmt.executeQuery( sql ) ;

        // put together a channel description
        StringBuffer descriptionBuf = new StringBuffer() ;
        descriptionBuf.append( "<p>This RSS feed was generated by the sample Java plugin." ) ;
        descriptionBuf.append( "<p>The SQL used to query the database was:" ) ;
        descriptionBuf.append( "<pre>&nbsp;&nbsp;" + sql + "</pre>" ) ;

        // generate the RSS feed
        StringBuffer rssBuf = new StringBuffer() ;
        rssBuf.append( "<rss>\n" ) ;
        rssBuf.append( "<channel>\n" ) ;
        rssBuf.append( "<title>Sample Java Plugin</title>\n" ) ;
        rssBuf.append( "<description><![CDATA[" + descriptionBuf.toString() + "]]></description>\n" ) ;
        while( rs.next() ) {
            rssBuf.append( "<item>\n" ) ;
            rssBuf.append( "<link>" + rs.getString("link") + "</link>\n" ) ;
            rssBuf.append( "<title>" + rs.getString("title") + "</title>\n" ) ;
            rssBuf.append( "<description>" + rs.getString("description") + "</description>\n" ) ;
            rssBuf.append( "</item>\n" ) ;
        }
        rssBuf.append( "</channel>\n" ) ;
        rssBuf.append( "</rss>\n" ) ;
        System.out.println( rssBuf.toString() ) ;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    private static void
    queryCustomerSales( Connection conn, int maxRows ) throws Exception
    {
        // prepare the SQL query
        String sql ;
        if ( maxRows < 0 )
            sql = "SELECT * FROM customer_sales" ;
        else
            sql = "SELECT * FROM customer_sales LIMIT " + Integer.toString(maxRows) ;
        Statement stmt = conn.createStatement() ;
        ResultSet rs = stmt.executeQuery( sql ) ;

        // generate the RSS feed
        StringBuffer rssBuf = new StringBuffer() ;
        rssBuf.append( "<rss>\n" ) ;
        rssBuf.append( "<channel>\n" ) ;
        rssBuf.append( "<link>http://intranet/sales</link>\n" ) ;
        rssBuf.append( "<title>Latest Customer Sales</title>\n" ) ;
        while( rs.next() ) {
            rssBuf.append( "<item>\n" ) ;
            // NOTE: This is a dummy URL that demonstrates how one might be generated for a given sales item.
            String url = "http://intranet/sales/" + rs.getString("id") ;
            rssBuf.append( "<link>" + url + "</link>\n" ) ;
            String title = rs.getString("customer_name") + " ($" + rs.getInt("amount") + ")" ;
            rssBuf.append( "<title>" + title + "</title>\n" ) ;
            StringBuffer buf = new StringBuffer() ;
            buf.append( rs.getString("date_of_sale") + ": " ) ;
            buf.append( rs.getString("item_count") + " x " + rs.getString("item_name") ) ;
            String comments = rs.getString("Comments") ;
            if ( comments != null )
                buf.append( "<br><i>" + comments + "</i>" ) ;
            rssBuf.append( "<description><![CDATA[" + buf.toString() + "]]></description>\n" ) ;
            rssBuf.append( "</item>\n" ) ;
        }
        rssBuf.append( "</channel>\n" ) ;
        rssBuf.append( "</rss>\n" ) ;
        System.out.println( rssBuf.toString() ) ;
    }

    // --- DATA MEMBERS ---

    private final static String APP_PLUGIN_ID = "SampleJavaAppPlugin" ;
    private final static String SYSTEM_PARAMETERS_SECTION_NAME = "System" ;
    private final static String PLUGIN_PARAMETERS_SECTION_NAME = "PluginParameters" ;
    private final static String CHANNEL_PARAMETERS_SECTION_NAME = "ChannelParameters" ;

}
