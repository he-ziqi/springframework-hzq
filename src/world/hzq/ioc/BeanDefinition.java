package world.hzq.ioc;

import world.hzq.aop.impl.Agent;

import java.util.List;

//bean定义
public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";
    //获取beanName
    String getBeanName();
    //获取bean类型
    Class<?> getBeanType();
    //设置bean类型
    void setBeanType(Class<?> beanType);
    //设置beanName
    void setBeanName(String beanName);
    //设置/获取作用域
    void setScope(String scope);
    String getScope();
    //是否单例
    boolean isSingleton();
    //是否懒加载
    boolean isLazyInit();
    void setLazyInit(boolean lazyInit);

    //设置/获取bean类名
    String getBeanClassName();
    void setBeanClassName(String beanClassName);

    //设置/获取bean的Class文件
    Class<?> getBeanClass();
    void setBeanClass(Class<?> beanClass);
    
    //获取/设置bean的接口
    List<Class<?>> getInterfaces();
    void setInterfaces(List<Class<?>> interfaces);
    
    //获取/设置bean的直接或间接父类
    List<Class<?>> getSuperClasses();
    void setSuperClasses(List<Class<?>> superClasses);

    //获取/设置加载状态
    void setLoad(boolean load);
    boolean isLoad();

    //获取/设置是否需要代理bean
    void setProxy(boolean proxy);
    boolean getProxy();

    //获取设置代理人
    void setAgent(Agent agent);
    Agent getAgent();
}
