package pl.com.tt.psc.reflectionTest;

import org.apache.log4j.Logger;

/**
 * Created by goreckik on 2015-02-17.
 */
public class AspectClass {
    private static final Logger LOGGER = Logger.getLogger(AspectClass.class);

    public static void beforeMethod() {
        LOGGER.debug("This method are executing before another method");
    }

    public static void springResultAfterMethod(String str) {
        LOGGER.debug(String.format("This method are executing after another method which return \"%s\"", str));
    }
}
