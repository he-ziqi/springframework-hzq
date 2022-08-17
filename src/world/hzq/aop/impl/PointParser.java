package world.hzq.aop.impl;

import world.hzq.aop.exeception.PointcutExpressionException;

import java.util.HashSet;
import java.util.Set;

/**
 * 切点表达式的语法：execution([修饰符] 返回值类型 包名.类名.方法名(参数))
 * 修饰符可以省略
 * 返回值类型、包名、类名、方法名可以用*代替
 * 包名与类名之前一个点.代表当前包写的类，两个点…代表当前包极其子包下的所有类
 * 参数列表可以用两个点…代表任意类型、任意个数的参数
 * 切点表达式的解析,返回切点表达式的所有接口实现类的字节码文件
 */
public class PointParser {
    private final PointExpressionImpl pointExpressionImpl;

    public PointParser(PointExpressionImpl pointExpressionImpl) {
        this.pointExpressionImpl = pointExpressionImpl;
    }

    /**
     * 解析切点表达式并获取表达式增强方法的字节码文件集合(只获取不是注解的方式)
     */
    public Set<Class<?>> parsePointcutExpression(){
        //解析切点表达式
        resolvePointcutExpression();
        Set<Class<?>> res = null;
        //不是注解方式
        if(!pointExpressionImpl.isAnnotation()){
            //类是通配,则获取该切点表达式包下的所有类
            if("*".equals(pointExpressionImpl.getClassName())){
                res = ClassUtil.getClassesByPackageName(pointExpressionImpl.getPackageName(), pointExpressionImpl.isIncludeSubPackages());
            }else {
                res = new HashSet<>();
                String className = pointExpressionImpl.getPackageName() + "." + pointExpressionImpl.getClassName();
                //类不是通配,直接获取字节码文件即可
                Class<?> targetClass = ClassUtil.getClassByClassName(className);
                res.add(targetClass);
            }
        }
        return res;
    }

    /**
     * 对切点表达式进行解析
     */
    private void resolvePointcutExpression(){
        //前缀为execution(,后缀为))
        if(pointExpressionImpl.getPointcutExpression().startsWith("execution(") && pointExpressionImpl.getPointcutExpression().endsWith("))")){
            //获取正文内容的字符串
            int endIndex = pointExpressionImpl.getPointcutExpression().indexOf("))");
            String pointcutExpressionContentString = pointExpressionImpl.getPointcutExpression().substring(10, endIndex + 1);
            String[] strings = pointcutExpressionContentString.split(" ");
            boolean omit = false; //默认无省略
            //没有任何省略
            if(strings.length == 2){ //代表访问修饰符被省略(任何方法都被增强)
                pointExpressionImpl.setModifier("*");
                omit = true;
            }else if(strings.length != 3){ //长度为3代表无忽略
                //错误的切点表达式
                throw new PointcutExpressionException("the expression is illegal");
            }
            //方法访问修饰符被省略
            if(omit){
                //解析包名、类名、方法名、参数列表
                resolvePackageAndClassNameAndMethodNameAndParameters(strings,0);
            }else{
                //方法访问权限修饰符未省略
                if (strings[0].equals("*")) {
                    //如果访问权限修饰符为*,则出错(访问权限修饰符为*时,返回值必须省略)
                    throw new PointcutExpressionException("the expression is illegal\n\tcaused by : returnType must be omit when modifier is *");
                }else{
                    pointExpressionImpl.setModifier(strings[0]);
                    //解析包名、类名、方法名、参数列表
                    resolvePackageAndClassNameAndMethodNameAndParameters(strings,1);
                }
            }
        }else if(pointExpressionImpl.getPointcutExpression().startsWith("@annotation(") && pointExpressionImpl.getPointcutExpression().endsWith(")")){ //注解切点表达式
            //截取注解的全限定名,获取其注解的字节码文件
            String annotationClassName = pointExpressionImpl.getPointcutExpression().substring(12, pointExpressionImpl.getPointcutExpression().indexOf(')'));
            try {
                pointExpressionImpl.setAnnotationType(Class.forName(annotationClassName));
                pointExpressionImpl.setAnnotation(true);
            } catch (ClassNotFoundException e) {
                throw new PointcutExpressionException("the expression is illegal\n\tcaused by : this annotation type is not exists : " + annotationClassName);
            }
        }else{
            throw new PointcutExpressionException("the expression is illegal\n\tcaused by : the expression neither expression type or annotation type");
        }
    }

    /**
     * 解析类名、包名、方法名、参数类别
     */
    private void resolvePackageAndClassNameAndMethodNameAndParameters(String[] strings, int start){
        //返回值类型简单名称
        pointExpressionImpl.setReturnType(strings[start]);
        int len = strings[start + 1].length();
        final StringBuilder sb = new StringBuilder();
        //参数列表名子符串的倒数第1、2、3个字符
        char c1 = strings[start + 1].charAt(len - 2);
        char c2 = strings[start + 1].charAt(len - 3);
        char c3 = strings[start + 1].charAt(len - 4);
        //通配所有参数
        if(c1 == '.' && c2 == '.' && c3 == '('){
            pointExpressionImpl.setParameters("..");
        }else if(c1 == '('){ //无参
            pointExpressionImpl.setParameters("");
        }else{ //有参数
            int i = 2; //从参数字符串的倒数第一个字符开始拼接
            while(c1 != '('){
                sb.append(c1);
                i++;
                c1 = strings[start + 1].charAt(len - i);
            }
            //参数字符串
            pointExpressionImpl.setParameters(sb.reverse().toString());
            //清空sb
            sb.setLength(0);
        }
        //获取参数列表字符串的开始索引(即为方法名的结束索引位置的下一个)
        int endIndex = len - (pointExpressionImpl.getParameters().length() + 2);
        //获取包名及其类名和方法名
        String name = strings[start + 1].substring(0, endIndex);
        //count = 1表示方法名,2表示类名
        int count = 1;
        endIndex = -1;
        for (int i = name.length() - 1; i > 0; i--) {
            c1 = name.charAt(i);
            if(c1 == '*'){
                if(count == 2){
                    //类名通配
                    pointExpressionImpl.setClassName("*");
                    count = 3;
                }else if(count == 1) {
                    //方法名通配
                    pointExpressionImpl.setMethodName("*");
                    count = 2;
                }
                sb.setLength(0);
                continue;
            }
            if(c1 == '.' && sb.length() > 0){
                if(count == 2){
                    //获取类名
                    pointExpressionImpl.setClassName(sb.reverse().toString());
                    endIndex = i;
                    break;
                }else if(count == 1){
                    //获取方法名
                    pointExpressionImpl.setMethodName(sb.reverse().toString());
                    count = 2;
                }
                sb.setLength(0);
                continue;
            }
            if(c1 != '.'){
                sb.append(c1);
            }
            if(count == 3){
                endIndex = i;
                break;
            }
        }
        pointExpressionImpl.setPackageName(name.substring(0, endIndex));
        if(pointExpressionImpl.getPackageName().endsWith(".")){
            //包名包括当前包及其所有子包
            pointExpressionImpl.setPackageName(pointExpressionImpl.getPackageName().substring(0,pointExpressionImpl.getPackageName().length() - 1));
            pointExpressionImpl.setIncludeSubPackages(true);
        }//否则包名只包括当前包
    }

}
