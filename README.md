# springframework-hzq

### 1、核心功能实现
  ##### 1.1、IOC部分
  实现了bean的管理，如bean的定义、创建、获取、依赖注入等，实现spring ioc的常用注解功能，如Autowired、Configuration、ComponentScan、Value、Bean注解等。
  ##### 1.2、AOP部分
  实现了切点表达式的包扫描解析和注解解析，实现了前置通知、返回通知、异常通知、后置通知和环绕通知的切面方法的切入实现（目前仅采用JDK动态代理的方式对Aop进行实现）
### 2、项目截图
  ##### 2.1、IOC使用截图
  - **通过工具类方法获取ApplicationContext**
  
  ![image](https://user-images.githubusercontent.com/62747607/185086997-b6566de0-ee27-4ff7-8723-024d1bbcd4c4.png)
  
  - **Bean1、Bean2、Bean3类截图**
  
  ![image](https://user-images.githubusercontent.com/62747607/185088250-019e1cbf-9e05-4d86-b3e7-ee65b1804ac2.png)
  
  - **通过配置类获取ApplicationContext(Bean1、Bean2、Bean3同上)**
  
  ![image](https://user-images.githubusercontent.com/62747607/185089231-bced2b22-9d0d-4d90-9280-931ffdd47c1a.png)

  ##### 2.2、AOP使用截图
  - **通过包扫描切点表达式对bean包下所有类进行的所有方法进行增强**
  
  ![image](https://user-images.githubusercontent.com/62747607/185094934-e5731765-d545-4a84-baf5-06b4cb80492e.png)
  
  - **通过注解切点表达式对所有标注有目标注解的方法进行增强**
  
  ![image](https://user-images.githubusercontent.com/62747607/185097241-5c06b308-846b-4687-990f-e03ac8f3fbe6.png)

**结语：由于能力有限，代码中难免会出现错误，敬请大家批评指正。**
