package org.mikesoft.winsearch.utils;

@FunctionalInterface
public interface ThrowingSupplierVoid<E extends Exception> {
    void get() throws E;
}
