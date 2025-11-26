package io.github.alirostom1.smartshop.aspect;

import io.github.alirostom1.smartshop.annotation.AuthN;
import io.github.alirostom1.smartshop.annotation.AuthZ;
import io.github.alirostom1.smartshop.exception.AccessDeniedException;
import io.github.alirostom1.smartshop.exception.AuthenticationException;
import io.github.alirostom1.smartshop.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;



@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect{
    private final HttpServletRequest request;
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Before("@annotation(authN)")
    public void checkAuthN(AuthN authN){
        getAuthenticatedUser();
    }

    @Before("@annotation(authZ)")
    public void checkAuthZ(JoinPoint joinPoint,AuthZ authZ){
        User user = getAuthenticatedUser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user",user);

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for(int i = 0; i < paramNames.length;i++){
            context.setVariable(paramNames[i],args[i]);
        }
        Expression expression = expressionParser.parseExpression(authZ.value());
        Boolean result = expression.getValue(context,Boolean.class);
        if(result == null || !result){
            throw new AccessDeniedException("Unauthorized to access this endpoint!");
        }
    }


    private User getAuthenticatedUser(){
        HttpSession session = request.getSession(false);
        if(session == null){
            throw new AuthenticationException("Not authenticated!");
        }
        User user = (User) session.getAttribute("user");
        if(user == null){
            throw new AuthenticationException("Not authenticated!");
        }
        return user;
    }

}
