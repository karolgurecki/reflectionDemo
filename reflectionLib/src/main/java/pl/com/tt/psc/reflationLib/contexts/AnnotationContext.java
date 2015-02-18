package pl.com.tt.psc.reflationLib.contexts;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import pl.com.tt.psc.reflationLib.annotantions.Component;
import pl.com.tt.psc.reflationLib.annotantions.ComponentScan;
import pl.com.tt.psc.reflationLib.annotantions.Configuration;
import pl.com.tt.psc.reflationLib.annotantions.Inject;
import pl.com.tt.psc.reflationLib.proxies.AspectProxy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Karol Górecki
 * <a href="mailto:kagurecki@gmail.com?Subject=Autotask Question" target="_top">kagurecki (at) gmail.com</a>
 * Version: 0.01
 * Since: 0.01
 */
public class AnnotationContext implements Context {

    public static final String SLASH = "/";
    public static final String DOT = ".";
    private static final Logger LOGGER = Logger.getLogger(AnnotationContext.class);
    private static final String EMPTY_STRING = "";
    private Map<Class, Object> beanMap = new HashMap<>();


    public AnnotationContext(Class configurationClass) {
        try {
            if (configurationClass.getAnnotation(Configuration.class) == null) {
                throw new RuntimeException(String.format("Class %s is not a configuration class",
                        configurationClass.getName()));
            }

            ComponentScan componentScan = (ComponentScan) configurationClass.getAnnotation(ComponentScan.class);
            if (componentScan == null) {
                throw new RuntimeException(String.format(
                        "A configuration class %s do not have ComponentScan annotation on it",
                        configurationClass.getName()));
            }

            String packagePath = componentScan.packageToScan().equalsIgnoreCase(EMPTY_STRING)
                    ? configurationClass.getPackage().getName() : componentScan.packageToScan();

            String packagePathWithoutDots = packagePath.replace(DOT, SLASH);

            List<File> classFiles = new ArrayList<>();
            scanPackage(packagePathWithoutDots, classFiles);

            createBeans(packagePathWithoutDots.replace(SLASH, File.separator), classFiles, 0, -1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> tClass) {
        return (T) beanMap.get(tClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String className) throws ClassNotFoundException {
        return getBean((Class<T>) Class.forName(className));
    }

    private void createBeans(String packagePath, List<File> classFiles, int k, int beginIndex)
            throws ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        for (int i = k; i < classFiles.size(); i++) {
            File clazzFile = classFiles.get(i);
            String absolutePath = clazzFile.getAbsolutePath();

            if (beginIndex == -1) {
                beginIndex = absolutePath.indexOf(packagePath);
            }
            Class aClass = Class.forName(FilenameUtils.getBaseName(absolutePath.substring(beginIndex).
                    replace(File.separator, DOT)));
            if (aClass.getAnnotation(Component.class) != null) {
                Object classObject;
                Class[] interfaces = aClass.getInterfaces();
                if (interfaces.length > 0) {
                    classObject = AspectProxy.newInstance(aClass.newInstance());

                    for (Class interfaceClass : interfaces) {
                        putIntoMap(classObject, interfaceClass);
                    }

                } else {
                    classObject = aClass.newInstance();
                    putIntoMap(classObject, aClass);
                }
                fillOutFields(packagePath, classFiles, i + 1, aClass, classObject, beginIndex);
            }
        }
    }

    private void scanPackage(String packagePathWithDots, List<File> classFiles) throws IOException {
        Enumeration<URL> resources = this.getClass().getClassLoader().getResources(packagePathWithDots);
        List<File> dirs = new ArrayList<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        for (File dir : dirs) {
            classFiles.addAll(FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
        }
    }

    private void fillOutFields(String packagePath, List<File> classFiles, int i, Class aClass, Object classObject,
                               int beginIndex)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getAnnotation(Inject.class) != null) {

                Class<?> fieldDeclaringClass = field.getType();
                Object valueOfField = beanMap.get(fieldDeclaringClass);

                if (valueOfField == null) {
                    createBeans(packagePath, classFiles, i, beginIndex);
                    valueOfField = beanMap.get(fieldDeclaringClass);
                }

                if (valueOfField == null) {
                    throw new RuntimeException(String.format("Can not satisfy dependency %s in class %s",
                            fieldDeclaringClass.getName(), aClass.getName()));
                }

                try {
                        field.set(classObject, valueOfField);
                    } catch (Exception e) {
                        boolean accessible = true;

                        try {
                            accessible = field.isAccessible();
                            field.setAccessible(true);
                            field.set(classObject, valueOfField);
                        } catch (Exception e2) {
                            throw new RuntimeException(e2);
                        } finally {
                            if (!accessible) {
                                field.setAccessible(false);
                            }
                        }
                }
            }
        }
    }

    private void putIntoMap(Object classObject, Class aClass) {
        Object o = beanMap.get(aClass);
        if (o != null) {
            if (!o.getClass().equals(classObject.getClass())) {
                throw new RuntimeException(String.format("Bean for class %s already exists in scope",
                        aClass.getName()));
            }
            return;
        }
        LOGGER.debug(String.format("Creating bean of class %s", aClass.getName()));
        beanMap.put(aClass, classObject);
    }
}
