package com.medenterprise.jsftoolkit.util;

import java.util.ResourceBundle;
import java.text.MessageFormat;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.servlet.http.HttpSession;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtils {

	private static String RESOURCE_BASE_NAME = null;

	public static ResourceBundle messages;

  public FacesUtils() {
  }

	public static ResourceBundle getBundle(FacesContext context) {
		if (messages == null) {
			if (RESOURCE_BASE_NAME == null) {
				RESOURCE_BASE_NAME = context.getApplication().getMessageBundle();
			}
			messages = ResourceBundle.getBundle(RESOURCE_BASE_NAME);
		}
		return messages;
	}

	protected static FacesContext context() {
		return FacesContext.getCurrentInstance();
	}

	public static void log(String value) {
		context().getExternalContext().log(value);
	}

	public static void renderResponse() {
		context().renderResponse();
	}

  public static Object getManagedObject(String objectName) {
    Application app = context().getApplication();
    return app.evaluateExpressionGet(context(), "#{" + objectName + "}", Object.class);
  }

	public ValueExpression getValueExpression(String expression, Class[] params) throws Exception {
		String property = "#{" + expression + "}";
		Application app = context().getApplication();
		return app.getExpressionFactory().
				createValueExpression(context().getELContext(), property, Object.class);
	}
	
	public MethodExpression getActionExpression(String expression, Class[] params) throws Exception {
		String action = "#{" + expression + "}";
		Application app = context().getApplication();
		return app.getExpressionFactory().
				createMethodExpression(context().getELContext(), action, null, new Class[]{});
	}

	public static String getInitParameter(String key) {
		return context().getExternalContext().getInitParameter(key);
	}
	
	public static Object getObject(String key) {
		return context().getExternalContext().getRequestMap().get(key);
	}

	public static Object getSessionObject(String key) {
		return context().getExternalContext().getSessionMap().get(key);
	}

	public static Object putSessionObject(String key, Object obj) {
		return context().getExternalContext().getSessionMap().put(key, obj);
	}
  
	public static Object removeSessionObject(String key) {
		return context().getExternalContext().getSessionMap().remove(key);
	}  

	public static Object putRequestObject(String key, Object obj) {
		return context().getExternalContext().getRequestMap().put(key, obj);
	}

	public static Object getAppObject(String key) {
		return context().getExternalContext().getApplicationMap().get(key);
	}

	public static Object getParameter(String key) {
		return context().getExternalContext().getRequestParameterMap().get(key);
	}

	public static String getString(FacesContext context, String key) {
		String text;
		try {
			text = getBundle(context).getString(key);
		} catch (Exception e) {
			text = "???" + key + "???";
		}
		return text;
	}

	public static void message(String clientId, String key) {
		String text = null;
		try {
			text = getString(context(), key);
		} catch (Exception ex) {
			text = "???" + key + "???";
		}
		context().addMessage(clientId, new FacesMessage(text));
  }

	public static void addMessage(FacesContext context,	String clientId,
			String key,	FacesMessage.Severity severity,	Object... params) {
		String text = getString(context, key);
		if ((params != null) && (params.length > 0)) {
			text = MessageFormat.format(text, params);
		}
		context.addMessage(clientId,	new FacesMessage(severity, text, text));
	}

	public static void addErrorMessage(FacesContext context, String clientId,
			String key, Object... params) {
		addMessage(context, clientId, key, FacesMessage.SEVERITY_ERROR, params);
	}

	public static void addErrorMessage(String msg) {
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(null, facesMsg);
	}

	public static void addInfoMessage(FacesContext context, String clientId,
			String key, Object... params) {
		addMessage(context, clientId, key, FacesMessage.SEVERITY_INFO, params);
	}

	public static void addInfoMessage(String msg) {
		FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage("successInfo", facesMsg);
	}

	public static void endSession() {
		HttpSession session = (HttpSession)context().getExternalContext().
				getSession(false);
		if (session != null) session.invalidate();
	}

	public static java.io.InputStream getStream(String path) {
		FacesContext context = FacesContext.getCurrentInstance();
		javax.faces.context.ExternalContext external = context.getExternalContext();
		return external.getResourceAsStream(path);
	}
}