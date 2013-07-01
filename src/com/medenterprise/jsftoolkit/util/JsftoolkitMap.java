package com.medenterprise.jsftoolkit.util;
import java.util.HashMap;

public class JsftoolkitMap extends HashMap {

   public JsftoolkitMap add(Object key, Object value) {
      this.put(key, value);
      return this;
   }

}
