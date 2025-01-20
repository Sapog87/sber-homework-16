package org.sber.example;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CalculatorImpl implements Calculator {
    @Override
    public List<Integer> fibonachi(int n) {
        return Stream.iterate(new int[]{0, 1}, f -> new int[]{f[1], f[0] + f[1]})
                .limit(n)
                .map(f -> f[0])
                .collect(Collectors.toList());
    }
}