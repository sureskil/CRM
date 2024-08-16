# 简介

- 该项目基于Layui框架与FreeMarker模板引擎
- 导入数据库，配置yml文件中的数据库相关数据
- 启动服务，访问地址：http://localhost:8080/crm/index进入登录页面
  - 账号可用：admin、scott，其余账户可以通过数据库中的t_user表查看
  - 密码：都为123456





# 一、数据库



## 1、表结构详情

- eg

  ![image-20240314142715704](TTP/image-20240314142715704.png)



## 2、mybatis-spring-boot-starter

- 导入这个依赖后要进行数据库的相关配置，否则项目启动会报错！



## 3、数据库链接驱动

- mysql-connector-java 8以上驱动加.cj.：com.mysql.cj.jdbc.Driver
- 8一下不加

```yaml
datasource:
    type: com.mchange.v2.c3p0.ComboPooledDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/crm?userUnicode=true&characterEncoding=utf8&userSSL=true
    username: root
    password: 123456
```



## 4、加时区

```yaml
url: jdbc:mysql://localhost:3306/shop?userUnicode=true&characterEncoding=utf8&userSSL=true&serverTimezone=Asia/Shanghai
```



## 5、tinyint

```xml
url="jdbc:mysql://localhost:3306/ego?useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=Asia/Shanghai&amp;tinyInt1isBit=false"
```







# 二、环境搭建

- 讲师用的是：`parent标签`（相关Spring Boot依赖不需要写版本号），没有`dependencyManagement标签`
- 自己用的是：SpringBoot模板的初始化，`dependencyManagement标签`（相关Spring Boot依赖不需要写版本号），没有parent标签
- 首页测试：resources/static文件夹下建立index.html



## 1、依赖



- 讲师-依赖

  ![image-20240314144700250](TTP/image-20240314144700250.png)

  

  ![image-20240314155328562](TTP/image-20240314155328562.png)



- 自己-依赖
  - 暂时没有添加：mybatis-generator依赖
  - 要自己通过IDEA组件来完成数据库代码的逆向生成！



## 2、配置文件

- freemarker的配置是在spring配置下的！



- 讲师的

  ![image-20240314162757269](TTP/image-20240314162757269.png)

- 自己的，和老师的没有什么区别

  ```yml
  server:
    port: 8080
    servlet:
      context-path: /crm
  
  spring:
    datasource:
      type: com.mchange.v2.c3p0.ComboPooledDataSource
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/db6?userUnicode=true&characterEncoding=utf8&userSSL=true
      username: root
      password: 123456
  
    freemarker:
      suffix: .ftl
      content-type: text/html
      charset: utf-8
      template-loader-path: classpath:/views/
  
    # 在maven插件中进行配置后，这里的配置可以不需要；也可以配置这里，不再maven插件中配置
    devtools:
      restart:
        enabled: true
        additional-paths: src/main/java
  
  mybatis:
    mapper-locations: classpath:/mappers/*.xml
    type-aliases-package: com.xxxx.crm.vo; com.xxxx.crm.query; com.xxxx.crm.dto
    configuration:
      # 开启驼峰功能，使得数据库中的字段样式（user_name）可以和实体类中的属性字段样式（userName）一一对应
      map-underscore-to-camel-case: true
  
  # 设置日志打印级别
  logging:
    level:
      com:
        xxxx:
          crm:
            dao: debug
  
  # pagehelper分页配置
  pagehelper:
    helper-dialect: mysql
  ```





# 三、登录功能

- 功能的思考、使用都是在Controller层
- 功能的实现在Service层
- 所以大致上，实现的过程有几步就有几个controller



- 后端的实现是为了向前端传递需要的数据
- 前端的实现是为了根据后端返回的数据进行对应的操作
  - 内容
    1. 根据code值来判断选择哪一个方法执行
    2. 通过msg值来提示操作进行的结果
    3. 通过result对象来进行对应位置数据的获取
  - 本来前端显示的提示信息都是由后端反馈的（通过js代码），但是前端也可以直接去写一些操作提示信息
    1. 比如账号、密码不能为空，这些提示都是不需要后端反馈就可以直接设置的
    2. 并且html的页面表单在点击跳转后都会进行跳转，如果不想进行跳转，需要用js代码进行控制
    3. 所以前端是一定要需要js代码的！



## 1、后端实现步骤

### 1、首先要能访问到登录页面

- 这个功能被写在IndexController中

- 直接返回页面，不需要Service、Dao（Mapper）层控制

- 登录页面：views/index.ftl

  

### 2、通过用户名查询对象

- 通过用户名（或其他的访问方式所需要的属性字段）来查询数据库，返回对应的对象

- 这个功能被写在UserController中

- 数据的查询是不需要密码的，密码主要是用来判断认证！！

  ---

1. 在Dao中写对应的方法
   - 返回User对象
2. 在Mapper写对应的sql语句
   - ==sql中的参数名要与Dao方法中的参数名一致==
   - ==而Dao中的参数名，一般与实体类的属性名一致==
3. 在Service接口层写要实现的功能（==不是对应Dao中方法的功能，Dao中方法只是Service层功能实现的一部分==）
   - 返回User
4. 写Service接口的实现类，在里面写功能逻辑
5. 在Controller层进行调用
   - 返回User
   - controller层需要返回json格式的对象，所以别忘了加@ResponseBody
   - 不加@ResponseBody会返回一个视图，因为视图不存在会报404
6. 使用Postman进行测试，成功后进行下一步



## 2、后端具体实现

### 1、判断参数（name、pwd）是否为空

1. 在service层的方法中添加pwd参数

   - sql查询的条件只有name（和is_valid），但是在service中的登录判断需要name和pwd

2. 创建一个自定义异常

   ```java
   public class ParamsException extends RuntimeException{
   
       private Integer code = 300;
       private String msg = "参数异常";
   
       public ParamsException() {
           // 通过源码查看可以知父方法需要一个参数message（报错信息）：public RuntimeException(String message)
           // 这里的设置表明如果抛出这个自定义异常的时候，没有传入异常信息msg，就会默认统一显示异常信息："参数异常"
           super("参数异常");
       }
   
       public ParamsException(Integer code) {
           // 因为这个方法中也没有传入异常信息msg，所以也需要设置统一默认异常信息："参数异常"
           super("参数异常");
           this.code = code;
       }
   
       public ParamsException(String msg) {
           // 这里有传入异常信息，所以不需要设置异常信息
           super("msg");
           this.msg = msg;
       }
   
       public ParamsException(Integer code, String msg) {
           super("msg");
           this.code = code;
           this.msg = msg;
       }
   
       public Integer getCode() {
           return code;
       }
   
       public void setCode(Integer code) {
           this.code = code;
       }
   
       public String getMsg() {
           return msg;
       }
   
       public void setMsg(String msg) {
           this.msg = msg;
       }
   }
   ```

   

3. 原始代码，判断-->抛出对应的异常

   ```java
   if (StringUtils.isBlank(userName)) {
       throw  new ParamsException("用户名不能为空！");
   } else if (StringUtils.isBlank(userPwd)) {
       throw new ParamsException("密码不能为空！");
   }
   ```

4. 但是因为后面经常要用这样的方法（判断-->抛出对应的异常），所以应该封装一个工具类进行判断

5. 最后这判断功能都可以封装成一个方法，这样代码会简洁易读

   - 让方法自动生成，然后把内容放进去！



### 2、得到用户对象后，判断是否为null

- 我们要求name和pwd必须不能为空
- 但是在name、pwd都存在的条件下，name如果不是数据库中存在的内容，就会返回查询结果：null
- 所以要有null判断



### 3、判断密码是否正确

- 原始写法

  ```java
  // 判断密码是否正确，因为一个是明文，一个是密文，所以要将明文密码（前端传递过来的）加密后在进行比较
  String encode = Md5Util.encode(userPwd);
  AssertUtil.isTrue(!encode.equals(user.getUserPwd()), "密码不正确!");
  ```

- 但为了代码的简洁易读，还是把这部分的代码封装成一个方法



### 4、Service层创建新对象，封装需要返回的数据

- 之前返回的是User对象，有很多不需要的属性数据
- 所以只把需要返回的数据新封装一个对象：id、userName、trueName
- 同样的，把这部分的代码封装为一个方法



### 5、更改Controller的返回对象

- 经过步骤4后，controller的返回对象从User --> UserModel，但还是不够

- 在Controller层，除了要返回的数据，还要返回状态值code、报错信息msg，所以需要把这些数据重新封装到一个对象中！

- 这里如果controller层不处理报错信息的话，出错的效果是这样的

  ![image-20240315140312308](TTP/image-20240315140312308.png)

- 设置了controller处理报错后，出错的效果是这样的

  ![image-20240315143859240](TTP/image-20240315143859240.png)

- 通过这里也可以看出来，自己设置异常处理是必须的，否则代码会按照默认的异常信息输出一堆看起来很费劲的提示！



## 3、前端实现步骤

- 通过index.ftl可以知道，需要一个index.js文件
  1. 保证用户输入的账号、密码都不为空
  2. 发送请求后得到后端反馈的数据
  3. 根据code值进行判断要进行的方法
     1. 如果成功的话，存入cookie，并进行页面的跳转
     2. 失败的话，显示对应的提示信息



## 4、前端具体实现

### 1、表单提交事件

1. 判断用户输入的账号、密码==都==不为空，有两种方法实现

   1. 通过html标签设置required参数，来保证输入的参数不为空（null）也不为空字符串（""）

   2. 通过js判断保证参数不为空，别忘了：return false;

      ```js
      if (data.field.username == null || data.field.username.trim() == "") {
          layer.msg("用户名称不能为空！", {icon: 5});
          return false;
      } else if (data.field.password == null || data.field.password.trim() == "") {
          layer.msg("用户密码不能为空！", {icon:5});
          return false;
      }
      ```

      关于这段代码，判断不能为null，也不能为""，后端用的是StringUtils工具类，简单便捷

      所以后端经常使用的话，也可以写一个类似的工具类，比如

      ```js
      // 这里index.js被使用的地方法是index.ftl，所以index.ftl也要引入util.js文件
      // 这样index.js中才可以用util.js中的方法！否则即使写的时候有提示也无效！
      function isEmpty(str) {
          if (str == null || str.trim() == "") {
              return true;
          }
          return false;
      }
      ```

   3. 其实这两种方法任选其一就行，效果是一样的

   4. 并且，就算前端这两种方法都不用，后端也会写参数不能为空的代码，最终效果和前端表达的内容是一样的

      

2. 在layui官网的组件中找到form的提交事件，然后copy事件框架写自己需要的内容

3. return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可

4. 需要ajax的访问

   1. 在[Ajax中文网 (p2hp.com)](https://ajax.p2hp.com/)可以找到对应的`POST JSON`访问方法
   2. ajax的使用，需要使用Jquery框架！！

5. 先写好路径、data中的参数，在success中随便写点东西，然后点击按钮，看ajax绑定是否成功

6. 加入code判断

   1. 如果成功的话，存入cookie，并进行页面的跳转，页面跳转需要在IndexController中设置
   2. 失败的话，显示对应的提示信息



- 至此，登录功能完毕！



# 四、给cookie中用户ID加密

- 虽然正常的登录认证、成功登录的功能已经实现，但是可以看到cookie中用户ID是明文，所以需要加密



## 1、后端实现步骤

1. 不需要访问新的页面
2. 在把数据封装到UsereModel之前，给用户ID加密就可以了
3. 具体步骤封装成一个方法



## 2、后端具体实现

1. 原本想的是，把id加密后再赋值给user对象
2. 但问题是加密后得到的是String格式的数据，user对象给id赋值需要Integer格式的数据
3. 所以需要另一种思路，把UserModel中id的数据类型改为String
4. 然后不给user对象的id赋值，而是直接给UserModel对象中的id赋值即可！



# 五、在右上角显示登录用户的信息

- 在登录成功后，右上角显示登录用户的用户名！



## 1、后端实现步骤

1. 不需要访问新的页面
2. 在有域对象的前提下，前端直接给模板中对应位置赋值即可！所以要给域对象中放入登录的用户对象
3. 直接在控制页面路径的controller中写功能实现，没有service层



## 2、后端具体实现

1. 刚开始想的是从域对象中直接取值，但后来发现没有这个域对象的值
2. 所以要先从cookie中获取用户的id字符串
3. id字符串解码后，用于id查询用户对象；这个查询的具体方法
   1. 老师用的是：注入service对象后，userService.selectByPrimaryKey
   2. 自己用的是：注入dao对象后，userMapper.selectByPrimaryKey
   3. 因为老师的service类继承了BaseService，所以他的Service有selectByPrimaryKey
   4. 我的service没有继承BaseService，但是dao中有对应的selectByPrimaryKey方法，所以直接拿来用了
   5. 一般不在controller层直接用userMapper的原因是，userMapper只是service层中实现功能的一部分，单独使用userMapper是不够的，所以controller注入的是service层的对象！
   6. 而这里没有复杂的功能逻辑，所以直接用userMapper去查找需要的数据就可以了！
4. 把查到的用户对象放到request作用域中



# 六、修改密码



## 1、后端实现步骤

1. 首先要能访问要密码页面

   1. 这个页面是views/user/password.ftl

      1. 修改密码的页面要在主页的位置显示，不单独打开，要再main中修改data-iframe-tab位置的路径
      2. 之前的页面显示虽然都和user表有关，但都是登录前后自动显示的页面，所以不放在user目录下
      3. 但是修改密码页面是在登录后点击相关按钮才显示的，所以应该放在user目录下

   2. 和password.ftl放在user目录下一样，路径要在UserController中添加

      1. 因为之前UserController中没有返回过页面（页面都是在IndexController中返回的）
      2. 所以要使路径生效，UserController需要extends BaseController，因为BaseController中有ctx路径！（项目路径）

      

2. 密码修改的实现

   - 这个功能被写在UserController中

     ---

   1. Dao中有相关的修改方法：updateByPrimaryKeySelective
   2. Mapper中也有对应的sql语句
   3. 在Service层进行相关功能的编辑
      1. 先通过id获取用户对象
      2. 给id对象重新赋予密码，在赋予前进行MD5加密
      3. 在修改密码的同时，还要把对应对象的update时间给更新了！
      4. 使用updateByPrimaryKeySelective进行更新
      5. 返回数据为：Integer
   4. 在Controller层创建访问这个方法的控制路径，返回数据类型为：Integer
   5. 打开postman进行测试，如果程序运行成功，会显示返回值为1！



## 2、后端具体实现

1. 测试通过后，根据实际要求，在功能方法中添加重复密码、旧密码，以前是两个参数，现在是四个
2. 在Service层进行一些判断，复杂的部分封装为方法，便于代码的简洁易读
   1. 输入参数不能为空
   2. 新旧密码不能相同
   3. 两次输入的新密码不同
   4. 判断用户是否存在
   5. 判断旧密码是否正确
3. 这里返回的数据很简单，所以不需要对Service层的返回数据进行重新封装
4. 在Controller层
   1. 把返回对象改为RestInfo对象，增加code值，msg值
   2. id值要从cookie中获取，==要把Controller中原先名为id的参数换位request参数，但是Service层的方法不需要改动！！！==



## 3、前端实现步骤

- 通过index.ftl可以知道，需要一个password.js文件
  1. 保证用户输入的账号、密码都不为空
  2. 发送请求后得到后端反馈的数据
  3. 根据code值进行判断要进行的方法
     1. 如果成功的话，存入cookie，并进行页面的跳转
     2. 失败的话，显示对应的提示信息



## 4、前端具体实现

1. 这是一个标准的表单提交事件
   1. 导入layui的表单使用模块
   2. 导入表单提交事件的模板
   3. 链接对应按钮的lay-filter
2. 和登录功能类似，参数非空判断有一下几种方法
   1. html的required属性值应用
   2. js的判断控制
3. 除了参数非空判断，这里还需要对输入的密码进行初步校验
   1. 新旧密码不能相同
   2. 新密码与重复密码是否相同
4. ajax请求提交，==ajax的使用需要jquery对象！==
5. 判断code值
   1. 如果code=200，弹出提示框：密码修改成功；跳转到index页面（window.==parent==.location.href）
   2. 否则弹出对应的提示信息，别忘了加表情图标！
6. 前端操作修改成功后
   1. 增加修改成功后的弹出提示框：3秒后跳转
   2. ==在弹出提示框的同时，把原本的cookie删除掉，否则原本的cookie还是在的，系统还是处于一个登录的状态！！==
   3. 增加js的参数非空判断，与密码的初始校验
7. 至此，修改密码功能全部完成！



# 七、退出功能



## 1、前端实现步骤

- 不需要访问新页面，也不需要访问后端控制器
- 点击退出按钮后，经过js的控制
  1. 清空cookie
  2. 跳转到登录页面



## 2、前端具体实现

1. 这个功能的实现在main.js中编写
2. 要通过js去控制，就要想办法让这个退出按钮绑定到js
   1. 之前是通过表单的lay-filter绑定js
   2. 现在没有，直接通过==类选择器.click();==绑定js



# 八、全局异常处理

> 如果没出错，那返回的是自己创建的ResultInfo，出现异常返回的是全局异常处理创建的ResultInfo！！
>
> 如果出错那么返回数据就是null；只有不出错的时候，才会去添加数据，返回controller中创建的的ResultInfo对象



## 1、后端实现步骤

- 不需要访问新页面，也不用访问特定的后端控制器，也不需要前端有什么操作
- 但是需要后端加一个全局异常处理器类！



## 2、后端具体实现

> 访问只有通过后端并出错的时候，全局异常处理才会中的内容才会参与显示，否则只会显示error页面，但是没有提示信息
>
> 比如：如果一个页面没有后端控制路径，那么在访问这个页面的时候，回出现error页面，但是没有提示信息
>
> 如果错误出现在访问页面的控制中，但是不属于自定义异常，那么提示信息会是：默认的异常处理！（好处是没有杂乱的异常信息，坏处是不知道究竟是什么原因导致的）



1. 这个功能类似于工具类，写过一遍之后，就像数据库逆向生成代码的generator.java文件一样，再次使用的时候，直接用就好了

2. 创建一个类implements HandlerExceptionResolver，重写其中的方法

   1. 这样的话除了自定义的异常外，其他异常会返回这个error页面

      ==这个页面的格式内容可以自己定义（自己写一个error页面）==

      ==但是，这个error页面不需要专门去定义控制路径（路径是Spring Boot默认的！)==

   2. 比如在控制welcome页面的controller中添加：int i = 1/0;那么在访问welcome页面时，就是跳转到这个页面

   3. 但是只这么写是不行的，因为这个error页面，并不会告诉使用者到底发生了什么错误，只会显示：默认的异常处理

   ```java
   @Override
   public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
   
       ModelAndView mv = new ModelAndView("error");
       mv.addObject("error", "默认的异常处理");
   
       return mv;
   }
   ```

3. 所以要加一些判断，返回的是视图，还是JSON格式数据

   1. ==别忘了加@Component==
   2. 并且这个类的书写需要：fastjson依赖，其他json依赖也可以

   ```java
   package com.xxxx.crm;
   
   import com.alibaba.fastjson.JSON;
   import com.xxxx.crm.base.ResultInfo;
   import com.xxxx.crm.exceptions.ParamsException;
   import org.springframework.stereotype.Component;
   import org.springframework.web.bind.annotation.RequestBody;
   import org.springframework.web.bind.annotation.ResponseBody;
   import org.springframework.web.method.HandlerMethod;
   import org.springframework.web.servlet.HandlerExceptionResolver;
   import org.springframework.web.servlet.ModelAndView;
   
   import javax.servlet.http.HttpServletRequest;
   import javax.servlet.http.HttpServletResponse;
   import java.io.IOException;
   import java.io.PrintWriter;
   import java.lang.reflect.Method;
   
   /**
    * 全局异常处理器
    */
   @Component
   public class GlobalExceptionResolver implements HandlerExceptionResolver {
   
       /**
        * 可以看到重写的方法需要返回一个MV对象
        *
        * @param httpServletRequest
        * @param httpServletResponse
        * @param handler 拦截的方法
        * @param e 异常对象
        * @return
        */
       @Override
       public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {
   
           ModelAndView mv = new ModelAndView("error");
           mv.addObject("msg", "默认的异常处理");
   
           /**
            * 判断方法的返回值类型
            */
           if (handler instanceof HandlerMethod) {
               // 类型转换
               HandlerMethod handlerMethod = (HandlerMethod) handler;
               // 得到出问题的方法
               Method method = handlerMethod.getMethod();
               // 得到方法名
               System.out.println("当前异常的方法名："+method.getName());
   
               /**
                * 判断方法上是否有指定注解
                * 这里的方法应该是反射
                */
               ResponseBody responseBody = method.getDeclaredAnnotation(ResponseBody.class);
   
               // 如果requestBody的值为空，表示方法上没有这个注解，返回的是一个视图
               if (responseBody == null) {
                   /**
                    * 方法返回的是：视图
                    */
   
                   // 判断是否是自定义异常信息
                   if (e instanceof ParamsException) {
                       ParamsException p = (ParamsException) e;
                       mv.setViewName("error");
                       mv.addObject("code", p.getCode());
                       mv.addObject("msg", p.getMsg());
                   }
   
                   return mv;
   
               } else {
                   /**
                    * 方法返回的是：JSON格式数据
                    */
   
                   ResultInfo resultInfo = new ResultInfo();
                   resultInfo.setCode(300);
                   resultInfo.setMsg("操作失败！");
   
                   // 判断是否是自定义异常
                   if (e instanceof ParamsException) {
                       ParamsException p = (ParamsException) e;
                       resultInfo.setCode(p.getCode());
                       resultInfo.setMsg(p.getMsg());
                   }
   
                   // 设置想要类型及编码格式
                   httpServletResponse.setContentType("application/json; charset=UTF-8");
                   // 得到字符输出流
                   PrintWriter out = null;
                   try {
                       out = httpServletResponse.getWriter();
                       // 将响应结果输出
                       out.write(JSON.toJSONString(resultInfo));
   
                       out.flush();
                       out.close();
                   } catch (IOException ex) {
                       e.printStackTrace();
                   }
   
                   return null;
   
               }
           }
   
           return mv;
       }
   }
   ```

4. 写完以后，去掉Controller层中的try...catch...代码！



# 九、登录拦截功能



## 1、后端实现步骤

1. 不需要访问新页面，也不用访问特定的后端控制器，也不需要前端有什么操作
2. 但是需要后端加一个拦截类！并进行配置
3. 还要在全局异常处理器中进行配置，因为拦截器如果起作用会抛出异常



## 2、后端具体实现



### 2.1、创建一个拦截器

1. 创建一个拦截器类（==放在拦截器目录中==），需要extends HandlerInterceptorAdapter，重写preHandle方法

2. 从cookie中获取用户id

3. 注入UserMapper对象，然后用id查出对应的用户对象

4. 如果==id==或==查出的对象==为null的话，表明用户没有登录，抛出未登录异常

5. 书写未登录异常，写法和其他自定义异常一样

   ==注意这里是没有@Component，因为会在配置类中使用@Bean来实例化这个拦截器类==

   ```java
   public class NoLoginInterceptor extends HandlerInterceptorAdapter {
   
       @Autowired
       private UserMapper userMapper;
   
       @Override
       public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
   
           // 从cookie中获取用户ID
           Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
   
           // 判断用户ID是否为空，且用户对象是否存在
           if (userId == null || userMapper.selectByPrimaryKey(userId) == null) {
               throw new NoLoginException();
           }
   
           return true;
       }
   
   }
   ```

   

### 2.2、在全局异常处理类中设置

1. 在全局异常处理类中添加相关判断，放在项目目录下，与服务启动类在同一个目录下

   ```java
   /**
    * 判断是否是非法登录异常
    * 这里单独添加的原因是：其他异常要么去错误页面error，要么return null；现在要去index页面
    */
   if (e instanceof NoLoginException) {
       ModelAndView mv1 = new ModelAndView("redirect:/index");
       return mv1;
   }
   ```



### 2.3、配置拦截器

1. 配置拦截器，放在配置文件夹中！

   ```java
   @Configuration
   public class MvcConfig extends WebMvcConfigurerAdapter {
   
       /**
        * 实例化拦截器
        */
       @Bean
       public NoLoginInterceptor noLoginInterceptor() {
           return new NoLoginInterceptor();
       }
   
       @Override
       public void addInterceptors(InterceptorRegistry registry) {
   
           // 配置noLoginInterceptor拦截器的拦截策略！
           registry.addInterceptor(noLoginInterceptor())
                   // 需要拦截的资源路径：项目路径下的所有内容
                   .addPathPatterns("/**")
                   // 不需要拦截的过滤规则：访问登录页、访问登录功能、各种静态资源目录
                   .excludePathPatterns("/index", "/user/login", "/css/**", "/images/**", "/js/**", "/lib/**");
       }
   
   }
   ```



### 2.4、eg：未登录异常

1. eg，未登录异常举例

   ```java
   /**
    * 自定义参数异常
    */
   public class NoLoginException extends RuntimeException {
       private Integer code=300;
       private String msg="用户未登录!";
   
   
       public NoLoginException() {
           super("用户未登录!");
       }
   
       public NoLoginException(String msg) {
           super(msg);
           this.msg = msg;
       }
   
       public NoLoginException(Integer code) {
           super("用户未登录!");
           this.code = code;
       }
   
       public NoLoginException(Integer code, String msg) {
           super(msg);
           this.code = code;
           this.msg = msg;
       }
   
       public Integer getCode() {
           return code;
       }
   
       public void setCode(Integer code) {
           this.code = code;
       }
   
       public String getMsg() {
           return msg;
       }
   
       public void setMsg(String msg) {
           this.msg = msg;
       }
   }
   ```

   

# 十、记住我功能

- ==chrome、edge等浏览器开启了“继续浏览上次打开的网页”这个功能时，会导致重启浏览器后session cookie还存在==



## 1、前端实现步骤

1. 这个功能的实现主要是更改cookie在浏览器的存储时间，所以和后端没有什么关系



## 2、前端具体实现

1. 在index页面（登录页面）中添加记住我复选框

   ```xml
   <div class="layui-form-item">
       <input type="checkbox" name="rememberMe" id="rememberMe" value="true" lay-skin="primary" title="记住密码">
   </div>
   ```

   

2. 在js中进行判断，复选框被选中，则进行cookie值存储时间的改变，否则不做任何事；判断方法有几种如下

   1. `if (data.field.rememberMe)`
      1. remember为复选框的默认值true
      2. 不选择的话data.field.rememberMe的值为undefined，js中转为boolean值为false
   2. `if($("#rememberMe").prop("checked"))`
      1. 通过选择器选中复选框，判断其是否被选中

   ```js
   if (data.field.rememberMe) {
       // 如果勾选了记住密码的复选框，按指定时间存储存cookie
       $.cookie("id", data1.result.id, {expires: 7});
       $.cookie("userName", data1.result.userName, {expires: 7});
       $.cookie("trueName", data1.result.trueName, {expires: 7});
   } else {
       // 存cookie
       $.cookie("id", data1.result.id);
       $.cookie("userName", data1.result.userName);
       $.cookie("trueName", data1.result.trueName);
   }
   ```

   

3. `if($("#rememberMe").val())`，是不起效果的！

   1. 在js中，null、" "、0转换boolean值时为false
   2. 在js中，非0、非空转换boolean值时为true
   3. val() 方法返回或设置被选元素的值，如果不勾选复选框，val()的结果是off，转boolean值还是true
   4. 所以这里用val()起不到判断的效果



# 11、营销机会管理CRUD



## 1、获取营销机会管理的Layui数据表格

### 1、后端实现步骤

1. 需要访问到一个新页面，连把新页面连通
2. 确定查询方法
   1. 自动生成的方法中是没有对应的查询方法的，需要自己写
   2. 本来第一步的实现，只要查询对应表所有的内容（就是select *）返回，分页即可
   3. 但是可以知晓的是，后面还需要通过条件来查询这个数据表格的内容
   4. 所以==这个查询方法本质上是一个多条件查询==，无条件显示全部数据，有条件显示筛选数据
3. service查询到数据后，还需要分页，返回一个Layui数据表格需要的数据模型
4. controller层获取数据后，封装需要参数的数据模型返回给前端



### 2、后端具体实现

1. Sql语句设计
   1. 条件主要有：客户没、创建人、分配状态，sql中的where将变为`<where>...<if>`
   2. 之前的查询中，查询条件比较简单，只需要传入单一的String、Integer值
   3. 现在比较复杂，一次性需要多个条件，并且上面的三个条件指不定会用到几个，可能一个没有也可能全都有
   4. 所以需要一个数据模型（查询对象）来存放查询条件
   5. 本来查询对象中只需要放3个条件，但是因为分页的需要，所以一般数据表格的查询还需要limit和page参数
   6. 因为limit和page参数是全部都需要的，所以可以将其封装，其他的查询对象去继承这个封装
2. 在dao与mapper.xml中写对应的内容
3. service层
   1. service需要返回给controller的内容是
      1. 分好页的SaleChance对象集合
      2. code，count、msg（之前code、msg是作为异常处理放在controller进行处理，这里的三个数据是layui数据表格返回数据的固定格式需要的内容，所以在serevice写）
      3. 所以应该返回给controller的而数据格式是一个装有需要数据的Map类型对象
   2. 开启分页
   3. 进行数据查询
   4. 得到分页对象，进行分页
   5. 把需要返回给数据表格的数据封装到数据模型map中；测试时
      1. msg先写：" "
      2. code先写：0
4. controller层
   1. controller需要返回给前端的内容是：service层返回的map集合
   2. controller方法参数为查询对象，在没有前端给参的情况下，默认条件为空，page=1，limit=10
   3. 直接调用service中的方法得到结果并返回
   4. 因为这里是给前端生成数据表格，不需要code和msg的信息进行判断，所以直接返回结果即可，没有进行ResultInfo的封装返回
5. postman测试
   1. 是否可以返回表中所有数据
   2. 条件查询是否有效！
6. 如果对返回的时间格式不满意，可以在对应的实体类上有关时间的属性，添加@JsonFormat加注解进行格式化
   1. @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
   2. 这个注解的依赖在：spring-boot-starter-web中



### 3、前端实现步骤

1. 添加对应的layui.js代码，用于生成对应的数据表格
2. 进行数据表格的调整



### 4、前端具体实现

1. 想显示数据表格，后端的访问方式腰围@RequestMapping（不能是@PostMapping），code = 0；
2. 参数设置
   1. 不写每个表格的具体宽度，写cellMinWidth: 95
   2. height: "full-125"
   3. limits: [10,15,20,25]
   4. id: "saleChanceListTable"
   5. {type: "checkbox", fixed: 'center'}
   6. , align: "center"



## 2、实现筛选功能

1. 在11、1、中就说过，获取数据表格的本质是一个多条件查询，所以后端的书写以及完成了多条件查询的功能
2. 现在通过前端实现就可以了



### 1、前端具体实现

1. 绑定搜索按钮，绑定事件为：需要通过layui的表格重载来实现筛选功能



## 3、给数据表格添加==头部工具条==

- 目的是：添加添加功能、批量删除功能



### 1、后端实现==添加==步骤

1. 添加功能

   1. dao、mapper.xml

      1. 查找、设计对应的sql语句，使用逆向自动生成的insertSelective方法

   2. service

      1. 进行传入参数的非空判断，某些值是不能为空的
      2. 设置数据的创建、更新时间
      3. 使用dao中的insertSelective方法把参数对象添加到数据库
      4. 判断是否添加成功，设置对应的异常信息
      5. service层方法返回值为void

   3. controller

      1. 接受前端传来的参数

      2. 从cookie中用当前登录用户的trueName，把这个值赋给参数对象的创建人属性

      3. 调用service方法进行添加

      4. 虽然没有要返回的数据，但因为前端可能会用到返回数据的code与msg信息进行判断，所以需要封装ResultInfo对象

      5. 因为设置过全局异常处理

         1. 所以controller不再需要进行异常处理
         2. 全局异常处理会自动把异常code与msg赋值给出错时创建的resultInfo对象

      6. ==如果没出错，那返回的是自己创建的ResultInfo，出现异常返回的是全局异常处理的ResultInfo！！==

         如果出错那么返回数据就是null，只有不出错的时候，才会去添加数据，返回controller中创建的的ResultInfo对象

   4. postman测试



### 2、前端实现==添加==步骤

1. 写好头部工具条的模板
2. 把头部工具条绑定到数据表格渲染中
3. 出现按钮后，绑定头部工具栏触发事件
   1. 弹出新增数据的页面：新增对应页面的controller路径，然后在弹出的iframe层中添加这个路径
   2. 设置弹出页面（type: 2）的：title、area、url
   3. 弹出页面是一个form，通过提交按钮去访问后端控制，所以在弹出页面的相关js中书写ajax代码
      1. 在ajax代码之前先设置一个弹出框作为加载层，不设定取消时间，icon:16
      2. 写ajax代码，判断code=200后
         1. 弹出数据添加成功
         2. 关闭加载层
         3. 关闭所有iframe层
         4. 父页面刷新（注意不是表格重载，是整个父页面刷新！刷新后表格自然就重载了）



### 3、后端实现==删除==步骤

1. dao、mapper.xml
   1. 查询、设计对应的sql语句：需要批量删除的方法，数据库逆向生成的方法中没有这种方法
   2. 自己写一个，需要传入一个id数组来进行批量删除
2. service
   1. 接收id数组，进行id数组非空判断
   2. 调用dao中的方法删除即可
   3. 判断删除是否成功，设置异常信息
3. controller
   1. 接收id数组，调用service方法
   2. 没有要返回的数据，返回success()（new 一个ResultInfo对象返回）
4. postman测试



### 4、前端实现==删除==步骤

1. 头部工具栏删除事件
2. 需要进行ajax访问，需要传递的参数是id的数组，这里的数组可以用访问参数传代替：如?ids=135&ids=138
   1. ==可知==：var checkStatus = table.checkStatus(obj.config.id); 中有被选中的id的数据集合（这个集合本质是一个array容器）
   2. 所以，checkStatus .data.length就是这个array容器元素的个数，如果小于1，表明没有数据被选中，需要弹出对应的消息提示框
   3. var saleChance = data.data[i];就是这个array容器中的第i个元素，这些元素都是saleChance 对象
   4. 所以saleChance.id就是saleChance对象的id
   5. 通过变量array容器中的saleChance对象，把saleChance对象的id拼接成需要的字符串个数：?ids=135&ids=138...
3. 弹出一个询问框：是否删除被选中的营销数据？
   1. 选择是的话，进行ajax访问
      1. 返回数据中code==200的话，证明删除成功，显示对应的提示框，并刷新表格
      2. 否则显示对应的失败提示框



## 4、给数据表格添加==单元格工具栏==



### 1、后端实现==编辑==步骤

1. dao、mapper.xml
   1. 这个功能可以通过逆向生成的updateByPrimaryKeySelective方法实现
2. service
   1. 需要返回给controller数据格式：int
   2. 接受参数对象，进行非空判断
   3. 建立xin的更新时间赋给参数对象
   4. 条用dao中的方法进行更新
   5. 判断是否更新成功，编辑异常信息
3. controller
   1. 没有什么需要返回的数据，所以直接返回success()即可
   2. 调用service中的方法进行更新即可
4. postman测试
   1. 需要解决的问题：在没有任何编辑的时候也可以进行提交更新



### 2、前端实现==编辑==步骤

1. 先把单元格工具栏渲染出来

2. 添加单元格工具栏触发事件，弹出一个iframe层页面，和头部工具条中添加按钮弹出的是同一个iframe页面！

3. 让弹出的iframe层中自动填充已有的数据内容
   1. 在弹出页面的请求路径中添加：?id=data.id
   
   2. 然后再页面返回控制中，通过id查询对应的对象数据，然后放到作用域中
   
   3. 再页面中进行域对象属性的取值，就可以达到自动填充内容的目的！
   
      ```js
      layer.open({
          type: 2,
          title: '<h3>营销机会管理--编辑</h3>',
          area: ['444px', '620px'],
          content: ctx+'/sale_chance/addOrUpdate?id='+data.id
      });
      ```
   
      
   
4. 在弹出页面的js中添加对应的修改事件

   1. 因为两个事件绑定了同一个页面，所以需要进行判断来决定事件的url是哪个
   2. 添加事件中页面是取不到数据id值的，因为域对象中没有相关数据存在
   3. 编辑事件中页面是可以取到被编辑对象id值的
   4. 所以设置一个隐藏标签，用于对被编辑对象的id取值
   5. 然后对这个隐藏标签的value进行判断，如果id值不为`null或''`，就是编辑事件；否则就是添加事件
   6. 成功以后的提示信息也需要做判断，resp.code == 200 && (sid == null || sid == '')是添加事件，否则为编辑事件或执行失败信息

5. 在编辑url的编写中

   1. 因为后端通过对象id判断修改对象是否存在，所以需要对象的id值
   2. 但是因为表单field中没有id值，所以需要在url后面拼接对应数据的id



### 3、后端实现==删除==步骤

1. dao、mapper.xml
   1. selectByPrimaryKey
   2. updateByPrimaryKeySelective
2. service
   1. 判断被删除对象是否为空，弹出对应异常信息
   2. 通过id查得对应的对象数据
   3. 调用dao中方法进行删除，把其中的isValid值变为0
   4. 判断是否删除成功
   5. 返回给controller层的数据类型为int
3. controller
   1. 直接调用service方法进行删除即可
   2. 返回success()（ResultInfo）
4. postman测试



### 4、前端实现删除步骤

1. 通过单元格工具栏触发事件，先弹出询问狂询问是否要删除对应的数据
2. 然后向后端发送ajax请求，传递参数data为对象id
3. 如果code=200，弹出对应的消息提示框，==但是不用重载表格==
   1. 因为，单元格工具栏的删除方法中自带了：obj.del(); // 删除对应行（tr）的 DOM 结构，并更新缓存



## 5、完善表格格式

- 大体上的CRUD工作以及完成，但是一些细节需要完善



### 1、表格中指派人由数字换位对应的trueName

1. 后端实现

   1. 通过saleChance对象的id去User表查询对应的trueName，然后进行assignMan的重新赋值

   2. 这里需要注意的是：==String的非空判断有三种，isEmpty()一定要放到最后！！==

      ```java
          private void replaceAssignMan(List<SaleChance> saleChancesList) {
              for (SaleChance saleChance : saleChancesList) {
                  String assignMan = saleChance.getAssignMan();
                  if (assignMan != null && assignMan != "" && !assignMan.isEmpty()) {
                      System.out.println("assignMan = " + saleChance.getAssignMan());
                      int i = Integer.parseInt(assignMan);
                      User user = userMapper.selectByPrimaryKey(i);
                      saleChance.setAssignMan(user.getTrueName());
                  }
              }
          }
      ```

      



### 2、格式分配化状态

- 前端实现

  1. 在数据表格中对应的位置使用templet绑定一个方法

     ```js
     ,{field: 'devResult', title: '开发状态', sort: true, align: "center", templet: function (d) {
         return formatDevResult(d.state);
     }}
     ```

  2. 编写方法内容

     ```js
     function formatDevResult(state) {
     
         if (state == 0) {
             return "<div style='color: yellow'>未分配</div>"
         } else if (state == 1) {
             return "<div style='color: green'>已分配</div>"
         } else {
             return "<div style='color: red'>未知</div>"
         }
         
     }
     ```

     1

### 3、格式化开发状态

- 前端实现
  1. 在数据表格中对应的位置使用templet绑定一个方法
  2. 编写方法内容





### 4、通过==取消按钮==来关闭弹出层

1. 之前弹出层只能通过x来关闭，现在设置其通过取消按钮关闭

2. 固定写法

   ```js
   $("#closeBtn").click(function () {
       // 先得到当前iframe层的索引
       var index = parent.layer.getFrameIndex(window.name);
       // 执行关闭
       parent.layer.close(index);
   });
   ```

   

### 5、添加事物注解

- service层相关方法添加：`@Transactional(propagation = Propagation.REQUIRED)`



### 6、弹出页面的下拉框中显示指派人

- 后端实现

  1. 在访问弹出添加/编辑页面的控制中，把所有的User对象中的销售人员对象信息，放到域对象中

  2. dao、mapper.xml

     1. 这个方法即使时UserMapper中也是没有的，所以需要重新编写一个
     2. 本来返回User对象的集合就可以，但是为了不要多余的返回数据，只返回User的id与name值
     3. 把数据封装到Map对象中，每一个对象的id、name存放一个map对象
     4. 然后把这些map对象放到一个list集合返回给前端

  3. service

     1. 直接调用dao的方法查询即可

  4. controller

     1. 直接调用service的方法查询即可

  5. postman测试

     

- 前端实现

  1. 在弹出页面的js中，下拉框所在的页面直接发送ajax请求

  2. 把返回来的数据进行拼接；先获取对应的下拉框对象，然后每次循环都要append，循环完毕后从新渲染下拉框对象

     ```js
     var sels = $("#assignMan");
     $.ajax({
         type: 'POST',
         url: ctx + '/sale_chance/queryAllSaleUser',
         // data: data,
         success: function (resp) {
             // console.log(resp);
             var opt = "";
             if (resp != null && resp.length > 0) {
                 for (let i = 0; i < resp.length; i++) {
                     opt = "<option value='"+resp[i].id+"'>"+resp[i].true_name+"</option>"
                     sels.append(opt);
                 }
             }
     
             layui.form.render("select");
         }
     });
     ```

     

### 7、指派时间、分配状态、开发状态的完善

- 后端实现
  1. 在添加数据功能中增加：判断判断是否有指派人，并设置不同情况下分配状态、开发状态、分配时间的默认值设置
  2. 在编辑（更新）功能中判断：
     1. 如果同一个id对象编辑前后的指派人不一样，那就从新设定分配时间！
     2. 如果对象的分配人没有选择那么：setAssignTime(null)、setState(0)、setDevResult(0)
        1. 为了把时间设为null，需要改动updateByPrimaryKeySelective查询
        2. 去掉`assign_time != null`的if判断，直接赋值：`assign_time = #{assignTime,jdbcType=TIMESTAMP},`
        3. 虽然不选任何分配人，分配人也不是null，所以分配人为空不用进行这样的sql改变！！
- 支持，功能11全部开发完成！





# 12、客户开发计划CRUD



## 1、获取Layui数据表格

- 多条件查询功能（一般返回数据表格都是多条件查询功能）
- 这里与之前不同的是，获取的数据表格中，只有分配给登录用户自己的数据
- 并且这里因为返回数据所需要的sql和营销机会管理的多条件查询时一样的，所以要和营销机会管理共用多条件查询接口



### 1、后端实现步骤

1. 先接通页面，controller需要extends BaseMapper否则报错
2. dao、mapper.xml
   1. 不需要dao，与营销机会管理共用多条件查询sql，但是需要做一些修改
   2. 共用的两者多条件的内容不一样，所以需要添加开发状态这个条件到sql查询挑架你判断
   3. 在对应的查询对象中添加开发状态属性（多条件查询需要）、assign_man属性（只返回登录者的数据）
   4. sql查询语句条件增加开发状态判断、assign_man判断
3. serice：不需要
4. controller
   1. 可知，assgn_man的值是User对象的id，id可以通过cookie获取
      1. 第一种选择
         1. 所以在共用接口的条件下，需要做的是判断是否需要获取id值从而区分不同的功能情况
         2. 给客户开发计划的数据表格访问路径增加一个参数flag=1，在controller中判断
            1. 如果flag=1，表明是客户开发计划功能访问，把id值赋给查询对象
            2. 否则就是数据营销管理访问
      2. 第二种选择
         1. 不共用接口，直接在控制中通过cookie获取id即可
         2. 不需要flag参数，不需要判断
   2. 调用营销机会管理的service实现类，用其中的多条件查询返回数据即可
5. postman测试



### 2、前端实现步骤

1. js中写返回数据表格的代码
2. 给多条件搜索框绑定事件，实现多条件筛选功能
3. 把单元格工具栏绑定好，头部工具栏的模板内容跟是空的！



## 2、单元格工具栏内容的实现



### 1、前端实现步骤

1. 先接通页面，这个页面需要通过单元格事件来弹出，所以是前端的部分
2. 弹出页面--编辑内容的自动填充（==前后端实现==）
   1. 之前做过类似的，先拿到id
   2. 然后再返回页面的时候，把id对应的对象放到作用域中
   3. 前端页面通过作用域中的对象取取值
3. 添加页面中需要的数据表格，内容为SaleChance对象id对应的CusDevPlan对象内容
   1. js数据表格模板
   2. 在请求时加上对应的id
      1. 在前面通过作用域中获取值时，设置一个隐藏框，用来获取SaleChance.id
      2. 这样就可以在弹出页面中获取SaleChance.id来添加到数据表格的访问路径中，返回对应的数据！
4. 可知弹出页面的数据表格中也有==头部工具栏==和==单元格工具栏==
   1. js的渲染中绑定对应的工具栏，使其显示出来
   2. 头部工具栏--==添加事件==
      1. 先联通页面（前后端）
         1. 前端在头部工具栏事件中弹出对应的iframe层，写好页面请求地址
         2. 后端写页面的访问控制
      2. 添加计划项事件的实现
         1. 在访问添加页面的时候传递sid参数，然后存入request作用域
         2. 在添加页面设置隐藏框，放入sid参数值
         3. 在添加页面提交的时候，路径添加sid值！
   3. 头部工具栏--==开发成功/失败事件==
      1. 点击按钮，弹出询问对话框
      2. 点击确定后把对应数据SaleChance的开发状态改为：成功或失败
      3. 根据前端单元格个工具栏模板的判断，如果开发状态不是未开发或开发中，会显示详情按钮（否则是开发按钮）
      4. 详情按钮弹出的对话框内容和开发按钮一样，但是没有单元格工具栏，无法操作，这意味着这条数据不能再进行更改！
      5. 设置一个flag参数，flag=1代表开发成功，flag=2代表开发失败；不同的状态给更新对象设置不同的开发状态值
      6. 传递sid参数，用于通过主键来更新saleChance对象
      7. 通过返回的code值，弹出对应的提示信息，--关闭弹出框--刷新页面！
   4. 单元格工具栏--==编辑事件==
      1. 和添加事件共用同一个模板，打开弹出层
         1. 单元格工具栏可以直接获取行中的CusDevPlan对象的id（cid）
         2. 通过cid是否为null判断访问的是哪个事件的接口，通过判断设置弹出窗口中不同的title、url值
         3. 编辑事件除了之前sid参数，还要把cid参数一同传到后端
      2. 点击确定按钮，进行数据更改
         1. 因为是共用页面，所以需要判断进行的是什么操作
         2. 设置隐藏域，放置cid值
         3. js中进行判断，如果cid不为null证明是编辑事件，如果cid=null证明是添加事件；设置不同的url，提示信息
   5. 单元格工具栏--==删除事件==
      1. 单元格工具栏中的删除事件自带询问对话框
      2. 发送ajax请求传递cid参数，然后根据code值显示对应的提示信息







### 2、后端实现步骤

1. 返回弹出页面中的数据表格！

   1. dao、mapper.xml
      1. 可知是通过SaleChance的id去做查询
      2. 查询的时候不能但用id做参数，把id放到查询对象中，因为后面需要分页（==多个对象就需要分页，需要查询对象==）
      3. 所以需要写一个新的方法
   2. service
      1. 判断id不为空
      2. 通过id调用dao中的方法进行查询
      3. 分页，封装需要的数据到map对象
   3. controller
      1. 调用service中的方法返回map

2. 添加计划事件的实现

   1. dao、mapper.xml
      1. 使用insertSelective
   2. service
      1. 判断参数非空
      2. 设置默认的date值
      3. 直接调用dao的方法
   3. controller
      1. 接受从前端传递的sid值，并判断其是否为空！不为空的话把sid赋予cusDevPlan对象
      2. 调用service的方法
      3. 返回ResultInfo对象

3. 开发成功/失败事件

   1. dao、mapper.xml
      1. 使用updateByPrimaryKeySelective
   2. service
      1. 对象是非为空判断
      2. 调用dao方法进行更新
      3. 判断是否更新成功
   3. controller
      1. 接受参数，通过flag参数判断是开发成功/失败
      2. 调用service方法进行更新
      3. 返回ResultInfo对象

4. 打开编辑窗口事件，并自动填充其中的内容

   1. 接受前端传来的cid的值，进行判断
   2. 如果cid不为null，证明是编辑事件，通过cid查到CusDevPlan对象，放到request作用域中；前端通过作用域取值进行填充
   3. 如果cid为null，证明是添加事件

5. CusDevPlan对象编辑功能

   1. dao、mapper.xml

      1. 使用updateByPrimaryKeySelective

   2. service

      1. 判断参数是否为null
      2. 调用dao方法进行更新
      3. 判断是否更新成功

   3. controller

      1. 调用service方法

      2. 返回ResultInfo对象

         

6. CusDevPlan对象删除功能

   1. dao、mapper.xml
      1. 使用updateByPrimaryKeySelective
   2. service
      1. 判断参数是否为null
      2. 设置isValid值、更新时间
      3. 调用dao方法进行更新
      4. 判断是否更新成功
   3. controller
      1. 调用service方法
      2. 返回ResultInfo对象
      3. 至此客户开发计划功能全部完成！



# 13、系统设置

![image-20240327153419272](TTP/image-20240327153419272.png)



## 1、用户管理



### 1、后端实现步骤

1. 写对应页面的控制

2. 返回数据表格数据，实现多条件查询

   1. 新建查询对象

   2. dao、mapper.xml

      1. queryUserList

   3. controller

      1. 参数为UserQuery，调用service方法
      2. 返回值类型为：Map<String, Object>

   4. service

      1. 参数校验
      2. 开启分页
      3. 参数为UserQuery，调用dao方法
      4. 分页
      5. 把数据表格需要的数据封装到map对象中
      6. 返回值类型为：Map<String, Object>

   5. postman测试

      

3. 添加用户

   1. 写对应弹出页面的控制
   2. dao、mapper.xml
      1. insertSelective
   3. controller
      1. 参数：User对象
      2. 返回数据类型ResultInfo
      3. 调用service方法
   4. service
      1. 参数校验，在数据库中对象名是否唯一！
      2. 设置创建时间、更新时间，密码设为默认值123456（用户登录后自己修改密码）
      3. 调用dao方法
      4. 判断是否添加成功
   5. postman测试

   

4. 编辑用户数据

   1. 对应页面控制与添加用户共用，同一个页面

   2. dao、mapper.xml

      1. updateByPrimaryKeySelective

   3. controller

      1. 参数：User对象
      2. 判读user.id是否存在，根据user的id查询对应的user对象，存到作用域中
      3. 返回数据类型ResultInfo
      4. 调用service方法

   4. service

      1. 参数校验，在数据库中对象名是否唯一！
      2. 设置更新时间
      3. 调用dao方法
      4. 判断你是否添加成功

   5. postman测试

      

5. 批量删除用户数据

   1. dao、mapper.xml
      1. 自己写方法：deleteBatch
      2. 参数Integer数组
   2. contoller
      1. 返回ResultInfo类型数据
      2. 调用service方法
   3. service
      1. 参数校验
      2. 调用dao方法
      3. 判断是否删除成功
   4. postman测试
   
   
   
6. 单元格删除数据

   1. dao、mapper.xml
      1. updateByPrimaryKeySelective，不需要新写
      
      2. controller
         1. 需要的参数是User对象，前端需要传入对应的user对象id
         2. 调用service方法
         3. 返回ResultInfo类型的数据
         
      3. service
         1. 添加事物注解
         2. user对象id校验
         3. 调用controller方法
         4. 判断是否删除成功
         5. 返回int类型的数据
         
      4. postman测试
      
         
   
7. 在添加用户时添加角色多选下拉框

   1. 这意味着，在添加用户时，需要对`用户角色关系中间表`作出操作
   2. 首先要把所有的角色信息在下拉框中进行显示，这个下拉框可以进行多选！（这是和之前不同的地方）
   3. 根据组件的需要查询需要的数据后，以对应的格式返回到前端
   4. 新建一个数据模型：RoleModel（需要role_id、role_name），或者直接返回Map类型额List集合（因为只有两个属性）
   5. dao、mappere.xml
      1. 新写selectRoleIdAndName，查询所有的用户id与name
   6. controller
      1. 直接调用dao方法返回对应类型的数据即可！
   
8. 添加用户对象时增加下拉框参数

   1. 当复选框的值被勾选时，在`用户-角色中间表中`添加对应用户id的数据；否则删除对应用户id的数据
   2. ==因为添加了下拉框的标签在html中，所以下拉框有值后，前端向后端传递的参数中自然会有角色id的参数==
   3. ==之前批量删除要先凭借好id集合再传参的原因是，html中没有对应的标签，所以只能那么做！！==
   4. 但是在添加用户的事件中，是没有用户id的，所以要先添加用户数据再获取用户id；重要的是角色id的获取
      1. ==正常sql添加完user表后，直接使用user.getId()就可以拿到id值了！！！测试可行==
      2. ==不需要为了返回user的id，在添加的sql中额外添加字段什么的==
   5. 后端拿到角色id的集合后，（先查询中间表是否有对应rold_id的数据）先删除中间表中旧的数据，然后添加新的数据
   6. dao、mapper.xml
      1. 新写方法：selectUserRole，deleteBatchUserRole，insertUserRole
   7. controller
      1. 再原本的controller参数上，添加Integer[] 参数
      2. 其他的不变
   8. service
      1. 添加Integer[] 参数，然后执行查询、删除、添加的过程

9. 编辑用户对象时回显下拉框参数

   1. 在返回下拉框数据的方法中，判断是否有user_id
   2. 如果有的话
      1. 先查询对应user_id所有用户-角色中间表数据中的role_id的集合list
      2. 给返回对象模型添加属性selected，默认值为空字符串" "
      3. 循环然后的对象模型，然后判断对象模型的id是否包含在list中，是的话，selected属性设置为"selected"

10. 编辑用户通过角色更改信息

    1. 更改页面提交访问会发送参数：roleIds，在controller以Integer数组的形式接受参数
    2. 在user的修改方法中，通过user_id先删除对应的中间表数据，然后通过roleIds添加新的中间表数据
    3. 要用到的方法和添加角色时相同！

11. 删除功能

    1. 在删除功能方法中删掉用户id对应的中间表数据即可
    2. 批量删除方法中遍历用户id数组，然后和1相同



### 2、前端实现步骤

1. 显示数据表格
   1. 生成数据表格的js代码
   2. 绑定搜索按钮，实现多条件筛选功能
2. 添加用户
   1. js中头部工具栏事件，弹出对应的页面
   2. 弹出页面的js中，发送ajax请求（layui表单提交事件）
3. 编辑用户数据
   1. 单元格工具栏事件中，弹出对应页面
   2. 发送ajax
4. 批量删除用户数据
   1. 检查是否有数据被勾选
   2. 拼接选中数据的id字符串
   3. 头部工具栏触发事件，弹出询问对话框
   4. 发送ajax请求
   5. 对返回的数据进行处理，弹出对应的提示框信息
5. 单元格删除数据
   1. 单元格工具栏触发事件，弹出询问对话框
   2. 发送ajax请求，显示对应的提示信息
6. 在添加用户时添加角色多选下拉框
   1. 先添加需要的下拉框html代码
   2. 再添加对应组件需要的js代码
7. 添加用户对象时增加下拉框参数
   1. ==因为添加了下拉框的标签在html中，所以下拉框有值后，前端向后端传递的参数中自然会有角色id的参数==
   2. 所以不需要额外的操作！只要下拉框中能显示、选择数据，就能通过表单自然传递到后端！
8. 编辑用户对象时回显下拉框参数
   1. 在下拉框组件访问后端的路径中添加role_id参数即可
9. 编辑用户通过角色更改信息
   1. 在修改页面提交时，会传递roleIds参数
10. 删除功能
    1. 没有什么特别要做的





## 2、角色管理（RBAC）



### 1、后端实现

1. 链通角色管理页面，在控制中写对应的控制路径

2. 返回数据表格数据，多条件查询只有一个条件

   1. 新建查询对象
   2. dao、mapper.xml
      1. 新写：queryRoleBatch
      2. 参数为查询对象
      3. 返回数据类型为Role对象的list集合
   3. controller
      1. 参数为查询对象
      2. 调用service方法
      3. 返回数据类型为map类型数据
   4. service
      1. 参数校验
      2. 开启分页
      3. 查询数据
      4. 新建分页对象进行分页
      5. 新建map对象封装返回数据
   5. postm测试

   

3. 添加角色数据

   1. 链通页面，写对于的路径控制
   2. dao、mapper.xml
      1. insertSelective
      2. 参数role对象
   3. controller
      1. 参数role对象
      2. 调用service
      3. 返回ResultInfo对象
   4. service
      1. 参数校验
      2. 添加查询对应角色行数的方法，判断角色名是否以及存在（i>0，报错）
      3. 设置默认值：创建日期、更新日期、isValid值
      4. 调用dao方法
      5. 判断是否添加成功
   5. postman测试

   

4. 编辑事件

   1. dao、mapper.xml

      1. updateByPrimaryKeySelective

   2. controller

      1. 参数：role对象
      2. 弹出页面时，判断id，把对应id的role对象回显到前端页面
      3. 调用service方法
      4. 返回ResultInfo对象

   3. service

      1. 参数校验
      2. 调用dao方法
      3. 判断是否更新成功
      4. 返回int

      

5. 删除功能

   1. dao、mapper.xml
      1. updateByPrimaryKeySelective
   2. controller
      1. 参数：role对象
      2. 调用service方法
      3. 返回ResultInfo对象
   3. service
      1. 参数校验
      2. 设置更新时间
      3. 调用dao方法
      4. 判断是否删除成功
   4. postman测试

   

6. 添加授权

   1. 先接通页面，写对应页面的路径控制
   
   2. 勾选复选框，实现添加权限的功能
      
      1. 向用户数据添加roleid、moduleid、授权码
      
   3. dao、mapper.xml
      
      1. insertSelective
      
   4. controller
      1. 参数：Permission对象
      2. 调用service方法
      3. 返回数据类型ResultInfo
      
   5. service
      1. 参数校验
      2. 在添加权限数据之前，先删除对应rid的旧权限数据，如果删除的数量与查询rid得到的旧数据量不等，删除失败！
      3. 删除旧权限数据后判断，是否有mid是否为null，如果为null证明没有勾选任何权限，直接return终止程序继续执行添加操作
      4. 通过判断后，开始执行添加操作，设置默认值日期等，给Permission对象添加AclValue值
      5. 调用dao方法
      6. 判断是否添加成功
      
   6. postman测试
   
      
   
7. 授权回显

   1. 给所有TreeModule对象设置checked属性，默认值为false！

   2. 在返回树形图数据的控制中，获取前端传来的role_id，通过role_id获取权限表中的moudle_id的List集合

   3. 遍历TreeMoude对象集合，判断如果其id包含于list集合中，那么id对应TreeMoude的checked属性设置为true

      ```java
      List<Integer> list = moduleMapper.selectPser(rid);
      List<TreeModel> modules = moduleMapper.selectModule();
      
      for (TreeModel model : modules) {
          if (list.contains(model.getId())) {
              model.setChecked(true);
          }
      }
      ```

      

8. 实现用户权限校验（分为前端实现部分和后端实现部分）

   1. 先在main的控制路径中，查询对应用户id的aclValue集合（注意这个权限码是String类型的！），然后放到==session作用域==中
   
   2. 写一个权限注解，在对应的资源访问控制上标注`权限注解以及对应的权限码`
   
      ```java
      package com.xxxx.crm.annoation;
      
      import java.lang.annotation.*;
      
      @Target({ElementType.METHOD})
      @Retention(RetentionPolicy.RUNTIME)
      @Documented
      public @interface RequirePermission {
          // 权限码
          String code() default "";
      }
      
      ```
   
      
   
   3. 写一个权限异常：AuthException
   
      ```java
      package com.xxxx.crm.exceptions;
      
      public class AuthException extends RuntimeException {
          private Integer code = 300;
          private String msg = "暂无权限！";
      
          public AuthException() {
              super("暂无权限！");
          }
      
          public AuthException(Integer code) {
              super("暂无权限！");
              this.code = code;
          }
      
          public AuthException(String msg) {
              super(msg);
              this.msg = msg;
          }
      
          public AuthException(Integer code, String msg) {
              super(msg);
              this.code = code;
              this.msg = msg;
          }
      
          public Integer getCode() {
              return code;
          }
      
          public void setCode(Integer code) {
              this.code = code;
          }
      
          public String getMsg() {
              return msg;
          }
      
          public void setMsg(String msg) {
              this.msg = msg;
          }
      }
      
      ```
   
      
   
   4. 写一个切面功能：PermissionProxy
   
      ```java
      package com.xxxx.crm.aspect;
      
      import com.xxxx.crm.annoation.RequirePermission;
      import com.xxxx.crm.exceptions.AuthException;
      import org.aspectj.lang.ProceedingJoinPoint;
      import org.aspectj.lang.annotation.Around;
      import org.aspectj.lang.annotation.Aspect;
      import org.aspectj.lang.reflect.MethodSignature;
      import org.springframework.beans.factory.annotation.Autowired;
      import org.springframework.stereotype.Component;
      
      import javax.servlet.http.HttpSession;
      import java.util.List;
      
      @Component
      @Aspect
      public class PermissionProxy {
      
          @Autowired
          private HttpSession session;
      
          // 这里的意思应该是：作用的范围是一个注解，程序运行时有这个注解aop才会生效
          @Around(value = "@annotation(com.xxxx.crm.annoation.RequirePermission)")
          public Object around(ProceedingJoinPoint pjp) throws Throwable {    // 这里是aop中@Around方法的固定写法
      
              // 这里也是固定写法
              Object object = null;
      
              // 获取权限码集合
              List<String> permissions = (List<String>) session.getAttribute("permissions");
              // 这个判断表明没有任何的权限码
              if (permissions == null || permissions.size() < 1) {
                  throw new AuthException();
              }
      
              // 如果通过if判断，证明有权限码集合；获取当前操作的方法
              MethodSignature mSignature = (MethodSignature) pjp.getSignature();
              // 得到方法的权限校验注解
              RequirePermission requirePermission = mSignature.getMethod().getDeclaredAnnotation(RequirePermission.class);
              // 如果拥有的权限码中，没有注解指定的权限码值，抛出异常
              if (!permissions.contains(requirePermission.code())) {
                  throw new AuthException();
              }
      
              // 这里也是固定写法
              object = pjp.proceed();
      
              return object;
          }
      
      }
      
      ```
   
      
   
   5. 在全局异常中配置权限异常的处理情况
   
      ```java
      // 判断是否是自定义异常信息
      if (e instanceof ParamsException) {
          ParamsException p = (ParamsException) e;
          // mv.setViewName("error");
          mv.addObject("code", p.getCode());
          mv.addObject("msg", p.getMsg());
      } else if (e instanceof AuthException) {
          AuthException e1 = (AuthException) e;
          mv.addObject("code", e1.getCode());
          mv.addObject("msg", e1.getMsg());
      }
      
      // ----------------------------------------------------
      
      // 判断是否是自定义异常
      if (e instanceof ParamsException) {
          ParamsException p = (ParamsException) e;
          resultInfo.setCode(p.getCode());
          resultInfo.setMsg(p.getMsg());
      } else if (e instanceof AuthException) {
          AuthException e1 = (AuthException) e;
          resultInfo.setCode((e1.getCode()));
          resultInfo.setMsg(e1.getMsg());
      }
      ```
   
      
   
9. 测试，[localhost:8080/crm/sale_chance/list](http://localhost:8080/crm/sale_chance/list)

   ```java
   /**
    * 多条件查询，返回数据给前端的layui数据表格
    * @param saleChanceQuery
    * @return
    */
   @RequestMapping("/list")
   @ResponseBody
   @RequirePermission(code = "101001")
   public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {
   
       return saleChanceService.querySaleChanceByParams(saleChanceQuery);
   
   }
   ```

   

10. 至此RBAC功能完成！







### 2、前端实现

1. 显示数据表格

2. 绑定多条件搜索按钮相关事件

3. 添加角色数据
   1. 链通页面，头部工具栏事件
   2. 头部工具栏ajax提交，根据返回值显示提示信息
   
4. 编辑事件
   1. 单元格工具栏事件，打开对应的页面
   2. 弹出页面发送ajax请求
   3. 可以通过关闭按钮关闭页面
   
5. 删除功能
   1. 单元格工具栏时间，弹出询问对话框
   2. 发送ajax请求
   
6. 添加授权
   1. 判断是由有角色被勾选，勾选的数据量是否大于1，根据情况弹出对应的提示信息==并return程序==
   
   2. 打开对应页面，显示树形图
   
   3. 添加ztree点击添加事件
   
   4. 选择ztree的相关方法，取得被勾选数据的信息；还有一个方法是选择所有复选框状态改变的node，不要用错方法
   
      ```js
      var treeObj = $.fn.zTree.getZTreeObj("test1");
      var nodes = treeObj.getCheckedNodes(true);
      ```
   
   5. 后端传递role_id与module_id的集合！
   
7. 实现用户权限校验

   1. 在把用户的权限码集合放到session作用域后，在main页面中增加内容显示判断

      1. 在整体资源外判断permissions是否存在：`<#if permissions?? >`
         1. 判断的值是一个boolean值，不能直接输出，如果要输出后面需要+?string（${(value??)?string）
      2. 然后再具体的资源上判断是否有对应的权限码：`<#if permissions?seq_contains("10")>`

      ```html
      <#if permissions?? >
          <ul class="layui-nav layui-nav-tree layui-left-nav-tree layui-this" id="currency"></ul>
      </#if>
      ```

      ```html
      <#if permissions?seq_contains("10")>
          <li class="layui-nav-item">
              <a href="javascript:;" class="layui-menu-tips"><i class="fa fa-street-view"></i><span class="layui-left-nav"> 营销管理</span> <span class="layui-nav-more"></span></a>
              <dl class="layui-nav-child">
                  <dd>
                      <a href="javascript:;" class="layui-menu-tips" data-type="tabAdd" data-tab-mpi="m-p-i-1" data-tab="sale_chance/index" target="_self"><i class="fa fa-tty"></i><span class="layui-left-nav"> 营销机会管理</span></a>
                  </dd>
                  <dd>
                      <a href="javascript:;" class="layui-menu-tips" data-type="tabAdd" data-tab-mpi="m-p-i-2" data-tab="cus_dev_plan/index" target="_self"><i class="fa fa-ellipsis-h"></i><span class="layui-left-nav"> 客户开发计划</span></a>
                  </dd>
              </dl>
          </li>
      </#if>
      ```

      





## 3、菜单管理



### 1、后端实现

1. 接通页面，写对应的路径控制
   1. 页面控制中要返回grade、parentId值到域对象用于页面内容显示的判断！
   2. 并且在添加页访问后端时，带上这两个数据对新数据进行赋值
2. 添加目录，头部工具栏事件/单元格工具栏事件
   1. dao、mapper.xml
      1. insertSelective
   2. controller
      1. 返回ResultInfo
      2. 参数：module, grade, parentId
      3. 调用service
   3. service
      1. 参数校验，是否重复等等，可能涉及到一些新的方法（但是都比较简单）
      2. 添加默认值：isvalid、日期、 grade、 parentId
      3. 校验是否添加成功
3. 编辑功能
   1. 写弹出页面的控制，单元格事件，数据回显
      1. 接受module_id，然后查module对象，放到作用域中
   2. dao、mapper.xml
      1. updateByPrimaryKeySelective
   3. controller
      1. 返回ResultInfo
      2. 参数：module对象
      3. 调用service
   4. service
      1. 必要字段不能为空、判断是否重复、setUpdateDate
      2. 调用dao
      3. 判断是否更新成功
      4. 返回int
4. 删除功能
   1. dao、mapper.xml
      1. updateByPrimaryKeySelective
   2. controller
      1. 参数：module对象
      2. 调用service
      3. 返回ResultInfo
   3. service
      1. 数据校验
      2. 添加module对象的更新日期
      3. 调用dao方法
      4. 判断是否删除成功
   4. postman测试







### 2、前端实现

1. 页面显示使用TreeTable组件，数据返回格式和数据表格一样，但是没有分页
2. 全部展开，全部折叠：头部工具栏事件，固定写法
3. 添加目录，头部工具栏事件/单元格工具栏事件
   1. 弹出一个页面，然后提交form-ajax请求
   2. 重点是：grade、parentId值的传递，这两个参数是很重的，决定了新加数据的位置！
      1. 头部工具栏事件中：grade=0，parentId=-1是固定的
      2. 单元格工具栏事件中：grade=选中项的grade+1，parentId=选中项的id
      3. 对grade值进行判断，如果==3，弹出错误提示信息并return，终止程序
   3. 通过隐藏域把这两个参数最终传递到后端进行新数据的赋值！
4. 编辑功能
   1. 单元格事件，数据回显
      1. 弹出对应的页面，传递module_id参数
   2. 弹出的页面的js中，发送ajax，从隐藏域获取并传送module_id参数
5. 删除功能
   1. 单元格工具栏事件，弹出询问框
   2. 发送ajax请求，传递moudule_id参数
   3. 根据结果弹出对应的提示消息
6. 至此系统设置所有功能实现完毕！





# END





































6. 

