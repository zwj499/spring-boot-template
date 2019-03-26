package com.template.dal.func;

import java.util.function.Function;

public class Functions {
    public static <E> Function<E, E> identity() {
        return (Function<E, E>) IdentityFunction.INSTANCE;
    }

    private enum IdentityFunction implements Function<Object, Object> {
        INSTANCE;

        private IdentityFunction() {
        }

        @Override
        public Object apply(Object o) {
            return o;
        }

        @Override
        public String toString() {
            return "identity";
        }
    }
}