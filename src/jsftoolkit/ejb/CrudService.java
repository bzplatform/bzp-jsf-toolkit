package jsftoolkit.ejb;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Stateless
public class CrudService {

   @Resource
   SessionContext context;
   @EJB
   PersistenceResolverApi persistenceResolver;
   Calendar calendar = new GregorianCalendar();

   public EntityManager getEm(String entityName) {
      if (entityName == null) {
         return null;
      }
      String puName = persistenceResolver.getPersistenseUnitName(entityName.replaceFirst("^<([^>]+)>[\\w\\W]*$", "$1"));
      return EntityManager.class.cast(context.lookup(puName));
   }

   public <T> T create(T t) {
      EntityManager em = getEm(t.getClass().getPackage().getName() + "." + t.getClass().getSimpleName());
      em.persist(t);
      em.flush();
      em.refresh(t);
      return t;
   }
   
   public <T> T create(T t, String hint) {
      EntityManager em = getEm(hint);
      em.persist(t);
      em.flush();
      em.refresh(t);
      return t;
   }

   public <T> T refresh(T t) {
      EntityManager em = getEm(t.getClass().getPackage().getName() + "." + t.getClass().getSimpleName());
      em.refresh(t);
      return t;
   }
   
   public <T> T refresh(T t, String hint) {
      EntityManager em = getEm(hint);
      em.refresh(t);
      return t;
   }

//   public void flush() {
//      getEm().flush();
//   }
   @SuppressWarnings("unchecked")
   public <T> T find(Object id, Class<T> type) {
      EntityManager em = getEm(type.getPackage().getName() + "." + type.getSimpleName());
      return (T) em.find(type, id);
   }
   
   @SuppressWarnings("unchecked")
   public <T> T find(Object id, Class<T> type, String hint) {
      EntityManager em = getEm(hint);
      return (T) em.find(type, id);
   }

   public void delete(Object t) {
      EntityManager em = getEm(t.getClass().getPackage().getName() + "." + t.getClass().getSimpleName());
      Object obj = em.merge(t);
      em.remove(obj);
   }
   
   public void delete(Object t, String hint) {
      EntityManager em = getEm(hint);
      Object obj = em.merge(t);
      em.remove(obj);
   }

   public <T> T update(T t) {
      EntityManager em = getEm(t.getClass().getPackage().getName() + "." + t.getClass().getSimpleName());
      return (T) em.merge(t);
   }
   
    public <T> T update(T t, String hint) {
      EntityManager em = getEm(hint);
      return (T) em.merge(t);
   }

   public List<Object> findByNamedQuery(String namedQueryName) {
      EntityManager em = getEm(namedQueryName);
      return em.createNamedQuery(namedQueryName).getResultList();
   }

   public List<Object> findByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
      return findByNamedQuery(namedQueryName, parameters, 0);
   }

   public List<Object> findByNamedQuery(String namedQueryName, int resultLimit) {
      EntityManager em = getEm(namedQueryName);
      return em.createNamedQuery(namedQueryName).
              setMaxResults(resultLimit).
              getResultList();
   }

   public List<Object> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
      EntityManager em = getEm(namedQueryName);
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }
   
   public List<Object> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, String hint) {
      EntityManager em = getEm(hint);
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }

   public List<Object> findByNamedNativeQuery(String namedQueryName) {
      EntityManager em = getEm(namedQueryName);
      return em.createNamedQuery(namedQueryName).getResultList();
   }

   public List<Object> findByNamedNativeQuery(String namedQueryName, Map<Integer, Object> parameters) {
      return findByNamedNativeQuery(namedQueryName, parameters, 0);
   }
   
   public List<Object> findByNamedNativeQuery(String namedQueryName, int resultLimit) {
      EntityManager em = getEm(namedQueryName);
      return em.createNamedQuery(namedQueryName).
              setMaxResults(resultLimit).
              getResultList();
   }

   public void executeNamedNativeQuery(String namedQueryName, Map<Integer, Object> parameters) {
      EntityManager em = getEm(namedQueryName);
      Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      for (Entry<Integer, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      query.executeUpdate();
   }
   
   public void executeNamedNativeQuery(String namedQueryName, Map<Integer, Object> parameters, String hint) {
      EntityManager em = getEm(hint);
      Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      for (Entry<Integer, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      query.executeUpdate();
   }
   
   public void executeNamedQuery(String namedQueryName, Map<String, Object> parameters) {
      EntityManager em = getEm(namedQueryName);
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      query.executeUpdate();
   }
   
   public void executeNamedQuery(String namedQueryName, Map<String, Object> parameters, String hint) {
      EntityManager em = getEm(hint);
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      query.executeUpdate();
   }

   public List<Object> findByNamedNativeQuery(String namedQueryName, Map<Integer, Object> parameters, int resultLimit) {
      EntityManager em = getEm(namedQueryName);
      Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<Integer, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }
   
   public List<Object> findByNamedNativeQuery(String namedQueryName, Map<Integer, Object> parameters, int resultLimit, String hint) {
      EntityManager em = getEm(hint);
      Set<Entry<Integer, Object>> rawParameters = parameters.entrySet();
      Query query = em.createNamedQuery(namedQueryName);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<Integer, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }

   public List<Object> rawSearch(String querySource, Map<String, Object> parameters, int resultLimit, int startIndex) {
      EntityManager em = getEm(querySource);
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createQuery(querySource);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      if (startIndex > 0) {
         query.setFirstResult(startIndex);
      }
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }

   public List<Object> search(String querySource, Map<String, Object> parameters, int resultLimit, String hint) {
      EntityManager em;
      if (hint == null) {
         em = getEm(querySource);
      } else {
         em = getEm(hint);
      }
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createQuery(querySource);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<String, Object> entry : rawParameters) {
         if (entry.getValue() != null) {
            if (entry.getKey().startsWith("_")) {
               query.setParameter(entry.getKey().replaceFirst(".*:(\\w+).*", "$1"), entry.getValue());
            } else {
               if (entry.getValue() instanceof String) {
                  query.setParameter(entry.getKey().replaceFirst("^:", ""), entry.getValue() + "%");
               } else if (entry.getValue() instanceof Date && entry.getKey().replaceFirst("^:", "").startsWith("start_")) {
                  Date date = (Date) entry.getValue();
                  calendar.setTime(date);
                  calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                  query.setParameter(entry.getKey().replaceFirst("^:", ""), calendar.getTime());
               } else if (entry.getValue() instanceof Date && entry.getKey().replaceFirst("^:", "").startsWith("end_")) {
                  Date date = (Date) entry.getValue();
                  calendar.setTime(date);
                  calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                  query.setParameter(entry.getKey().replaceFirst("^:", ""), calendar.getTime());
               } else {
                  query.setParameter(entry.getKey().replaceFirst("^:", ""), entry.getValue());
               }
            }
         }
      }
      return query.getResultList();
   }

   public List<Object> search(String querySource, Map<String, Object> parameters, int resultLimit) {
      return search(querySource, parameters, resultLimit, null);
   }

   public int searchCount(String querySource, Map<String, Object> parameters, int resultLimit, String hint) {
      EntityManager em;
      if (hint == null) {
         em = getEm(querySource);
      } else {
         em = getEm(hint);
      }
      querySource = querySource.replaceFirst("SELECT\\s+(\\w+)", "SELECT COUNT($1)");
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = em.createQuery(querySource);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<String, Object> entry : rawParameters) {
         if (entry.getValue() != null) {
            if (entry.getValue() instanceof String) {
               query.setParameter(entry.getKey().replaceFirst("^:", ""), entry.getValue() + "%");
            } else if (entry.getValue() instanceof Date && entry.getKey().replaceFirst("^:", "").startsWith("start_")) {
               Date date = (Date) entry.getValue();
               calendar.setTime(date);
               calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
               query.setParameter(entry.getKey().replaceFirst("^:", ""), calendar.getTime());
            } else if (entry.getValue() instanceof Date && entry.getKey().replaceFirst("^:", "").startsWith("end_")) {
               Date date = (Date) entry.getValue();
               calendar.setTime(date);
               calendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59);
               query.setParameter(entry.getKey().replaceFirst("^:", ""), calendar.getTime());
            } else {
               query.setParameter(entry.getKey().replaceFirst("^:", ""), entry.getValue());
            }
         }
      }
      return ((Long) query.getSingleResult()).intValue();
   }

   public int searchCount(String querySource, Map<String, Object> parameters, int resultLimit) {
      return searchCount(querySource, parameters, resultLimit, null);
   }
}
