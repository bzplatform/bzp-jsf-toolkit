package com.medenterprise.jsftoolkit.controller;

import com.medenterprise.jsftoolkit.ejb.CrudService;

import com.medenterprise.jsftoolkit.util.FacesUtils;
import com.medenterprise.jsftoolkit.util.JsftoolkitMap;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;

public class SessionController {

   @EJB
   private CrudService crudService;
   private String entityName;
   private String destination;
   private HashMap<String, String> referrerMap;
   private final int SEARCH_MAX_COUNT = 3000;
   private Map global;
   private Map counter;

   public SessionController _for(String entityName) {
      this.entityName = entityName;
      return this;
   }

   public String concat_(Object ... objs) {
      String result = "";
      for (Object obj : objs) {
         result += (obj != null) ? obj.toString() : "";
      }
      return result;
   }
   
   public String concat(Object obj1, Object obj2) {
      return concat_(obj1, obj2);
   }
   
   public String concat(Object obj1, Object obj2, Object obj3) {
      return concat_(obj1, obj2, obj3);
   }

   public Date addDays(Date startDate, Integer days){
      if (startDate == null || days == null) return startDate;
      Date newDate = new Date(startDate.getTime() + ((long)(days)) * 1000L * 3600L * 24L);
      return newDate;
   }

   public SessionController _do(Object obj) {
      return this;
   }

   public SessionController _put(String entityName, Object obj) {
      FacesUtils.putSessionObject(lcFirst(entityName), obj);
      return this;
   }

   public SessionController _putSearch() {
      FacesUtils.putSessionObject("search", new HashMap<String, Object>());
      FacesUtils.putSessionObject("searchRendered", true);
      return this;
   }

   public SessionController _putSearch(String entityName) {
      FacesUtils.putSessionObject(lcFirst(entityName) + "Search", new HashMap<String, Object>());
      FacesUtils.putSessionObject(lcFirst(entityName) + "SearchRendered", true);
      return this;
   }

   public SessionController _putOptions() {
      FacesUtils.putSessionObject("options", new HashMap<String, Object>());
      FacesUtils.putSessionObject("optionsRendered", false);
      return this;
   }

   public SessionController _runSearch(String entityName) {
      HashMap searchMap;
      if (FacesUtils.getSessionObject(lcFirst(entityName) + "Search") != null) {
         searchMap = (HashMap) FacesUtils.getSessionObject(lcFirst(entityName) + "Search");
      } else {
         searchMap = (HashMap) FacesUtils.getSessionObject("search");
      }
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc, searchMap, SEARCH_MAX_COUNT);
      FacesUtils.putSessionObject(lcFirst(entityName) + "List", queryResult);
      return this;
   }

   public SessionController _runSearch(String entityName, String order) {
      HashMap searchMap;
      if (FacesUtils.getSessionObject(lcFirst(entityName) + "Search") != null) {
         searchMap = (HashMap) FacesUtils.getSessionObject(lcFirst(entityName) + "Search");
      } else {
         searchMap = (HashMap) FacesUtils.getSessionObject("search");
      }
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, SEARCH_MAX_COUNT);
      FacesUtils.putSessionObject(lcFirst(entityName) + "List", queryResult);
      return this;
   }

   public SessionController _runSearch(String entityName, String order, HashMap<String, Object> searchMap, String resultName) {
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, SEARCH_MAX_COUNT);
      FacesUtils.putSessionObject(resultName, queryResult);
      return this;
   }

   public SessionController _runSearch(String entityName, String order, HashMap<String, Object> searchMap, String resultName, int limit) {
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, limit);
      FacesUtils.putSessionObject(resultName, queryResult);
      return this;
   }

   public List search(String entityName, String order, HashMap<String, Object> searchMap) {
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, SEARCH_MAX_COUNT);
      return queryResult;
   }
   
  public List hintedSearch(String entityName, String order, HashMap<String, Object> searchMap, String hint) {
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, SEARCH_MAX_COUNT, hint);
      return queryResult;
   }

   public List search(String entityName, String order, HashMap<String, Object> searchMap, int limit) {
      String querySrc = generateQuery(entityName, searchMap);
      List queryResult = crudService.search(querySrc + (!"".equals(order) ? " ORDER BY " + order : ""), searchMap, limit);
      return queryResult;
   }

   public int searchCount(String entityName, HashMap<String, Object> searchMap) {
      String querySrc = generateQuery(entityName, searchMap);
      return crudService.searchCount(querySrc, searchMap, 0);
   }

   private static String generateQuery(String entityName, HashMap<String, Object> searchMap) {
      String varName;
      String type;
      if (entityName.startsWith("_")) {
         type = "OR";
         entityName = entityName.substring(1);
      } else {
         type = "AND";
      }
      varName = entityName.substring(0, 1).toLowerCase();
      String query = "SELECT " + varName + " FROM " + ucFirst(entityName) + " " + varName;
      if (searchMap != null && !searchMap.isEmpty()) {
         query += " WHERE ";
         for (String param : searchMap.keySet()) {
            if (param == null) {
               continue;
            }
            Object value = searchMap.get(param);
            String paramFormatted = param.replace("start_", "").replace("end_", "").replace("_dot_", ".");
            if (value != null && param.startsWith(":")) {
              //do nothing
            } else if (value != null && !param.startsWith("_")) {
               if (value instanceof Date) {
                  if (param.startsWith("start_")) {
                     query += varName + "." + paramFormatted + " >= :" + param + " " + type + " ";
                  } else if (param.startsWith("end_")) {
                     query += varName + "." + paramFormatted + " <= :" + param + " " + type + " ";
                  } else {
                     query += varName + "." + paramFormatted + " = :" + param + " " + type + " ";
                  }
               } else if (value instanceof String) {
                  String str = (String) value;
                  if (!str.trim().equals("")) {
                     query += varName + "." + paramFormatted + " LIKE :" + param + " " + type + " ";
                  } else {
                     searchMap.put(param, null);
                  }
               } else {
                  query += varName + "." + paramFormatted + " = :" + param + " " + type + " ";
               }
            } else if (param.startsWith("_")) {
               query += param.replaceFirst("^_", "") + " " + type + " ";
            } else {
               searchMap.put(param, null);
            }
         }
         query = query.replaceFirst(" " + type + " $", "");
         query = query.replaceFirst(" WHERE $", "");
      }
      return query;
   }

   public SessionController _remove(String entityName) {
      FacesUtils.removeSessionObject(lcFirst(entityName));
      return this;
   }

   public SessionController _save(Object obj) {
      Object entity;
      if (obj instanceof String) {
         entity = FacesUtils.getSessionObject(lcFirst((String) obj));
      } else {
         entity = obj;
      }
      crudService.update(entity);
      return this;
   }

   public SessionController _create(Object obj) {
      crudService.create(obj);
      return this;
   }

//   public SessionController _flush() {
//      crudService.flush();
//      return this;
//   }

   public SessionController _refresh(Object obj) {
      Object entity;
      if (obj instanceof String) {
         entity = FacesUtils.getSessionObject(lcFirst((String) obj));
      } else {
         entity = obj;
      }
      crudService.refresh(entity);
      return this;
   }

   public SessionController _addToList(List list, Object obj) {
      list.add(obj);
      return this;
   }

   public SessionController _removeFromList(List list, Object obj) {
      list.remove(obj);
      return this;
   }

   public SessionController _to(String destination) {
      this.destination = destination;
      return this;
   }

   public SessionController _set(String entityName, String field, Object obj) {
      Object entity = FacesUtils.getSessionObject(lcFirst(entityName));
      Class entityClass = entity.getClass();
      for (Method m : entityClass.getMethods()) {
         if (m.getName().equals("set" + ucFirst(field))) {
            try {
               m.invoke(entity, obj);
            } catch (Exception ex) {
               Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
            }
         }
      }
      return this;
   }

   public SessionController _new(String className) {
      try {
         Class entityClass = Class.forName(className);
         Object entity = entityClass.newInstance();
         String varName = lcFirst(className.substring(className.lastIndexOf(".") + 1));
         FacesUtils.putSessionObject(varName, entity);
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return this;
   }

   public Object newObject(String className) {
      try {
         Class objClass = Class.forName(className);
         Object obj = objClass.newInstance();
         return obj;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public Object findObject(String className, Object id) {
      try {
         Class objClass = Class.forName(className);
         Object obj = crudService.find(id, objClass);
         return obj;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public SessionController _delete(Object obj) {
      try {
         Object entity;
         if (obj instanceof String) {
            entity = FacesUtils.getSessionObject(lcFirst((String) obj));
         } else {
            entity = obj;
         }
         crudService.delete(entity);
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return this;
   }

   public Date getNewDate() {
      return new Date();
   }

   public String go() {
      return go(destination);
   }
   
   public Object go(Object obj) {
      return obj;
   }

   public String leap() {
      return leap(destination);
   }

   public List all(String entityName) {
      try {
         List entityList = crudService.findByNamedQuery(entityName + ".findAll");
         return entityList;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public List findByNamedQuery(String namedQueryName, Map parameters) {
      try {
         List entityList;
         if (parameters != null) {
            entityList = crudService.findByNamedQuery(namedQueryName, parameters);
         } else {
            entityList = crudService.findByNamedQuery(namedQueryName);
         }
         return entityList;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public List findByNamedNativeQuery(String namedQueryName, Map parameters) {
      try {
         List entityList;
         if (parameters != null) {
            entityList = crudService.findByNamedNativeQuery(namedQueryName, parameters);
         } else {
            entityList = crudService.findByNamedNativeQuery(namedQueryName);
         }
         return entityList;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public Map newMap() {
      return new JsftoolkitMap();
   }

   public Map global(Object obj1) {
      if (global == null) {
         global = new HashMap();
      }
      if (!global.containsKey(obj1)) {
         global.put(obj1, new HashMap());
      }
      return (Map) global.get(obj1);
   }

   public void clearGlobal(Object obj1) {
      if (global != null && global.containsKey(obj1)) {
         global.remove(obj1);
      }
   }

   public Map global(Object obj1, Object obj2) {
      if (global == null) {
         global = new HashMap();
      }
      if (!global.containsKey(obj1)) {
         global.put(obj1, new HashMap());
      }
      if (!((Map) global.get(obj1)).containsKey(obj2)) {
         ((Map) global.get(obj1)).put(obj2, new HashMap());
      }
      return (Map) ((Map) global.get(obj1)).get(obj2);
   }

   public void clearGlobal(Object obj1, Object obj2) {
      if (global != null && global.containsKey(obj1) && ((Map) global.get(obj1)).containsKey(obj2)) {
         ((Map) global.get(obj1)).remove(obj2);
      }
   }

   public boolean delayClosed(Object obj1, Object obj2, int min) {
      if (counter == null) {
         counter = new HashMap();
      }
      Date current = new Date();
      if (! counter.containsKey(obj1 + ":" + obj2)) {
         counter.put(obj1 + ":" + obj2, current);
         return true;
      }
      if (current.getTime() - ((Date) counter.get(obj1 + ":" + obj2)).getTime() > (long) min * 1000L * 60L) {
         return false;
      } else {
         counter.put(obj1 + ":" + obj2, current);
         return true;
      }
   }

   private static String lcFirst(String name) {
      if (name == null || name.equals("")) {
         return name;
      }
      return name.substring(0, 1).toLowerCase() + name.substring(1);
   }

   private static String ucFirst(String name) {
      if (name == null || name.equals("")) {
         return name;
      }
      return name.substring(0, 1).toUpperCase() + name.substring(1);
   }

   public CrudService getCrudService() {
      return crudService;
   }

   public void setCrudService(CrudService crudService) {
      this.crudService = crudService;
   }

   public String save() {
      Object entity = FacesUtils.getSessionObject(lcFirst(entityName));
      crudService.update(entity);
      FacesUtils.removeSessionObject(lcFirst(entityName));
      return returnBack();
   }

   public String back() {
      return returnBack();
   }

   private String translate() {
      FacesContext context = FacesContext.getCurrentInstance();
      String viewId = context.getViewRoot().getViewId();
      String actionName = null;
      if (viewId != null) {
         actionName = viewId.replaceFirst("/(.*)\\.xhtml", "$1");
      }
      return actionName;
   }

   private String go(String to) {
      if (to == null || to.isEmpty()) {
         return null;
      }
      String from = translate();
      if (from != null) {
         if (referrerMap == null) {
            referrerMap = new HashMap();
         }
         referrerMap.put(ucFirst(to), from);
      }
      return "go:" + ucFirst(to);
   }

   private String leap(String to) {
      return "go:" + ucFirst(to);
   }

   public String stay() {
      return null;
   }

   private String returnBack() {
      String from = translate();
      if (referrerMap != null && referrerMap.get(from) != null) {
         return "go:" + referrerMap.get(from);
      } else {
         return null;
      }
   }

   public HashMap<String, String> getReferrerMap() {
      return referrerMap;
   }

   public void setReferrerMap(HashMap<String, String> referrerMap) {
      this.referrerMap = referrerMap;
   }

   public void listen(ValueChangeEvent event) {
   }

   public void listen(AjaxBehaviorEvent event) {
   }

   public void print(Object obj) {
      System.out.println(obj);
   }

   public void remove(List list, int index) {
      list.remove(index);
   }

   public void removeObj(List list, Object obj) {
      list.remove(obj);
   }
   
   public List list_(Object ... objs) {
      List list = new ArrayList();
      for (Object obj : objs) {
         list.add(obj);
      }
      return list;
   }
   
   public List list(Object obj) {
      return list_(obj);
   }
   
}
