package com.ineunet.knife.validation.annotation;

import java.lang.reflect.Method;

import javax.persistence.Column;

import com.ineunet.knife.core.validation.ValidatingProperty;
import com.ineunet.knife.core.validation.annotation.VProp;

/**
 * 
 * @author Hilbert Wang
 * @since 1.0.0
 * Created on 2015年3月19日 
 */
public class VPropTests {

	public static void main(String[] args) {
		Method[] methods = User.class.getDeclaredMethods();
		for (Method method : methods) {
			String methodName = method.getName();
			// match 'public * get*()'
			if (methodName.startsWith("get")) {
				System.err.println(methodName);
				Column c = method.getAnnotation(Column.class);
				System.out.println(c);
				VProp annot = method.getAnnotation(VProp.class);
				System.out.println(annot);
				if (annot != null) {
					ValidatingProperty prop = new ValidatingProperty(annot.name(), annot.title(), annot.length(), annot.nullable(), annot.type());
					System.out.println(prop);
				}
			}
		}
	}
	
}
