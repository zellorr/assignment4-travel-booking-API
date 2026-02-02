package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static void inspect(Object obj) {
        Class<?> clazz = obj.getClass();
        System.out.println("Class Name: " + clazz.getSimpleName());

        System.out.println("\nFields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(" - " + field.getName() + " (" + field.getType().getSimpleName() + ")");
        }

        System.out.println("\nMethods:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(" - " + method.getName());
        }
    }
}