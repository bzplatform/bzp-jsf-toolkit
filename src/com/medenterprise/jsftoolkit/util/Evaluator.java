package com.medenterprise.jsftoolkit.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ELContext;
import javax.faces.context.FacesContext;

public class Evaluator {

  private Object obj;
  private String expression;
  private ELContext context;
  private ArrayList<String> literals = new ArrayList<String>();

  public Evaluator() {
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public Object getObj() {
    return obj;
  }

  public void setObj(Object obj) {
    this.obj = obj;
  }

  public ELContext getContext() {
    return context;
  }

  public void setContext(ELContext context) {
    this.context = context;
  }

  public void evaluate() {
    obj = FacesContext.getCurrentInstance().getApplication().getExpressionFactory().
          createValueExpression(context, "#{" + expression + "}", Object.class).
          getValue(context);
  }

//  private boolean findAll(String patternString) {
//    Pattern pattern = Pattern.compile(patternString);
//    Matcher matcher = pattern.matcher(expression);
//    return matcher.find();
//  }
//
//  private void replaceAll(String from, String to) {
//    expression = expression.replaceAll(from, to);
//  }
//
//  private void replaceAllRecursive(String from, String to) {
//    while (findAll(from)) {
//      replaceAll(from, to);
//    }
//  }
//
//  private void escapeLiterals() {
//    replaceAll("\\\\\"", "\uA004");
//    int counter = 0;
//    Pattern p = Pattern.compile("(\"[^\"]*\")");
//    Matcher m = p.matcher(expression);
//    StringBuffer sb = new StringBuffer();
//    while (m.find()) {
//      m.appendReplacement(sb, new String(Character.toChars(0xA400 + counter++)));
//      literals.add(m.group(1));
//    }
//    m.appendTail(sb);
//    expression = sb.toString();
//  }
}
