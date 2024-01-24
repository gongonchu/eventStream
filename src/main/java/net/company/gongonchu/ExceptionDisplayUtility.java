package net.company.gongonchu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExceptionDisplayUtility {

    private static Logger LOG = LoggerFactory.getLogger(ExceptionDisplayUtility.class);

    private ExceptionDisplayUtility() {
    }

    public static void displayExceptionMessage( String _message, Exception _exception){
        LOG.error(_message);
        LOG.error(_exception.getMessage());
    }

}
