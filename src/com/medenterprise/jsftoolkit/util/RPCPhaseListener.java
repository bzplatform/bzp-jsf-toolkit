package com.medenterprise.jsftoolkit.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RPCPhaseListener implements PhaseListener {

   @Override
   public void afterPhase(PhaseEvent pe) {
      try {
         FacesContext context = FacesContext.getCurrentInstance();
         ExternalContext ec = context.getExternalContext();
         HttpSession session = (HttpSession) ec.getSession(true);
         HttpServletRequest req = (HttpServletRequest) ec.getRequest();
         HttpServletResponse res = (HttpServletResponse) ec.getResponse();
         if (req.getHeader("RPC-Request") != null) {
            Object bean = FacesUtils.getManagedObject((String) FacesUtils.getParameter("bean"));
            String methodName = (String) FacesUtils.getParameter("method");
            String[] args = ec.getRequestParameterValuesMap().get("args");
            Method method = null;
            for (Method m : bean.getClass().getMethods()) {
               if (m.getName().equals(methodName) &&
                       ((args == null && m.getParameterTypes().length == 0) ||
                       m.getParameterTypes().length == args.length)) {
                  method = m;
                  break;
               }
            }
            if (method != null) {
               method.invoke(bean, args);
            }
            session.setAttribute("messages", context.getMessageList());
            context.responseComplete();
         } else if(req.getHeader("Faces-Request") == null) {
            if (session.getAttribute("messages") != null) {
               List<FacesMessage> messages = (List<FacesMessage>)session.getAttribute("messages");
               for (FacesMessage message : messages) {
                  context.addMessage(null, message);
               }
               session.removeAttribute("messages");
            }
         }
      } catch (Exception ex) {
         Logger.getLogger(RPCPhaseListener.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void beforePhase(PhaseEvent pe) {
   }

   @Override
   public PhaseId getPhaseId() {
      return PhaseId.RESTORE_VIEW;
   }

}
