package br.com.bit.ideias.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import br.com.bit.ideias.reflection.core.Introspector;
import br.com.bit.ideias.reflection.criteria.Restriction;
import br.com.bit.ideias.reflection.rql.Rql;
import br.com.bit.ideias.reflection.test.artefacts.ClasseDominio;

/**
 * @author Leonardo Campos
 * @date 24/11/2009
 */
public class SimplePerformanceTest {
    public static void main(String[] args) {
        String methodName = "getAtributoPrivadoString";
        String rql = String.format("from br.com.bit.ideias.reflection.test.artefacts.ClasseDominio where name eq '%s'", methodName);
        ClasseDominio classeDominio = new ClasseDominio();
        int times = 1000000;
        
        //========================================
        long init = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            classeDominio.getAtributoPrivadoString();
        }
        long finish = System.currentTimeMillis();
        
        System.out.println("Regular method call");
        long difference = finish - init;
        System.out.println(difference);
        //========================================
        
        //========================================
        init = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            try {
                Method declaredMethod = ClasseDominio.class.getDeclaredMethod(methodName);
                declaredMethod.invoke(classeDominio);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        finish = System.currentTimeMillis();
        
        difference = finish - init;
        System.out.println("Regular reflection lookup and method call");
        System.out.println(difference);
        //========================================
        
        
        //========================================
        init = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Introspector.inObject(classeDominio).method(methodName).invoke();
        }
        finish = System.currentTimeMillis();
        
        difference = finish - init;
        System.out.println("Introspector reflection lookup and method call");
        System.out.println(difference);
        //========================================
        
        
        
      //========================================
        init = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Method result = Introspector.createCriterion(ClasseDominio.class).add(Restriction.eq(methodName)).uniqueResult();
            try {
                result.invoke(classeDominio);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        finish = System.currentTimeMillis();
        
        difference = finish - init;
        System.out.println("Criterion method call");
        System.out.println(difference);
        //========================================
        
        
        //========================================
        init = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            Method method = Rql.getInstance().parse(rql).uniqueResult();
            try {
                method.invoke(classeDominio);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        finish = System.currentTimeMillis();
        
        difference = finish - init;
        System.out.println("RQL method call");
        System.out.println(difference);
        //========================================
    }
}
