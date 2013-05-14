// generic exception class for problems creating an RSSFeed object.

package org.spl.feed;

public class RSSException extends Exception {
    public RSSException( String message ) {
        super(message);
    }
}