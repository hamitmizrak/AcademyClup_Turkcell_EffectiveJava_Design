package com.hamitmizrak.bad.common;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // Kötü: Hiç kimse okumuyor yine de RUNTIME
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface CommonAudited {
    String value() default "no-meaning"; // Kötü: anlamsız öntanım
}
