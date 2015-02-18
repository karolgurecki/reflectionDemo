package pl.com.tt.psc.reflationLib.contexts;

/**
 * Created by: Karol Górecki
 * <a href="mailto:kagurecki@gmail.com?Subject=Autotask Question" target="_top">kagurecki (at) gmail.com</a>
 * Version: 0.01
 * Since: 0.01
 */
public interface Context {

    <T> T getBean(Class<T> tClass);

    <T> T getBean(String className) throws ClassNotFoundException;
}
