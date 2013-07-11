package jsftoolkit.ejb;

import accesscontrol.entity.Policy;
import accesscontrol.entity.Role;
import accesscontrol.entity.User;
import accesscontrol.entity.UserLogin;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.*;

@Stateless
public class AuthenticationService {

   @PersistenceContext(unitName = "access-control")
   private EntityManager em;

   public User findUserByLogin(UserLogin login) {
      User user;
      Query query = em.createNamedQuery("User.findByLogin");
      query.setParameter("loginName", login.getName());
      query.setParameter("password", login.getPassword());
      try {
         user = (User) query.getSingleResult();
         return user;
      } catch (NoResultException ex) {
         return null;
      }
   }

   public User findUser(Integer userId) {
      Query query = em.createNamedQuery("User.findById");
      query.setParameter("id", userId);
      try {
         return (User) query.getSingleResult();
      } catch (NoResultException ex) {
         return null;
      }
   }

   public List<Role> findRolesByUser(Integer userId) {
      Query query = em.createNamedQuery("User.findRolesByUser");
      query.setParameter("userId", userId);
      return query.getResultList();
   }

   public List<Role> findRolesByRemoteUser(Integer userId) {
      Query query = em.createNamedQuery("User.findRolesByRemoteUser");
      query.setParameter("userId", userId);
      return query.getResultList();
   }

   public List<Policy> findPoliciesByRoleName(String name, String application) {
      Query query = em.createNamedQuery("Policy.findByRoleName");
      query.setParameter("name", name);
      query.setParameter("application", application);
      return query.getResultList();
   }

   public List<Policy> findPoliciesByUser(User user, String application) {
      Query query = em.createNamedQuery("Policy.findByUser");
      query.setParameter("user", user);
      query.setParameter("application", application);
      return query.getResultList();
   }

   public List<Policy> findPoliciesByRemoteUser(User user, String application) {
      Query query = em.createNamedQuery("Policy.findByRemoteUser");
      query.setParameter("user", user);
      query.setParameter("application", application);
      return query.getResultList();
   }

   public List<Object> findByNamedQuery(String namedQueryName) {
      return this.em.createNamedQuery(namedQueryName).getResultList();
   }

   public List<Object> findByNamedQuery(String namedQueryName, Map<String, Object> parameters) {
      return findByNamedQuery(namedQueryName, parameters, 0);
   }

   public List<Object> findByNamedQuery(String queryName, int resultLimit) {
      return this.em.createNamedQuery(queryName).
              setMaxResults(resultLimit).
              getResultList();
   }

   public List<Object> findByNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
      Set<Entry<String, Object>> rawParameters = parameters.entrySet();
      Query query = this.em.createNamedQuery(namedQueryName);
      if (resultLimit > 0) {
         query.setMaxResults(resultLimit);
      }
      for (Entry<String, Object> entry : rawParameters) {
         query.setParameter(entry.getKey(), entry.getValue());
      }
      return query.getResultList();
   }
}
