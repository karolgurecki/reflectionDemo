package pl.com.tt.psc.reflectionTest;

import org.apache.log4j.Logger;
import pl.com.tt.psc.reflationLib.contexts.AnnotationContext;
import pl.com.tt.psc.reflationLib.contexts.Context;
import pl.com.tt.psc.reflationLib.proxies.AspectProxy;
import pl.com.tt.psc.reflectionTest.subPackage.SecondBean;

import java.lang.reflect.Proxy;

/**
 * The class Test
 *
 * @author Karol Górecki <a href="mailto:kgorecki (at) ptc.com">kgorecki
 *         (at) ptc.com</a>
 */
public class Test {

    private static final Logger LOGGER = Logger.getLogger(Test.class);

    public static void main(String[] args) {
        Context context = new AnnotationContext(ConfigClass.class);
        MainBean mainBean = context.getBean(MainBean.class);
        LOGGER.info(mainBean.getText());
        LOGGER.info(mainBean.getFirstInterface().methodFroFirstInterface());
        LOGGER.info(mainBean.getSecondInterface().methodFroSecondInterface());
    }
}
