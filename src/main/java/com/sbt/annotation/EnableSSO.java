package com.sbt.annotation;

import com.sbt.config.SssoAutoConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(SssoAutoConfig.class)
public @interface EnableSSO {

}
