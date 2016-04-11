package com.rushabh.decisiontrees;

/**
 * Created by rushabhnagda on 4/11/16.
 */
public interface Condition<F, C> {
    //no need to declare these as final, other than not changing the reference
    int nextNodeIndex(final F feature, final C features);
}