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
