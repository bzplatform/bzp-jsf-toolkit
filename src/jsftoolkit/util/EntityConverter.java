package jsftoolkit.util;

import jsftoolkit.controller.SessionController;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class EntityConverter implements Converter {

   private SessionController jsf = null;
   private final Pattern ENTITY_PATTERN_ID = Pattern.compile("(.*)\\[id=(\\d+)\\]");
   private final Pattern ENTITY_PATTERN_CODE = Pattern.compile("(.*)\\[code=([^\\]]*)\\]");

   public Object getAsObject(FacesContext context, UIComponent component, String value) {
      Matcher entityMatcherById = ENTITY_PATTERN_ID.matcher(value);
      Matcher entityMatcherByCode = ENTITY_PATTERN_CODE.matcher(value);
      if (entityMatcherById.matches()) {
         try {
            String className = entityMatcherById.group(1);
            int id = Integer.parseInt(entityMatcherById.group(2));
            Class entityClass = Class.forName(className);
            if (jsf == null) {
               jsf = (SessionController) FacesUtils.getManagedObject("jsf");
            }
            Object entity;
            if (id == 0) {
               entity = jsf.getCrudService().find(id, entityClass);
               if (entity == null) { 
                  entity = entityClass.newInstance();
               }
            } else {
               entity = jsf.getCrudService().find(id, entityClass);
            }
            return entity;
         } catch (Exception ex) {
            Logger.getLogger(EntityConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
         }
      } else if (entityMatcherByCode.matches()) {
         try {
            String className = entityMatcherByCode.group(1);
            String code = entityMatcherByCode.group(2);
            Class entityClass = Class.forName(className);
            if (jsf == null) {
               jsf = (SessionController) FacesUtils.getManagedObject("jsf");
            }
            Object entity;
            if (code == null || code.isEmpty()) {
               entity = entityClass.newInstance();
            } else {
               entity = jsf.getCrudService().find(code, entityClass);
            }
            return entity;
         } catch (Exception ex) {
            Logger.getLogger(EntityConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
         }
      } else {
         return null;
      }
   }

   public String getAsString(FacesContext context, UIComponent component, Object value) {
      if (value == null) {
         return null;
      } else {
         return value.toString();
      }
   }
}
