package world.hzq.aop.impl;

import world.hzq.aop.PointExpression;
import world.hzq.aop.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理人解析器
 */
public class AgentParser {
    //代理人
    private final Agent agent;
    //增强方法的集合(标有before、after等注解的增强方法)
    private final Map<Class<?>,List<Entry<Method,PointExpressionImpl>>> adviceMethods = new ConcurrentHashMap<>();
    //拦截当前方法的切面方法集合
    private final Set<Method> interceptedMethods = new HashSet<>();

    public AgentParser(Agent agent) throws NoSuchMethodException {
        this.agent = agent;
        initialize();
    }

    /**
     * 通过拦截方式和通知方法获取切点表达式实现
     * @param way 拦截方式
     * @param adviceMethod 通知方法
     */
    public PointExpression getPointExpression(Class<?> way,Method adviceMethod){
        for (int i = 0; i < adviceMethods.get(way).size(); i++) {
            Entry<Method, PointExpressionImpl> entry = adviceMethods.get(way).get(i);
            if (entry.getMethod().equals(adviceMethod)) {
                return entry.getPointExpressionImpl();
            }
        }
        return null;
    }

    /**
     * 初始化被拦截的方法集合
     */
    public void initialize() throws NoSuchMethodException {
        //获取切面类中该切点表达式增强的方式(Before、After、AfterReturning、AfterThrowing、Around)
        for (Method declaredMethod : agent.getAspectClass().getDeclaredMethods()) {
            for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                if(declaredAnnotation == null){
                    continue;
                }
                List<Entry<Method, PointExpressionImpl>> entries = adviceMethods.computeIfAbsent(declaredAnnotation.annotationType(), k -> new LinkedList<>());
                String pointcutMethodName = null;
                if(declaredAnnotation instanceof Around){
                    Around around = (Around) declaredAnnotation;
                    pointcutMethodName = around.value().substring(0,around.value().length() - 2);
                }else if(declaredAnnotation instanceof Before){
                    Before before = (Before) declaredAnnotation;
                    pointcutMethodName = before.value().substring(0,before.value().length() - 2);
                }else if(declaredAnnotation instanceof After){
                    After after = (After) declaredAnnotation;
                    pointcutMethodName = after.value().substring(0,after.value().length() - 2);
                }else if(declaredAnnotation instanceof AfterReturning){
                    AfterReturning afterReturning = (AfterReturning) declaredAnnotation;
                    pointcutMethodName = afterReturning.value().substring(0,afterReturning.value().length() - 2);
                }else if(declaredAnnotation instanceof AfterThrowing){
                    AfterThrowing afterThrowing = (AfterThrowing) declaredAnnotation;
                    pointcutMethodName = afterThrowing.value().substring(0,afterThrowing.value().length() - 2);
                }
                if(pointcutMethodName == null){
                    break;
                }
                //获取切点方法
                Method pointcutMethod = agent.getAspectClass().getDeclaredMethod(pointcutMethodName);
                //获取切点表达式的解析器
                PointExpressionImpl pointcutParser = agent.getPointcutParser().get(pointcutMethod);
                //当前aspect的方法能被此表达式解析器解析
                entries.add(new Entry<>(declaredMethod, pointcutParser));
            }
        }
    }

    /**
     * 获取拦截当前方法的切面方法集合
     * @param method 当前方法
     * @return 拦截当前方法的切面方法集合
     */
    public Set<Method> getInterceptedAspectMethods(Method method){
        //已经获取过不需要再次获取
        if(interceptedMethods.size() == 0){
            for (Map.Entry<Class<?>, List<Entry<Method,PointExpressionImpl>>> entry : adviceMethods.entrySet()) {
                List<Entry<Method, PointExpressionImpl>> value = entry.getValue();
                for (Entry<Method, PointExpressionImpl> methodPointExpressionEntry : value) {
                    if (methodPointExpressionEntry.getPointExpressionImpl().matchMethodExecution(method)) {
                        interceptedMethods.add(methodPointExpressionEntry.getMethod());
                    }
                }
            }
        }
        return interceptedMethods;
    }

    /**
     * 获取拦截当前方法的指定方式的增强方法
     * @param AdviceWayClass 方式(前置、后置等)
     * @param method 当前方法
     */
    public List<Method> getInterceptedSpecifyAdviceWay(Class<? extends Annotation> AdviceWayClass,Method method) {
        List<Method> interceptedMethods = new ArrayList<>();
        for (Method aspectMethod : getInterceptedAspectMethods(method)) {
            if (aspectMethod.getDeclaredAnnotation(AdviceWayClass) != null) {
                interceptedMethods.add(aspectMethod);
            }
        }
        return interceptedMethods;
    }

    /**
     * 当前方法是否应该拦截
     * @param method 目标方法
     * @return true表示拦截,false表示放行
     */
    public boolean shouldIntercept(Method method) throws NoSuchMethodException {
        //获取拦截当前方法的切面方法集合(集合元素大于0即为有切面方法拦截当前方法)
        return getInterceptedAspectMethods(method).size() > 0;
    }

    private static class Entry<K,V>{
        private K method;
        private V pointExpressionImpl;

        public V getPointExpressionImpl() {
            return pointExpressionImpl;
        }

        public void setPointExpressionImpl(V pointExpressionImpl) {
            this.pointExpressionImpl = pointExpressionImpl;
        }

        public K getMethod() {
            return method;
        }

        public void setMethod(K method) {
            this.method = method;
        }

        public Entry(K method, V pointExpressionImpl) {
            this.method = method;
            this.pointExpressionImpl = pointExpressionImpl;
        }
    }
}
