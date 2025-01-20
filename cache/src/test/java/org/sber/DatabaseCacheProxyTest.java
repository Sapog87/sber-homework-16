package org.sber;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sber.example.Calculator;
import org.sber.example.CalculatorImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

class DatabaseCacheProxyTest {
    private static final String DB_URL = "jdbc:h2:mem:test;MODE=PostgreSQL";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    @Test
    @DisplayName("проверка обращения в кэш при повторном вызове с тем же аргументом")
    void testCachedResult() {
        DatabaseCacheProxy cacheProxy = new DatabaseCacheProxy(DB_URL, USER, PASSWORD);

        Calculator calculator = new CalculatorImpl();
        Calculator spiedCalculator = spy(calculator);
        Calculator proxy = (Calculator) cacheProxy.cache(spiedCalculator);

        List<Integer> result1 = proxy.fibonachi(15);
        //проверяем, что первое обращение идет к calculator
        verify(spiedCalculator, times(1)).fibonachi(15);

        List<Integer> result2 = proxy.fibonachi(15);
        //проверяем, что второе обращение идет в базу данных
        verify(spiedCalculator, times(1)).fibonachi(15);

        assertEquals(result1, result2);
    }

    @Test
    @DisplayName("проверка отсутсвия обращения в кэш при повторном вызове с другим аргументом")
    void test() {
        DatabaseCacheProxy cacheProxy = new DatabaseCacheProxy(DB_URL, USER, PASSWORD);

        Calculator calculator = new CalculatorImpl();
        Calculator spiedCalculator = spy(calculator);
        Calculator proxy = (Calculator) cacheProxy.cache(spiedCalculator);

        List<Integer> result1 = proxy.fibonachi(14);
        verify(spiedCalculator, times(1)).fibonachi(14);

        List<Integer> result2 = proxy.fibonachi(16);
        verify(spiedCalculator, times(1)).fibonachi(16);

        verify(spiedCalculator, times(1)).fibonachi(14);

        assertNotEquals(result1, result2);
    }
}