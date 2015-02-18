package pl.com.tt.psc.reflectionTest;

import pl.com.tt.psc.reflationLib.annotantions.Component;
import pl.com.tt.psc.reflationLib.annotantions.Inject;
import pl.com.tt.psc.reflectionTest.subPackage.FirstInterface;
import pl.com.tt.psc.reflectionTest.subPackage.SecondInterface;

/**
 * Created by goreckik on 2015-02-17.
 */
@Component
public class MainBean {

    @Inject
    private FirstInterface FirstInterface;

    @Inject
    private SecondInterface SecondInterface;

    public FirstInterface getFirstInterface() {
        return FirstInterface;
    }

    public SecondInterface getSecondInterface() {
        return SecondInterface;
    }

    public String getText() {
        return "Text in main bean";
    }
}
