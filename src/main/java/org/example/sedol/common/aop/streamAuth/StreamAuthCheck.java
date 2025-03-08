package org.example.sedol.common.aop.streamAuth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.example.sedol.domain.VO.StreamServiceType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StreamAuthCheck {
	StreamServiceType[] serviceTypes();
}
