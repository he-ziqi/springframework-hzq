package world.hzq.ioc.impl;

import world.hzq.ioc.ApplicationContext;
import world.hzq.ioc.annotation.Configuration;

import java.util.Set;

public class ApplicationContextUtil {
    private volatile static ApplicationContext context;
    /**
     * 获取ioc容器
     */
    public static ApplicationContext getApplicationContext(){
        if(context == null){
            synchronized (ApplicationContextUtil.class) {
                if(context == null){
                    Set<Class<?>> configurationClassSet = PackageAndAnnotationResolve.getAll(new Class<?>[]{Configuration.class});
                    //没有Configuration类
                    if (configurationClassSet.size() == 0) {
                        context = new AnnotationConfigApplicationContext();
                    } else {
                        Class<?>[] configClasses = new Class[configurationClassSet.size()];
                        int index = 0;
                        for (Class<?> config : configurationClassSet) {
                            configClasses[index++] = config;
                        }
                        context = new AnnotationConfigApplicationContext(configClasses);
                    }
                }
            }
        }
        return context;
    }
}