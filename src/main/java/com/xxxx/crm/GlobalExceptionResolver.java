package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
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
     * 访问只有通过后端并出错的时候，全局异常处理才会中的内容才会参与显示，否则只会显示error页面，但是没有提示信息
     * 比如：如果一个页面没有后端控制路径，那么在访问这个页面的时候，回出现error页面，但是没有提示信息
     * 如果错误出现在访问页面的控制中，但是不属于自定义异常，那么提示信息会是：默认的异常处理！（好处是没有杂乱的异常信息，坏处是不知道究竟是什么原因导致的）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param handler 拦截的方法
     * @param e 异常对象
     * @return
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) {

        /**
         * 判断是否是非法登录异常
         * 这里单独添加的原因是：其他异常要么去错误页面error，要么return null；现在要去index页面
         */
        if (e instanceof NoLoginException) {
            ModelAndView mv1 = new ModelAndView("redirect:/index");
            return mv1;
        }

        ModelAndView mv = new ModelAndView();
        mv.addObject("code", 300);
        mv.addObject("msg", "默认的异常处理");
        mv.setViewName("error");

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
             *
             * ParamsException通过AssertUtil判断抛出
             *      如果返回的是视图，把相关code、msg赋给视图，返回视图
             *      如果返回的是JSON格式数据，把相关code、msg赋给ResultInfo，输出ResultInfo，返回null
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
                    // mv.setViewName("error");
                    mv.addObject("code", p.getCode());
                    mv.addObject("msg", p.getMsg());
                } else if (e instanceof AuthException) {
                    AuthException e1 = (AuthException) e;
                    mv.addObject("code", e1.getCode());
                    mv.addObject("msg", e1.getMsg());
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
                } else if (e instanceof AuthException) {
                    AuthException e1 = (AuthException) e;
                    resultInfo.setCode((e1.getCode()));
                    resultInfo.setMsg(e1.getMsg());
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
