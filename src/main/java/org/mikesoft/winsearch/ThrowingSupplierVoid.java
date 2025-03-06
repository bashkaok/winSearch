package org.mikesoft.winsearch;

@FunctionalInterface
public interface ThrowingSupplierVoid<E extends Exception> {
    void get() throws E;
}
