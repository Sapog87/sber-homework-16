package org.sber.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    /**
     * Определяет какие параметры метода игнорировать при составлении ключа для кэша.
     * <p>
     * По дефолту все значения учитываются.
     */
    int[] exclude() default {};

    /**
     * Если аннотированный метод возвращает {@link java.util.List}, то его размер может быть ограничен.
     * <p>
     * Отрицательные значения и 0 не ограничивают список.
     */
    long limit() default 0;
}
