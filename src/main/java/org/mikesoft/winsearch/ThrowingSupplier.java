package org.mikesoft.winsearch;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {
    T get() throws E;
}
