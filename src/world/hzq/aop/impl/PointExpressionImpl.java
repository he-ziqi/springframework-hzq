package world.hzq.aop.impl;

import world.hzq.aop.PointExpression;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * 切点表达式检查实现
 */
public class PointExpressionImpl implements PointExpression {
    private final PointParser pointParser;
    //切点表达式
    private final String pointcutExpression;
    //方法访问修饰符
    private String modifier;
    //返回值类型
    private String returnType;
    //包名
    private String packageName;
    //是否包含子包
    private boolean includeSubPackages;
    //类名
    private String className;
    //方法名
    private String methodName;
    //参数列表
    private String parameters;
    //是否为注解
    private boolean isAnnotation;
    //注解类型
    private Class<?> annotationType;
    //被增强的字节码文件集合
    private Set<Class<?>> adviceClasses;

    public PointExpressionImpl(String expression) {
        this.pointcutExpression = expression;
        pointParser = new PointParser(this);
        //解析切点表达式并获取切点表达式所增强的类的字节码文件
        adviceClasses = pointParser.parsePointcutExpression();
    }

    //确定表达式可以匹配类中的连接点
    @Override
    public boolean couldMatchJoinPointsInType(Class<?> givenClass) {
        //是否为注解切点表达式
        if (isAnnotation()) {
            for (Method method : givenClass.getDeclaredMethods()) {
                for (Annotation methodDeclaredAnnotation : method.getDeclaredAnnotations()) {
                    if (annotationType.equals(methodDeclaredAnnotation.annotationType())) {
                        return true;
                    }
                }
            }
            return false;
        }else { //表达式形式
            if("*".equals(className) && !includeSubPackages){ //通配类名但不包含子包
                return givenClass.getPackage().getName().equals(packageName);
            }else if("*".equals(className)){ //通配类名并且包含其子包
                return givenClass.getPackage().getName().contains(packageName);
            }else { //不是通配类名
                return givenClass.getName().equals(packageName + "." + className);
            }
        }
    }

    //确定切点表达式是否与给定方法匹配
    @Override
    public boolean matchMethodExecution(Method method) {
        //是否为注解切点表达式
        if(isAnnotation()){
            Class<?> annotationType = getAnnotationType();
            for (Annotation methodDeclaredAnnotation : method.getDeclaredAnnotations()) {
                if (methodDeclaredAnnotation.annotationType().equals(annotationType)) {
                    return true;
                }
            }
            return false;
        }else{ //表达式形式
            /**
             * 1、修饰符不限制(修饰符不限制时返回类型不能限制)：
             *      1.1、修饰符不限制,返回值不限制、方法名不限制、方法参数不限制  (* com.hzq.Service.*(..))
             *      1.2、修饰符不限制,返回值不限制、方法名不限制、方法参数限制    (* com.hzq.Service.*(int,double))
             *      1.3、修饰符不限制,返回值不限制、方法名限制、方法参数不限制(重载 * com.hzq.Service.test(..))
             *      1.4、修饰符不限制,返回值不限制、方法名限制、方法参数限制      (* com.hzq.Service.test(int,String))
             * 2、修饰符限制(修饰符限制时,返回类型必须限制)：
             *      2.1、修饰符限制、返回值限制、方法名不限制、方法参数不限制      (public void com.hzq.Service.*(..))
             *      2.2、修饰符限制、返回值限制、方法名不限制、方法参数限制       (public void com.hzq.Service.*(int,long))
             *      2.3、修饰符限制、返回值限制、方法名限制、方法参数不限制   (重载 public void com.hzq.Service.test(..))
             *      2.4、修饰符限制、返回值限制、方法名限制、方法参数限制         (public void com.hzq.Service.test(int))
             */
            if("*".equals(modifier) && "*".equals(returnType)){ //1
                return expressionMatchFourRules(method);
            }else if(!"*".equals(modifier) && !"*".equals(returnType)){ //2
                //方法修饰符包含解析出的方法修饰符的结果(因为基于接口的代理修饰符会默认加其它,所以用包含判断)
                if (returnType.equals(method.getReturnType().getSimpleName()) && Modifier.toString(method.getModifiers()).contains(modifier)) {
                    return expressionMatchFourRules(method);
                }
            }
        }
        return false;
    }
    //表达式匹配的四种规则
    private boolean expressionMatchFourRules(Method method){
        if("*".equals(methodName) && "..".equals(parameters)){ //1.1
            return true;
        }else if("*".equals(methodName)){ //1.2
            return paramLimit(method);
        }else if("..".equals(parameters)){ //1.3
            return method.getName().equals(methodName);
        }else { //1.4
            return method.getName().equals(methodName) && paramLimit(method);
        }
    }
    //方法名不限制,参数限制
    private boolean paramLimit(Method method){
        if("".equals(parameters) && method.getParameterCount() == 0){
            return true;
        }
        String[] params = parameters.split(",");
        if(params.length != method.getParameterCount()){
            return false;
        }
        for (int i = 0; i < method.getParameters().length; i++) {
            if (!method.getParameters()[i].getType().getSimpleName().equals(params[i])) {
                return false;
            }
        }
        return true;
    }

    public String getPointcutExpression() {
        return pointcutExpression;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isIncludeSubPackages() {
        return includeSubPackages;
    }

    public void setIncludeSubPackages(boolean includeSubPackages) {
        this.includeSubPackages = includeSubPackages;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public boolean isAnnotation() {
        return isAnnotation;
    }

    public void setAnnotation(boolean annotation) {
        isAnnotation = annotation;
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<?> annotationType) {
        this.annotationType = annotationType;
    }

    @Override
    public String toString() {
        return "PointExpressionImpl{" +
                "pointcutExpression='" + pointcutExpression + '\'' +
                ", modifier='" + modifier + '\'' +
                ", returnType='" + returnType + '\'' +
                ", packageName='" + packageName + '\'' +
                ", includeSubPackages=" + includeSubPackages +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters='" + parameters + '\'' +
                ", isAnnotation=" + isAnnotation +
                ", annotationType=" + annotationType +
                '}';
    }

    public Set<Class<?>> getAdviceClasses() {
        return adviceClasses;
    }
}
