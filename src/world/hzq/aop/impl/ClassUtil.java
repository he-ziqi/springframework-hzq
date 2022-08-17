package world.hzq.aop.impl;

import world.hzq.aop.exeception.NoSuchPackageException;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 字节码文件操作工具类
 */
public class ClassUtil {
    private static final String FILE_PROTOCOL = "file";
    private static final String CLASS_SUFFIX = ".class";

    private ClassUtil(){}

    /**
     * 获取包名下的所有类的字节码文件
     * @param packageName 包名
     * @param includeSubDirectory 是否包含子包
     * @return 结果集
     */
    public static Set<Class<?>> getClassesByPackageName(String packageName,boolean includeSubDirectory){
        ClassLoader classLoader = getClassLoader();
        Set<Class<?>> res = null;
        res = new HashSet<>();
        URL resource = classLoader.getResource(packageName.replace(".", "/"));
        if(resource == null){
            //包不存在
            throw new NoSuchPackageException("appear error when acquire classes\n\tcaused by : current package is not exists,packageName : " + packageName);
        }
        //资源是文件
        if(FILE_PROTOCOL.equals(resource.getProtocol())){
            //获取其字节码文件并将其存入res中
            getDirClasses(res,resource,packageName,includeSubDirectory);
        }else{
            //资源不是文件
            throw new NoSuchPackageException("appear error when acquire classes\n\tcaused by : documents currently required but provide " + resource.getProtocol());
        }
        return res;
    }

    /**
     * 获取目录中的字节码文件
     * @param res 字节码文件结果集
     * @param resource 资源路径
     * @param packageName 包名
     * @param includeSubDirectory 是否包含子包
     */
    private static void getDirClasses(Set<Class<?>> res, URL resource,String packageName,boolean includeSubDirectory) {
        File file = new File(resource.getPath());
        //对文件进行过滤
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File curFile) {
                //获取当前文件的绝对路径
                String path = curFile.getAbsolutePath();
                //是目录并且包含子包
                if(curFile.isDirectory() && includeSubDirectory){
                    //当前是目录,递归向下寻找子包
                    for (File file : Objects.requireNonNull(curFile.listFiles())) {
                        accept(file);
                    }
                }else if(path.endsWith(CLASS_SUFFIX)){
                    //获取.class文件
                    addClassToSet(res,path,packageName);
                }
                return true;
            }
        });
    }

    /**
     * 增加class文件到set集合
     * @param res 结果集
     * @param path 文件的绝对路径
     * @param packageName 包名
     */
    private static void addClassToSet(Set<Class<?>> res,String path,String packageName){
        //对文件路径切割获取全限定类名
        path = path.replace(File.separator,".");
        String className = path.substring(path.indexOf(packageName));
        className = className.substring(0,className.lastIndexOf("."));
        //除内部类外
        if(!className.contains("$")){
            res.add(getClassByClassName(className));
        }
    }

    public static Class<?> getClassByClassName(String className){
        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return targetClass;
    }

    /**
     * 获取类加载器
     */
    private static ClassLoader getClassLoader(){
        ClassLoader classLoader = null;
        classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader != null ? classLoader : ClassUtil.class.getClassLoader();
    }
}
