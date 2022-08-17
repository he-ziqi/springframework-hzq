package world.hzq.ioc.impl;

import world.hzq.ioc.BeanDefinition;
import world.hzq.ioc.annotation.*;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * 通过包名和注解解析类文件并获取字节码文件
 */
public class PackageAndAnnotationResolve {
    public static final String FILE_PROTOCOL = "file";
    public static final String CLASS_SUFFIX = ".class";

    /**
     * configuration类不存在时默认加载所有包下带相应注解的bean
     */
    public static Set<BeanDefinition> defaultLoad() {
        Set<BeanDefinition> res = new HashSet<>();
        Class<?>[] accessAnnotations = new Class<?>[]{Service.class, Component.class, Controller.class, Repository.class, Bean.class};
        Set<Class<?>> classSet = getAll(accessAnnotations);
        for (Class<?> aClass : classSet) {
            BeanDefinition definition = createBeanDefinition(aClass, accessAnnotations);
            res.add(definition);
        }
        return res;
    }

    static Set<Class<?>> getAll(Class<?>[] targetAnnotations){
        Set<Class<?>> res = new HashSet<>();
        URL resource = getClassLoader().getResource("");
        if(resource != null){
            getDirClass(res,resource,targetAnnotations);
        }
        return res;
    }

    private static void getDirClass(Set<Class<?>> res, URL resource, Class<?>[] targetAnnotations) {
        File file = new File(resource.getPath());
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File curFile) {
                String path = curFile.getAbsolutePath();
                if (curFile.isDirectory()) {
                    for (File listFile : Objects.requireNonNull(curFile.listFiles())) {
                        accept(listFile);
                    }
                } else if (path.endsWith(".class")) {
                    add(res, path, targetAnnotations);
                    return true;
                }
                return false;
            }
        });
    }

    private static void add(Set<Class<?>> res, String path, Class<?>[] targetAnnotations) {
        String projectPath = Objects.requireNonNull(getClassLoader().getResource("")).getPath();
        path = path.substring(projectPath.length() - 1);
        path = path.substring(0,path.lastIndexOf("."));
        path = path.replace("\\",".");
        if(!path.contains("$")){
            try {
                Class<?> aClass = Class.forName(path);
                boolean accepted = false;
                for (Annotation annotation : aClass.getDeclaredAnnotations()) {
                    for (Class<?> targetAnnotation : targetAnnotations) {
                        if(targetAnnotation.equals(annotation.annotationType())){
                            accepted = true;
                            break;
                        }
                    }
                    if(accepted){
                        break;
                    }
                }
                if(accepted){
                    res.add(aClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定包下所有类的字节码文件
     * @param packageName 包名
     * @return 类字节码文件
     */
    public Set<BeanDefinition> getPackageClasses(String packageName,final Class<?>[] accessAnnotations){
        ClassLoader classLoader = getClassLoader();
        URL url = classLoader.getResource(packageName.replace(".", "/"));
        if (null == url) {
            System.out.println("this package is empty , packageName : " + packageName);
        }
        Set<BeanDefinition> res = null;
        //判断url是否为file
        assert url != null;
        if(Objects.equals(url.getProtocol(),FILE_PROTOCOL)){
            res = new HashSet<>();
            String path = url.getPath();
            try {
                path = URLDecoder.decode(path, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            getDirClasses(res,new File(path),packageName,accessAnnotations);
        }
        return res;
    }

    /**
     * 获取指定目录下的带有允许注解的所有字节码文件
     * @param res 结果集
     * @param file 文件对象
     * @param packageName 包名
     * @param accessAnnotations 允许的注解
     */
    private void getDirClasses(Set<BeanDefinition> res, File file, String packageName, final Class<?>[] accessAnnotations) {
        //不是目录则直接结束
        if(!file.isDirectory()){
            return;
        }
        //对目录内容进行过滤(包含子包)
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File curFile) {
                if (curFile.isDirectory()) {
                    for (File file : Objects.requireNonNull(curFile.listFiles())) {
                        accept(file);
                    }
                    return true;
                }
                String absolutePath = curFile.getAbsolutePath();
                if(absolutePath.endsWith(CLASS_SUFFIX)){
                    //如果是以.class结尾的,则加入到res中
                    addClassFile(res,absolutePath,packageName,accessAnnotations);
                }
                return false;
            }
        });
        if (null != files) {
            Arrays.stream(files).forEach(childFile -> getDirClasses(res,childFile,packageName, accessAnnotations));
        }
    }

    private void addClassFile(Set<BeanDefinition> res, String absolutePath, String packageName,final Class<?>[] accessAnnotations) {
        //将文件目录转换成包的形式
        addClass(res, absolutePath, packageName,accessAnnotations);
    }

    public static BeanDefinition createBeanDefinition(Class<?> target,final Class<?>[] accessAnnotations){
        //只放入类上带有特定注解的类文件
        try {
            for (Annotation targetAnnotation : target.getDeclaredAnnotations()) {
                for (Class<?> accessAnnotation : accessAnnotations) {
                    if (targetAnnotation.annotationType().equals(accessAnnotation)) {

                        //获取类上标注的注解的信息
                        Class<? extends Annotation> targetAnnotationClass = targetAnnotation.annotationType();
                        String beanName = null;
                        String scope = null;
                        boolean lazy = true;
                        Method valueMethod = null;
                        valueMethod = targetAnnotationClass.getDeclaredMethod("value");
                        beanName = (String) valueMethod.invoke(targetAnnotation);
                        if(!accessAnnotation.equals(Configuration.class)){
                            Method lazyMethod = targetAnnotationClass.getDeclaredMethod("lazy");
                            lazy = (boolean) lazyMethod.invoke(targetAnnotation);
                            scope = BeanDefinition.SCOPE_SINGLETON;
                            Scope scopeAnnotation = target.getDeclaredAnnotation(Scope.class);
                            if(scopeAnnotation != null && !scopeAnnotation.singleton()){
                                scope = BeanDefinition.SCOPE_PROTOTYPE;
                            }

                        }else{
                            beanName = (String) valueMethod.invoke(targetAnnotation);
                            //没有指定beanName时,默认使用类名的首字母小写后的字符串作为默认类名
                            scope = BeanDefinition.SCOPE_SINGLETON;
                            //configuration不能是懒加载(会导致容器少初始化beanDefinition)
                            lazy = false;
                        }
                        //没有指定beanName时,默认使用类名的首字母小写后的字符串作为默认类名
                        if("".equals(beanName)){
                            beanName = (char) (target.getSimpleName().charAt(0) + 32) + target.getSimpleName().substring(1);
                        }
                        //获取当前类字节码的所有直接或间接父类字节码和接口字节码
                        List<Class<?>> superClasses = new LinkedList<>();
                        getSupperClasses(target,superClasses);
                        List<Class<?>> interfaces = new LinkedList<>();
                        getInterfaces(target,interfaces);
                        return new BasePackagesBeanDefinition(target,target.getSimpleName(),beanName,scope,lazy,interfaces,superClasses);
                    }
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addClass(Set<BeanDefinition> res, String absolutePath, String packageName,final Class<?>[] accessAnnotations) {
        absolutePath = absolutePath.replace(File.separator,".");
        String className = absolutePath.substring(absolutePath.indexOf(packageName));
        className = className.substring(0,className.lastIndexOf("."));
        try {
            Class<?> target = Class.forName(className);
            //除过内部类
            if(!className.contains("$")){
                res.add(createBeanDefinition(target,accessAnnotations));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //递归获取目标字节码的所有接口
    public static void getInterfaces(Class<?> target,List<Class<?>> interfaces) {
        if (target == null || target.getInterfaces().length == 0) {
            return;
        }
        Class<?>[] targetInterfaces = target.getInterfaces();
        for (Class<?> targetInterface : targetInterfaces) {
            interfaces.add(targetInterface);
            getInterfaces(targetInterface,interfaces);
        }
    }

    //递归获取目标字节码的所有直接或间接父类
    public static void getSupperClasses(Class<?> target,List<Class<?>> supperClasses) {
        if(target == Object.class || target == null){
            return;
        }
        Class<?> superclass = target.getSuperclass();
        supperClasses.add(superclass);
        getSupperClasses(superclass,supperClasses);
    }

    private static ClassLoader getClassLoader(){
        ClassLoader classLoader = PackageAndAnnotationResolve.class.getClassLoader();
        if(classLoader == null){
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return classLoader;
    }
}
