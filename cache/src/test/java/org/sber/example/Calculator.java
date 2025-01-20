package org.sber.example;

import org.sber.annotation.Cache;

import java.util.List;

public interface Calculator {
    @Cache
    List<Integer> fibonachi(int n);
}
