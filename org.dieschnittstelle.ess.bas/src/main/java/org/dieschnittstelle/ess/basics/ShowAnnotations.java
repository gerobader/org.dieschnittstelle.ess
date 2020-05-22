package org.dieschnittstelle.ess.basics;


import org.dieschnittstelle.ess.basics.annotations.AnnotatedStockItemBuilder;
import org.dieschnittstelle.ess.basics.annotations.StockItemProxyImpl;
import org.dieschnittstelle.ess.basics.reflection.DisplayAs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowAnnotations {

	public static void main(String[] args) {
		// we initialise the collection
		StockItemCollection collection = new StockItemCollection(
				"stockitems_annotations.xml", new AnnotatedStockItemBuilder());
		// we load the contents into the collection
		collection.load();

		for (IStockItem consumable : collection.getStockItems()) {
			;
			showAttributes(((StockItemProxyImpl)consumable).getProxiedObject());
		}

		// we initialise a consumer
		//Consumer consumer = new Consumer();
		// ... and let them consume
		//consumer.doShopping(collection.getStockItems());
	}

	/*
	 * TODO BAS2
	 */
	private static void showAttributes(Object consumable) {
		try {
			Class cl = consumable.getClass();
			ArrayList<String> attrList = new ArrayList<String>();
			for (Field field : cl.getDeclaredFields()) {
				String fieldName;
				if (field.isAnnotationPresent(DisplayAs.class)) {
					DisplayAs nameAlias = field.getAnnotation(DisplayAs.class);
					fieldName = nameAlias.value();
				} else {
					fieldName = field.getName();
				}
				// dirty
				field.setAccessible(true);
				String fieldValue = String.valueOf(field.get(consumable));
				attrList.add(fieldName + ":" + fieldValue);

				/*
				//not so dirty
				String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				Method getterMethod = null;
				for (Method getter: cl.getDeclaredMethods()) {
					if (getterName.equals(getter.getName())) {
						getterMethod = getter;
					}
				}
				if (getterMethod != null) {
					String fieldValue = String.valueOf(getterMethod.invoke(consumable));
					attrList.add(fieldName + ":" + fieldValue);
				}
				*/
			}
			String objString = "{" + cl.getSimpleName() + " " + String.join(", ", attrList) + "}";
			show(objString);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
