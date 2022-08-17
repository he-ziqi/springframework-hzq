package world.hzq.ioc.impl;

import world.hzq.aop.impl.Agent;
import world.hzq.ioc.BeanDefinition;

import java.util.List;

public abstract class AbstractBeanDefinition implements BeanDefinition {
    private String beanName;
    private String scope = BeanDefinition.SCOPE_SINGLETON;
    private volatile Class<?> beanClass;
    private String beanClassName;
    private boolean lazyInit = true;
    private List<Class<?>> superClasses;
    private List<Class<?>> interfaces;
    private boolean load = false;
    private boolean proxy = false;
    private Agent agent;
    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public Class<?> getBeanType() {
        return beanClass;
    }

    @Override
    public void setBeanType(Class<?> beanType) {
        this.beanClass = beanType;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public boolean isSingleton() {
        return this.scope.equals(BeanDefinition.SCOPE_SINGLETON);
    }

    @Override
    public boolean isLazyInit() {
        return lazyInit;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public List<Class<?>> getInterfaces() {
        return interfaces;
    }

    @Override
    public void setInterfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
    }

    @Override
    public List<Class<?>> getSuperClasses() {
        return superClasses;
    }

    @Override
    public void setSuperClasses(List<Class<?>> superClasses) {
        this.superClasses = superClasses;
    }

    @Override
    public void setLoad(boolean load) {
        this.load = load;
    }

    @Override
    public boolean isLoad() {
        return load;
    }

    @Override
    public boolean getProxy() {
        return proxy;
    }

    @Override
    public void setProxy(boolean proxy) {
        this.proxy = proxy;
    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}
