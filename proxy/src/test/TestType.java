package test;


import javax.lang.model.type.TypeVariable;
import java.lang.reflect.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 8/25/13
 * Time: 9:17 PM
 */
public class TestType {

    private String stringField;
    private String[] stringArrField;
    private int intField;
    private Map<String, Object> mapField;
    private Set<Integer> integerField;
    private Class<? extends String> classField;
    private Map<Integer, List<String>>[] complex;

    public TestType(){
        for(final Field field : this.getClass().getDeclaredFields()){
            System.out.println("I am parsing field "+field.getName());
            printType(field.getGenericType(), 0);
        }
    }

    private void printType(final Type type, final int depth){
        final int newDepth = depth + 1;
        final StringBuilder sb = new StringBuilder();
        for(int index = 0; index < depth; index++){
            sb.append("  ");
        }
        if(type instanceof WildcardType){
            System.out.println(sb.toString()+"  "+type.getClass());
        } else if(type instanceof ParameterizedType){
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            System.out.println("This is a parameterozed Type with "+
                    parameterizedType.getActualTypeArguments().length+" parameters");
            System.out.println(sb.toString() +"   "+ parameterizedType.getRawType());
            for(final Type typeParas : parameterizedType.getActualTypeArguments()){
                printType(typeParas, newDepth);
            }
        } else if(type instanceof GenericArrayType){
            System.out.println("The following is an Array Type");
            printType(((GenericArrayType) type).getGenericComponentType(), newDepth);
        } else if(type instanceof TypeVariable){
            final TypeVariable typeVariable = (TypeVariable) type;
            System.out.println(sb.toString() +"   "+ typeVariable.getKind().getDeclaringClass());
        } else {
            System.out.println((Class)type);
        }

    }

    public static void p(final String... s){
        if(s == null){
            System.out.println("S = null");
        } else if(s.length == 0){
            System.out.println("empty");
        } else {
            for(final String ss : s){
                System.out.println(ss);
            }
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        new TestType();
    }


}
