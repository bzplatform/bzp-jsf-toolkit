package jsftoolkit.controller;

import accesscontrol.entity.Policy;
import accesscontrol.entity.User;
import accesscontrol.entity.UserLogin;
import jsftoolkit.ejb.AuthenticationService;
import jsftoolkit.util.FacesUtils;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class AccessController {

   @EJB
   private AuthenticationService authenticationService;
   private UserLogin login = null;
   private User user = null;
   private String application = null;
   private List<Policy> policyList = null;
   private List<Policy> publicPolicyList = null;

   public AccessController() {
   }

   public UserLogin getLogin() {
      if (login == null) {
         login = new UserLogin();
      }
      return login;
   }

   public void setLogin(UserLogin login) {
      this.login = login;
   }

   public User getUser() {
      return user;
   }

   public Object doorAction() {
      if (user == null) {
         return "login";
      } else {
         FacesUtils.endSession();
         return "login";
      }
   }

   public String goHome() {
      return "home";
   }

   public String doLogin() {
      try {
         if (login != null) {
            User newUser = authenticationService.findUserByLogin(login);
            if (newUser != null) {
               user = newUser;
               login = null;
               FacesContext context = FacesContext.getCurrentInstance();
               ExternalContext ec = context.getExternalContext();
               HttpServletRequest req = (HttpServletRequest) ec.getRequest();
               String remoteAddress = req.getHeader("x-forwarded-for") != null ? req.getHeader("x-forwarded-for") : req.getRemoteAddr();
//               InetAddress remoteInetAddress = InetAddress.getByName(req.getRemoteAddr());
//               System.out.println("REMOTE_ADDR:" + req.getRemoteAddr());
//               Enumeration headerNames = req.getHeaderNames();
//               while (headerNames.hasMoreElements()) {
//                  String headerName = (String) headerNames.nextElement();
//                  System.out.println(headerName + ":" + req.getHeader(headerName));
//               }
               if (remoteAddress != null && (remoteAddress.startsWith("10.") || remoteAddress.startsWith("192.") || remoteAddress.startsWith("127."))) {
                  policyList = authenticationService.findPoliciesByUser(user, getApplication());
               } else {
                  if (user.getRemoteIp() != null && ("*".equals(user.getRemoteIp()) || user.getRemoteIp().contains(remoteAddress))) {
                     policyList = authenticationService.findPoliciesByRemoteUser(user, getApplication());
                  }
               }
               return "home";
//               FacesContext context = FacesContext.getCurrentInstance();
//               ExternalContext ec = context.getExternalContext();
//               String referrer = (String) FacesUtils.getSessionObject("referrer");
//               if (referrer != null) {
//                  ec.redirect(viewIdToUri(referrer));
//                  return null;
//               } else {
//                  return "home";
//               }
            } else {
               FacesUtils.addErrorMessage("Invalid Login!");
            }
         }
      } catch (Exception e) {
         FacesUtils.addErrorMessage("Connection Failed!");
      }
      return null;
   }

   public void doSecurity() {
      FacesContext context = FacesContext.getCurrentInstance();
      if (context.getViewRoot() != null && context.getViewRoot().getViewId() != null && FacesUtils.getSessionObject(context.getViewRoot().getViewId()) == null) {
         try {
            ExternalContext ec = context.getExternalContext();
            String uri = context.getViewRoot().getViewId();
            if (isUriPermitted(uri)) {
               FacesUtils.putSessionObject(context.getViewRoot().getViewId(), true);
               System.out.println("ViewId:" + context.getViewRoot().getViewId());
            } else if (getUser() == null) {
               FacesUtils.putSessionObject("referrer", uri);
               ec.redirect("/" + getApplication() + "/login.jsf");
            } else {
               FacesUtils.putSessionObject("requiredAction", uri);
               ec.redirect("/" + getApplication() + "/denied.jsf");
            }
         } catch (Exception ex) {
            Logger.getLogger(AccessController.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public void trackReferrer() {
      FacesContext context = FacesContext.getCurrentInstance();
      if (context.getViewRoot() != null && context.getViewRoot().getViewId() != null) {
         if (context.getExternalContext().getRequestHeaderMap().containsKey("referer")
                 && !context.getExternalContext().getRequestHeaderMap().get("referer").matches(".*login\\.[^\\.]*$")
                 && !context.getExternalContext().getRequestHeaderMap().get("referer").equals(context.getViewRoot().getViewId())) {
            Boolean goBack = (Boolean) FacesUtils.getSessionObject("goBack");
            if (goBack == null || !goBack) {
               FacesUtils.putSessionObject(context.getViewRoot().getViewId() + "-referrer", context.getExternalContext().getRequestHeaderMap().get("referer"));
               System.out.println(context.getViewRoot().getViewId() + "-referrer" + ":" + context.getExternalContext().getRequestHeaderMap().get("referer"));
            } else {
               FacesUtils.putSessionObject("goBack", false);
            }
         }
      }
   }

   public String goBack() {
      FacesContext context = FacesContext.getCurrentInstance();
      if (context.getViewRoot() != null && context.getViewRoot().getViewId() != null) {
         if (FacesUtils.getSessionObject(context.getViewRoot().getViewId() + "-referrer") != null) {
            try {
               FacesUtils.putSessionObject("goBack", true);
               String back = (String) FacesUtils.getSessionObject(context.getViewRoot().getViewId() + "-referrer");
               context.getExternalContext().redirect(back);
               System.out.println("Redirect:" + context.getViewRoot().getViewId() + "=>" + back);
            } catch (IOException ex) {
               Logger.getLogger(AccessController.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
         }
      }
      return "home";
   }

   public boolean isUriPermitted(String uri) {
//      return (user == null && ! uri.matches(".*(login|denied|index)\\.[^\\.]*$")) ? false : true;
      if (user == null || policyList == null || policyList.isEmpty()) {
         return toBoolean(getEffect(getPublicPolicyList(), uri));
      } else {
         return toBoolean(getEffect(policyList, uri));
      }
   }

   public boolean isUriDenied(String uri) {
      return !isUriPermitted(uri);
   }

   private String getEffect(List<Policy> policyList, String uri) {
      String effect = null;
      if (policyList == null || uri == null) {
         return null;
      }
      for (Policy policy : policyList) {
         if (effect == null && uri.matches(policy.getUriExpression())) {
            effect = policy.getEffect();
            if ("DENIED".equals(effect)) {
               break;
            }
         } else if ("PERMIT".equals(effect) && policy.getEffect().equals("PERMIT")) {
            continue;
         } else if (uri.matches(policy.getUriExpression())) {
            effect = policy.getEffect();
            if ("DENIED".equals(effect)) {
               break;
            }
         }
      }
      return effect;
   }

   private boolean toBoolean(String effect) {
      if (effect == null || effect.equals("DENIED")) {
         return false;
      } else {
         return true;
      }
   }

   public String getUserName(int id) {
      User tUser = authenticationService.findUser(id);
      return (tUser != null) ? tUser.getName() : null;
   }

   public User userById(int id) {
      return authenticationService.findUser(id);
   }

   public String getApplication() {
      if (application == null) {
         application = FacesUtils.getInitParameter("com.medenterprise.APPLICATION");
      }
      return application;
   }

   private List<Policy> getPublicPolicyList() {
      if (publicPolicyList == null) {
         publicPolicyList = authenticationService.findPoliciesByRoleName("Public", getApplication());
      }
      return publicPolicyList;
   }

   private String viewIdToUri(String viewId) {
      return "/" + getApplication() + viewId.replaceFirst("\\.xhtml$", ".jsf");
   }

   public List all(String entityName) {
      try {
         List entityList = authenticationService.findByNamedQuery(entityName + ".findAll");
         return entityList;
      } catch (Exception ex) {
         Logger.getLogger(SessionController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return null;
   }

   public List userList() {
      return authenticationService.findByNamedQuery("User.list");
   }
}
