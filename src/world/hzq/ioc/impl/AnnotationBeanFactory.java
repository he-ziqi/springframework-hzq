package world.hzq.ioc.impl;

import world.hzq.aop.AopProxy;
import world.hzq.aop.annotation.Aspect;
import world.hzq.aop.annotation.Pointcut;
import world.hzq.aop.impl.Agent;
import world.hzq.aop.impl.PointExpressionImpl;
import world.hzq.aop.impl.autoproxy.JdkDynamicAopProxy;
import world.hzq.ioc.BeanDefinition;
import world.hzq.ioc.BeanDefinitionRegistry;
import world.hzq.ioc.BeanFactory;
import world.hzq.ioc.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * bean工厂实现,存储bean的容器
 */
public class AnnotationBeanFactory implements BeanFactory, BeanDefinitionRegistry {
    //注解包解析器
    private final PackageAndAnnotationResolve packageAndAnnotationResolve;
    //扫描的注解类型集合
    private final Class<?>[] accessAnnotations = {Component.class, Controller.class, Service.class, Repository.class,Configuration.class};
    //切点表达式解析器
    private final Map<BeanDefinition,Map<Method,PointExpressionImpl>> pointExpressionImplMap = new ConcurrentHashMap<>();
    //默认初始化的bean对象
    private static final Object BEAN_MAP_DEFAULT_VALUE = new Object();
    //存放bean
    private final Map<BeanDefinition,Object> beanMap = new ConcurrentHashMap<>();
    //存放切面类
    private final Map<String,BeanDefinition> aspectClasses = new ConcurrentHashMap<>();

    public AnnotationBeanFactory(PackageAndAnnotationResolve packageAndAnnotationResolve) {
        this.packageAndAnnotationResolve = packageAndAnnotationResolve;
    }

    /**
     * 通过名字获取bean
     * @param name beanName
     * @return bean
     */
    @Override
    public Object getBean(String name) {
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition beanDefinition = entry.getKey();
            Object bean = entry.getValue();
            if (beanDefinition.getBeanName().equals(name)) {
                return getBeanLogic(beanDefinition,bean);
            }
        }
        return null;
    }

    /**
     * 通过类型获取bean
     * @param requiredType bean字节码对象
     * @param <T> bean类型
     * @return bean
     */
    @SuppressWarnings("all")
    @Override
    public <T> T getBean(Class<T> requiredType) {
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition definition = entry.getKey();
            Object bean = entry.getValue();
            Class<?> beanClass = definition.getBeanClass();
            if(beanClass.equals(requiredType)){
                return (T) getBeanLogic(definition,bean);
            }
        }
        return null;
    }

    /**
     * 通过名字和字节码类型获取bean
     * @param name beanName
     * @param requiredType bean字节码对象
     * @param <T> bean 类型
     * @return bean
     */
    @SuppressWarnings("all")
    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition definition = entry.getKey();
            Object bean = entry.getValue();
            Class<?> beanClass = definition.getBeanClass();
            String beanName = definition.getBeanName();
            if(beanName.equals(name) && beanClass.equals(requiredType)){
                return (T) getBeanLogic(definition,bean);
            }
        }
        return null;
    }

    /**
     * 获取bean的逻辑
     * @param beanDefinition bean定义
     * @param bean bean实例
     * @return bean对象
     */
    private Object getBeanLogic(BeanDefinition beanDefinition,Object bean){
        //bean已经创建且为单例直接返回(已加载过)
        if(bean != BEAN_MAP_DEFAULT_VALUE && beanDefinition.isSingleton() && beanDefinition.isLoad()){
            return bean;
        }
        //非单例并且为懒加载则创建新实例返回
        if(!beanDefinition.isSingleton()){
            //进行依赖注入并返回对象
            try {
                Object obj = beanDefinition.getBeanClass().newInstance();
                //是否创建代理对象判断
                obj = ifNeedToCreateABeanInstanceThroughAProxy(beanDefinition,obj);
                //依赖注入
                dependencyInjection(beanDefinition,obj);
                beanDefinition.setLoad(true);
                return obj;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        //懒加载并且对象还未初始化
        if (!beanDefinition.isLoad()) {
            try {
                //进行依赖注入并返回对象
                Object obj = beanDefinition.getBeanClass().newInstance();
                //是否创建代理对象判断
                obj = ifNeedToCreateABeanInstanceThroughAProxy(beanDefinition,obj);
                beanMap.put(beanDefinition,obj);
                //依赖注入
                dependencyInjection(beanDefinition,obj);
                beanDefinition.setLoad(true);
                return obj;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public boolean isSingleton(String name) {
        return getBeanDefinition(name).isSingleton();
    }

    @Override
    public boolean containsBean(String name) {
        return containsBeanDefinition(getBeanDefinition(name));
    }

    @Override
    public Class<?> getType(String name) {
        return getBeanDefinition(name).getBeanType();
    }


    /**
     * 获取指定包名下的所有类文件,并将其封装为BeanDefinition
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        //获取指定包名下所有的bean定义对象
        Set<BeanDefinition> beanDefinitions = packageAndAnnotationResolve.getPackageClasses(beanName, accessAnnotations);
        //获取容器中所有的切面类
        getAllAspectClasses(beanDefinitions);
        //解析并且初始化所有的切面类
        resolveAndInitializeAspectClass();
        //对需要增强的bean进行标记
        markBeansThatNeedToBeEnhanced(beanDefinitions);
        //向容器中注册bean
        for (BeanDefinition definition : beanDefinitions) {
            registerBeanByBeanDefinition(definition);
        }
    }

    /**
     * 获取容器中所有的切面类
     */
    public void getAllAspectClasses(Set<BeanDefinition> beanDefinitions){
        for (BeanDefinition definition : beanDefinitions) {
            if(definition != null) {
                //获取所有的切面类
                if (definition.getBeanClass().getDeclaredAnnotation(Aspect.class) != null) {
                    aspectClasses.put(definition.getBeanName(), definition);
                }
            }
        }
    }

    /**
     * 解析并且初始化所有的切面类
     */
    public void resolveAndInitializeAspectClass(){
        //解析切面类
        for (Map.Entry<String, BeanDefinition> entry : aspectClasses.entrySet()) {
            BeanDefinition aspectBeanDefinition = entry.getValue();
            //创建该切面类的切点表达式map(一个切面类可能对应多个切点表达式)
            Map<Method,PointExpressionImpl> pointExpressions = new ConcurrentHashMap<>();
            //解析切点表达式
            for (Method method : aspectBeanDefinition.getBeanClass().getDeclaredMethods()) {
                Pointcut pointcut = method.getDeclaredAnnotation(Pointcut.class);
                //存在切点表达式则解析切点表达式
                if(pointcut != null){
                    PointExpressionImpl pointExpression = new PointExpressionImpl(pointcut.value());
                    pointExpressions.put(method,pointExpression);
                }
            }
            //创建切点表达式解析器
            pointExpressionImplMap.put(aspectBeanDefinition,pointExpressions);
        }
    }

    /**
     * 对容器中需要增强的bean进行标记和代理人的设置
     */
    public void markBeansThatNeedToBeEnhanced(Set<BeanDefinition> beanDefinitions){
        //遍历bean容器,对增强的类或方法设置代理标记和代理人
        for (Map.Entry<BeanDefinition, Map<Method, PointExpressionImpl>> entry : pointExpressionImplMap.entrySet()) {
            //遍历当前切面类所有的切点表达式
            for (Map.Entry<Method, PointExpressionImpl> methodPointExpressionEntry : entry.getValue().entrySet()) {
                PointExpressionImpl methodPointExpressionEntryValue = methodPointExpressionEntry.getValue();
                //是注解切点表达式
                if (methodPointExpressionEntryValue.isAnnotation()) {
                    //寻找容器中bean的方法上标注此注解切点表达式的bean
                    for (BeanDefinition beanDefinitionObjectEntry : beanDefinitions) {
                        if(beanDefinitionObjectEntry == null){
                            continue;
                        }
                        Class<?> beanClass = beanDefinitionObjectEntry.getBeanClass();
                        //遍历当前bean的所有方法
                        //切点表达式的注解是否存在
                        boolean exists = false;
                        for (Method method : beanClass.getDeclaredMethods()) {
                            //遍历当前方法的所有注解
                            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                                if(declaredAnnotation.annotationType().equals(methodPointExpressionEntryValue.getAnnotationType())){
                                    exists = true; //只要有一个方法存在即该类的此方法被增强
                                    break;
                                }
                            }
                            if(exists){
                                break;
                            }
                        }
                        if(exists){ //该bean被增强
                            //设置代理标记
                            beanDefinitionObjectEntry.setProxy(true);
                            //设置代理人为当前切面类及切面类中的切点表达式解析器的集合
                            beanDefinitionObjectEntry.setAgent(new Agent(entry.getKey().getBeanClass(),entry.getValue()));
                        }
                    }
                }else{ //非注解表达式
                    for (Class<?> adviceClass : methodPointExpressionEntryValue.getAdviceClasses()) {
                        //寻找容器中的bean是否与被增强的类相同
                        for (BeanDefinition beanDefinitionObjectEntry : beanDefinitions) {
                            if(beanDefinitionObjectEntry == null){
                                continue;
                            }
                            //当前类被增强
                            if (beanDefinitionObjectEntry.getBeanClass().equals(adviceClass)) {
                                //设置代理标记为true
                                beanDefinitionObjectEntry.setProxy(true);
                                //设置代理人为当前切面类及切面类中的切点表达式解析器的集合
                                beanDefinitionObjectEntry.setAgent(new Agent(entry.getKey().getBeanClass(),entry.getValue()));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            if (entry.getKey().getBeanName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 没有configuration类是默认加载所有包下的bean
     */
    @Override
    public void defaultLoad() {
        //扫描项目所有包获取beanDefinition
        Set<BeanDefinition> beanDefinitions = PackageAndAnnotationResolve.defaultLoad();
        //获取容器中所有的切面类
        getAllAspectClasses(beanDefinitions);
        //解析并且初始化所有的切面类
        resolveAndInitializeAspectClass();
        //对需要增强的bean进行标记
        markBeansThatNeedToBeEnhanced(beanDefinitions);
        //向容器中注册bean
        for (BeanDefinition definition : beanDefinitions) {
            registerBeanByBeanDefinition(definition);
        }
    }

    /**
     * 向容器中注册bean
     */
    private void registerBeanByBeanDefinition(BeanDefinition definition){
        if(definition == null){
            return;
        }

        //是懒加载的话暂不创建实例
        if (!definition.isLazyInit() && !definition.isLoad()) {
            Class<?> beanClass = definition.getBeanClass();
            try {
                if(!definition.isLoad()){
                    //创建当前bean的对象
                    Object obj = beanClass.newInstance();
                    //创建代理对象判断
                    obj = ifNeedToCreateABeanInstanceThroughAProxy(definition, obj);
                    //进行依赖注入并返回对象
                    beanMap.put(definition,obj);
                    dependencyInjection(definition,obj);
                    definition.setLoad(true);
                }
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }else{
            beanMap.put(definition,BEAN_MAP_DEFAULT_VALUE);
        }
    }

    /**
     * 如果需要则通过代理创建bean对象(将ioc容器中即将生成的bean替换为代理对象)
     * @param definition bean定义
     * @param obj bean对象
     * @return 如果需要代理则返回代理对象,不需要则原样返回obj
     */
    public Object ifNeedToCreateABeanInstanceThroughAProxy(BeanDefinition definition,Object obj) throws NoSuchMethodException {
        //判断当前bean是否需要代理
        if (definition.getProxy()) {
            //需要代理则创建代理对象放入容器
            Agent agent = definition.getAgent();//获取代理人
            //默认采用JDK动态代理(后期可调整================================================================================================================================)
            AopProxy aopProxy = new JdkDynamicAopProxy(obj,agent);
            //获取代理对象将obj重新赋值为代理对象
            obj = aopProxy.getProxy();
        }
        return obj;
    }

    /**
     * 对obj对象进行依赖注入
     * @param definition bean定义
     * @param obj 要进行依赖注入的目标对象
     */
    private void dependencyInjection(BeanDefinition definition,Object obj) throws IllegalAccessException, InstantiationException, NoSuchMethodException {
        //已初始化过直接结束
        if(definition.isLoad()){
            return;
        }
        for (Field field : definition.getBeanClass().getDeclaredFields()) {
            field.setAccessible(true);
            //value注解的注入
            Value v = field.getDeclaredAnnotation(Value.class);
            if(v != null){
                String value = v.value();
                Class<?> fieldType = field.getType();
                if(fieldType.equals(int.class) || fieldType.equals(Integer.class)){
                    field.set(obj,Integer.parseInt(value));
                }else if(fieldType.equals(double.class) || fieldType.equals(Double.class)){
                    field.set(obj,Double.parseDouble(value));
                }else if(fieldType.equals(float.class) || fieldType.equals(Float.class)){
                    field.set(obj,Float.parseFloat(value));
                }else if(fieldType.equals(short.class) || fieldType.equals(Short.class)){
                    field.set(obj,Short.parseShort(value));
                }else if(fieldType.equals(byte.class) || fieldType.equals(Byte.class)){
                    field.set(obj,Byte.parseByte(value));
                }else if(fieldType.equals(long.class) || fieldType.equals(Long.class)){
                    field.set(obj,Long.parseLong(value));
                }else if(fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)){
                    field.set(obj,Boolean.parseBoolean(value));
                }else if(fieldType.equals(char.class) || fieldType.equals(Character.class)){
                    if(value.length() == 1){
                        field.set(obj,value.charAt(0));
                    }
                }else if(fieldType.equals(String.class)){
                    field.set(obj,value);
                }
            }
        }
        for (Field field : definition.getBeanClass().getDeclaredFields()) {
            //暴力反射
            field.setAccessible(true);
            Annotation autowired = field.getDeclaredAnnotation(Autowired.class);
            Qualifier qualifier = field.getDeclaredAnnotation(Qualifier.class);
            //同时存在Autowired和Qualifier注解
            Object val = null;
            //获取当前属性应注入的beanDefinition
            BeanDefinition diBeanDefinition = getDIBeanDefinition(field.getType());
            //未匹配到合适类型(注入失败)
            if(diBeanDefinition == null){
                return;
            }
            //autowired和qualifier都存在
            if(autowired != null && qualifier != null){
                val = getBean(diBeanDefinition, qualifier.value());
            }
            //只存在Autowired
            if(autowired != null && qualifier == null){
                val = getBean(diBeanDefinition);
            }
            //判断当前beanDefinition是否已初始化
            if(diBeanDefinition.isLoad()){
                //已初始化过,则获取该实例为field赋值
                field.set(obj,val);
            }else{
                //当前definition未初始化,则递归对此definition进行初始化
                if(val == BEAN_MAP_DEFAULT_VALUE){
                    val = diBeanDefinition.getBeanClass().newInstance();
                    //判断是否需要代理
                    val = ifNeedToCreateABeanInstanceThroughAProxy(diBeanDefinition, val);
                    field.set(obj,val);
                    beanMap.put(diBeanDefinition,val);
                    dependencyInjection(diBeanDefinition,val);
                }else{ //有值则注入,否则就递归
                    field.set(obj,val);
                }
            }
        }
        //获取方法上注解的信息
        for (Method method : definition.getBeanClass().getDeclaredMethods()) {
            method.setAccessible(true);
            Bean bean = method.getDeclaredAnnotation(Bean.class);
            Scope scope = method.getDeclaredAnnotation(Scope.class);
            if(bean != null){
                String beanName = null;
                String value = bean.value();
                boolean lazy = bean.lazy();
                if("".equals(value)){
                    //如果没有指定bean的名称,默认使用方法返回值的首字母小写后作为bean的名称
                    Class<?> returnType = method.getReturnType();
                    beanName = (char) (returnType.getSimpleName().charAt(0) + 32) + returnType.getSimpleName().substring(1);
                }else{
                    beanName = value;
                }
                //默认为单例
                boolean singleton = true;
                if (scope != null) {
                    singleton = scope.singleton();
                }
                Class<?> returnClass = method.getReturnType();
                List<Class<?>> interfaces = new LinkedList<>();
                List<Class<?>> supperClasses = new LinkedList<>();
                //获取当前类型的父类及接口字节码
                PackageAndAnnotationResolve.getInterfaces(returnClass,interfaces);
                PackageAndAnnotationResolve.getSupperClasses(returnClass,supperClasses);
                //创建beanDefinition
                BasePackagesBeanDefinition beanDefinition = new BasePackagesBeanDefinition(returnClass, returnClass.getName(),
                        beanName, singleton ? BeanDefinition.SCOPE_SINGLETON : BeanDefinition.SCOPE_PROTOTYPE, lazy,
                        interfaces,supperClasses);
                registerBeanByBeanDefinition(beanDefinition);
            }
        }
        //属性和方法注入结束则标记为已加载
        definition.setLoad(true);
    }

    private Object getBean(BeanDefinition definition,String name){
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition key = entry.getKey();
            if(definition.equals(key) && definition.getBeanName().equals(name)){
                return entry.getValue();
            }
        }
        return null;
    }

    private Object getBean(BeanDefinition definition){
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition key = entry.getKey();
            if(definition.equals(key)){
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 从容器中选取beanClass类型的子类或接口实现类
     * @param beanClass 目标类型
     * @return 目标类型的子类或接口实现类
     */
    private BeanDefinition getDIBeanDefinition(Class<?> beanClass){
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition definition = entry.getKey();
            //匹配自身类型
            if (beanClass.equals(definition.getBeanClass())) {
                return definition;
            }
        }
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition definition = entry.getKey();
            //从接口中匹配
            for (Class<?> definitionInterface : definition.getInterfaces()) {
                if (beanClass.equals(definitionInterface)) {
                    return definition;
                }
            }
        }
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            BeanDefinition definition = entry.getKey();
            //从父类中匹配
            for (Class<?> superClass : definition.getSuperClasses()) {
                if(beanClass.equals(superClass)){
                    return definition;
                }
            }
        }
        return null;
    }

    private boolean containsBeanDefinition(BeanDefinition definition){
        for (Map.Entry<BeanDefinition, Object> entry : beanMap.entrySet()) {
            if(definition.equals(entry.getKey())){
                return true;
            }
        }
        return false;
    }

    private BeanDefinition createBeanDefinitionByClass(Class<?> beanClass){
        return PackageAndAnnotationResolve.createBeanDefinition(beanClass, accessAnnotations);
    }

    /**
     * 解析配置类
     * @param configClass 配置类
     */
    public void resolveConfigClass(Class<?>... configClass) {
        for (Class<?> beanClass : configClass) {
            BeanDefinition definition = createBeanDefinitionByClass(beanClass);
            registerBeanByBeanDefinition(definition);
        }
    }
}