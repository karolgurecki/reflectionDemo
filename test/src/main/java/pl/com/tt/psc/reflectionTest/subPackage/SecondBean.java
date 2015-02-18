package pl.com.tt.psc.reflectionTest.subPackage;

import org.apache.log4j.Logger;
import pl.com.tt.psc.reflationLib.annotantions.After;
import pl.com.tt.psc.reflationLib.annotantions.Before;
import pl.com.tt.psc.reflationLib.annotantions.Component;
import pl.com.tt.psc.reflectionTest.AspectClass;

/**
 * Created by goreckik on 2015-02-17.
 */
@Component
public class SecondBean implements FirstInterface, SecondInterface {

    private static final Logger LOGGER=Logger.getLogger(SecondBean.class);

    @Override
    @Before(clazz = AspectClass.class, methodName = "beforeMethod")
    public String methodFroFirstInterface() {
        LOGGER.debug("Executing method from first interface");
        return String.format(textToFormat(), "first");
    }

    @Override
    @After(clazz = AspectClass.class, methodName = "springResultAfterMethod")
    public String methodFroSecondInterface() {
        LOGGER.debug("Executing method from second interface");
        return String.format(textToFormat(), "second");
    }

    private String textToFormat() {
        return "Method from %s interface.";
    }
}
