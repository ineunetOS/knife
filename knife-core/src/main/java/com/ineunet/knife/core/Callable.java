package com.ineunet.knife.core;

/**
 * 
 * @author hilbert.wang@hotmail.com
 * Created on 2015年12月6日
 * @param <V>
 * @since 2.2.6
 */
public interface Callable<V> {
    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     */
    V call();
}