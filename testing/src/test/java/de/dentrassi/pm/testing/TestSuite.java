/*******************************************************************************
 * Copyright (c) 2015 IBH SYSTEMS GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package de.dentrassi.pm.testing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

@RunWith ( Suite.class )
@SuiteClasses ( { SetupTest.class, MailTest.class, FileStorageTest.class, UserTest.class, UploadTest.class, BasicTest.class, DefaultTest.class, } )
public class TestSuite
{
    private static String SAUCE_USER_NAME = System.getProperty ( "sauce.username" );

    private static String SAUCE_ACCESS_KEY = System.getProperty ( "sauce.accessKey" );

    private static RemoteWebDriver driver;

    public static RemoteWebDriver getDriver ()
    {
        return driver;
    }

    @BeforeClass
    public static void setupBrowser () throws MalformedURLException
    {
        if ( hasSauce () )
        {
            driver = createSauce ( Platform.WIN8_1, "chrome", null );
        }
        else
        {
            driver = new FirefoxDriver ();
        }
    }

    private static boolean hasSauce ()
    {
        return SAUCE_ACCESS_KEY != null && SAUCE_USER_NAME != null && !SAUCE_ACCESS_KEY.isEmpty () && !SAUCE_USER_NAME.isEmpty ();
    }

    protected static RemoteWebDriver createSauce ( final Platform os, final String browser, final String version ) throws MalformedURLException
    {
        final DesiredCapabilities capabilities = new DesiredCapabilities ();
        capabilities.setCapability ( CapabilityType.BROWSER_NAME, browser );
        if ( version != null )
        {
            capabilities.setCapability ( CapabilityType.VERSION, version );
        }
        capabilities.setCapability ( CapabilityType.PLATFORM, os );
        capabilities.setCapability ( "name", "Package Drone Main Test" );

        final RemoteWebDriver driver = new RemoteWebDriver ( new URL ( String.format ( "http://%s:%s@ondemand.saucelabs.com:80/wd/hub", SAUCE_USER_NAME, SAUCE_ACCESS_KEY ) ), capabilities );

        driver.setFileDetector ( new LocalFileDetector () );

        return driver;
    }

    @AfterClass
    public static void destroyBrowser ()
    {
        if ( driver != null )
        {
            driver.quit ();
        }
    }

    private static Process PROCESS;

    @BeforeClass
    public static void setup () throws IOException, InterruptedException
    {
        final String javaHome = System.getProperty ( "java.home" );

        final ProcessBuilder pb = new ProcessBuilder ( "target/instance/server" );

        pb.environment ().put ( "JAVA_HOME", javaHome );

        pb.inheritIO ();

        System.out.println ( "Starting: " + pb );
        PROCESS = pb.start ();
        System.out.print ( "Started ... waiting for port... " );
        System.out.flush ();

        int i = 0;
        while ( i < 5 )
        {
            if ( isOpen ( 8080 ) )
            {
                break;
            }
            i++;
            Thread.sleep ( 1000 );
        }

        if ( i >= 5 )
        {
            PROCESS.destroyForcibly ();
            throw new IllegalStateException ( "Failed to wait for port" );
        }
        else
        {
            System.out.println ( "Port open!" );

            // sleep a little bit longer to allow OSGi to fire up all services
            Thread.sleep ( 1000 );
        }
    }

    private static boolean isOpen ( final int port )
    {
        ServerSocket server = null;
        try
        {
            server = new ServerSocket ( port );
            return true;
        }
        catch ( final IOException e )
        {
        }
        finally
        {
            if ( server != null )
            {
                try
                {
                    server.close ();
                }
                catch ( final IOException e )
                {
                }
            }
        }

        return false;
    }

    @AfterClass
    public static void dispose () throws InterruptedException
    {
        System.out.print ( "Terminating server..." );
        System.out.flush ();

        if ( !PROCESS.destroyForcibly ().waitFor ( 10, TimeUnit.SECONDS ) )
        {
            throw new IllegalStateException ( "Failed to terminate process" );
        }

        System.out.println ( "done!" );
    }

}
