package com.fastjrun.executor;

public interface ExecutorManagerable {
    default void start() {
    }
    default boolean canStop() {
        return false;
    }
    default boolean canStart() {
        return true;
    }
    default void stop() {
    }
}
