package jsftoolkit.util;

import javax.el.*;
import java.util.*;
import java.lang.reflect.*;

public class MethodInvocationELResolver
    extends ELResolver
{
    public Object getValue(ELContext context, Object base, Object property)
        throws ELException
    {
//      System.out.println("base: " + base);
//		System.out.println("property: " + property);
        if(context == null) throw new NullPointerException();

        if(base == null) {
            if (property.toString().equals("#")) {
              context.setPropertyResolved(true);
              return new Evaluator();
            }
            // Unresolved
            return null;
        }
        
       
        if (base instanceof Evaluator) {
          Evaluator eval = (Evaluator) base;
          eval.setExpression((String)property);
          eval.setContext(context);
          eval.evaluate();
          context.setPropertyResolved(true);
          return eval.getObj();
        }

        ResolvedMethod meth;
        if (base instanceof ResolvedMethod) {
            meth = (ResolvedMethod) base;
            meth.add(property);
            context.setPropertyResolved(true);
            return meth.isReady()? meth.invoke(): meth;
        }

        meth = getMethod(base, property);
        if (meth != null) {
            context.setPropertyResolved(true);
            return meth.isReady()? meth.invoke(): meth;
        }

        return null;
    }

    private ResolvedMethod getMethod(Object base, Object property) {
        String methodName = property.toString();
        Class beanClass = base.getClass();
        for (Method method: beanClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return new ResolvedMethod(base, method);
            }
         }
         return null;
    }

    private static class ResolvedMethod {
        Object base;
        Method method;
        int parameterCount;
        ArrayList<Object> args = new ArrayList<Object>();

        public ResolvedMethod(Object base, Method method) {
            this.base = base;
            this.method = method;
            parameterCount = method.getParameterTypes().length;
        }

        public void add(Object arg) {
            args.add(arg);
        }

        public boolean isReady() {
            return parameterCount == args.size();
        }

        public Object invoke() {
            try {
                return method.invoke(base, args.toArray());
            } catch (Exception ex){
                throw new ELException(ex);
            }
        }
    }

    public Class getType(ELContext context, Object base, Object property)
        throws ELException
    {
        return null;
    }

    public void setValue(ELContext context, Object base, Object property,
        Object value)
        throws ELException
    {
    }

    public boolean isReadOnly(ELContext context, Object base,
        Object property)
        throws ELException
    {
        return true;
    }

    public Iterator<java.beans.FeatureDescriptor> getFeatureDescriptors(
            ELContext context, Object base) {
        return null;
    }

    public Class getCommonPropertyType(ELContext context,
                                                Object base)
    {
        return null;
    }
}
