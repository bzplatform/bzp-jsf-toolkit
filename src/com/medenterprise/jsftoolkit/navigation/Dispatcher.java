package com.medenterprise.jsftoolkit.navigation;

import com.medenterprise.jsftoolkit.util.FacesUtils;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

public class Dispatcher extends NavigationHandler {

   private NavigationHandler handler;
   private String application;

   public Dispatcher(NavigationHandler handler) {
      super();
      this.handler = handler;
   }

   public String getApplication() {
      if (application == null) {
         application = FacesUtils.getInitParameter("com.medenterprise.APPLICATION");
      }
      return application;
   }

   public void handleNavigation(FacesContext fc, String actionMethod, String actionName) {
      if (actionName != null && actionName.startsWith("go:")) {
         try {
            FacesContext context = FacesContext.getCurrentInstance();
//            System.out.println(context.getViewRoot().getViewId());
            ExternalContext ec = context.getExternalContext();
            ec.redirect(actionName.replaceFirst("go:", "/" + getApplication() + "/") + ".jsf");
            context.responseComplete();
         } catch (IOException ex) {
            Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
         } finally {
            return;
         }
      } else if (actionName != null && actionName.equals("stay")) {
         FacesContext context = FacesContext.getCurrentInstance();
         ExternalContext ec = context.getExternalContext();
         HttpServletRequest request = (HttpServletRequest) ec.getRequest();
         try {
            ec.redirect(request.getRequestURL().toString());
            context.responseComplete();
         } catch (IOException ex) {
            Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
      handler.handleNavigation(fc, actionMethod, actionName);
   }
}
