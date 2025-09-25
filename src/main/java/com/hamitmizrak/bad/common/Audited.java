package com.hamitmizrak.bad.common;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE,ElementType.METHOD,ElementType.FIELD})
public @interface Audited {
    String value() default "no-meaning"; //Kötü anlamsız öntanım
}
