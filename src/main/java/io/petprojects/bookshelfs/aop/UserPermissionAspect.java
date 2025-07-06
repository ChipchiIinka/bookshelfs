package io.petprojects.bookshelfs.aop;

import io.petprojects.bookshelfs.entity.ReaderEntity;
import io.petprojects.bookshelfs.exception.BookshelfsException;
import io.petprojects.bookshelfs.exception.ErrorType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Aspect
@Component
public class UserPermissionAspect {

    @Around("@annotation(checkUserPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, CheckUserPermission checkUserPermission) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ReaderEntity currentUser = (ReaderEntity) authentication.getPrincipal();
        Long targetId = extractTargetId(joinPoint, checkUserPermission.idParam());

//        if (!currentUser.isAdmin() && !currentUser.getId().equals(targetId)) {
//            throw new BookshelfsException(ErrorType.FORBIDDEN, "Недостаточно прав для изменения этого профиля!");
//        }
        if (!currentUser.getId().equals(targetId)) {
            throw new BookshelfsException(ErrorType.FORBIDDEN, "Недостаточно прав для изменения этого профиля!");
        }
        return joinPoint.proceed();
    }

    private Long extractTargetId(ProceedingJoinPoint joinPoint, String paramName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                return (Long) args[i];
            }
        }
        throw new IllegalArgumentException("Параметр с ID не найден!");
    }
}
