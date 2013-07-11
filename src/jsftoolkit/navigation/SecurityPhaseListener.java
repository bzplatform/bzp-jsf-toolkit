package jsftoolkit.navigation;

import jsftoolkit.controller.AccessController;
import jsftoolkit.util.FacesUtils;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;


public class SecurityPhaseListener implements PhaseListener {
   private static final long serialVersionUID = 1L;

   @Override
   public void afterPhase(PhaseEvent pe) {
      try {
         AccessController accessController = (AccessController) FacesUtils.getManagedObject("access");
         if (pe.getPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            accessController.trackReferrer();
         }
      } catch (Exception ex) {
//         Logger.getLogger(SecurityPhaseListener.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   @Override
   public void beforePhase(PhaseEvent pe) {
//      FacesContext context = FacesContext.getCurrentInstance();
//      ExternalContext ec = context.getExternalContext();
//      HttpServletRequest req = (HttpServletRequest) ec.getRequest();
//      System.out.println("REMOTE IP:" + req.getRemoteAddr());

      AccessController accessController = (AccessController) FacesUtils.getManagedObject("access");
      accessController.doSecurity();
   }

   @Override
   public PhaseId getPhaseId() {
      return PhaseId.ANY_PHASE;
   }
}
