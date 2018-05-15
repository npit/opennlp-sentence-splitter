package com.mycompany.app;

/**
 * Hello world!
 *
 */
import com.mycompany.app.splitter;

public class App 
{
    public static void main( String[] args )
    {
        splitter sp = new splitter("FR");
        sp.initSplitter("FR", args[0]);
    }
}
