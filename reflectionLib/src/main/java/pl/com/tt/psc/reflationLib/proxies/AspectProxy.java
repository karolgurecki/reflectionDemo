package pl.com.tt.psc.reflationLib.proxies;

import pl.com.tt.psc.reflationLib.annotantions.After;
import pl.com.tt.psc.reflationLib.annotantions.Before;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by: Karol Górecki
 * <a href="mailto:kagurecki@gmail.com?Subject=Autotask Question" target="_top">kagurecki (at) gmail.com</a>
 * Version: 0.01
 * Since: 0.01
 */
public class AspectProxy implements InvocationHandler {

    private Object object;

    public AspectProxy(Object obj) {
        this.object = obj;
    }

    @SuppressWarnings("unchecked")
    public static Object newInstance(Object obj) {
        Class<?> objClass = obj.getClass();
        return Proxy.newProxyInstance(objClass.getClassLoader(), objClass.getInterfaces(), new AspectProxy(obj));

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class[] argsClasses = null;
        if (args != null) {
            argsClasses = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argsClasses[i] = args[i].getClass();
            }
        }

        Method objectMethod=object.getClass().getMethod(method.getName(),method.getParameterTypes());
        Before beforeAnnotation = objectMethod.getAnnotation(Before.class);
        invokeBeforeMethod(args, argsClasses, beforeAnnotation);

        Object result = method.invoke(object, args);

        After afterAnnotation = objectMethod.getAnnotation(After.class);
        invokeAfterMethod(new Object[]{result}, new Class[]{result.getClass()}, afterAnnotation);

        return result;
    }

    @SuppressWarnings("unchecked")
    private void invokeBeforeMethod(Object[] args, Class[] argsClasses, Before annotation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (annotation != null) {
            Class beforeClass = annotation.clazz();
            Method beforeMethod = beforeClass.getMethod(annotation.methodName(), argsClasses);
            if (beforeMethod != null) {
                beforeMethod.invoke(null, args);
            } else {
                throw new RuntimeException(String.format("Can't find method named %s in class %s",
                        annotation.methodName(), annotation.clazz().getName()));
            }

        }
    }

    @SuppressWarnings("unchecked")
    private void invokeAfterMethod(Object[] args, Class[] argsClasses, After annotation) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (annotation != null) {
            Class beforeClass = annotation.clazz();
            Method beforeMethod = beforeClass.getMethod(annotation.methodName(), argsClasses);
            if (beforeMethod != null) {
                beforeMethod.invoke(null, args);
            } else {
                throw new RuntimeException(String.format("Can't find method named %s in class %s",
                        annotation.methodName(), annotation.clazz().getName()));
            }

        }
    }

}
