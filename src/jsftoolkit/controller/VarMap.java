package jsftoolkit.controller;

import java.util.LinkedHashMap;
import java.util.Map;

public class VarMap extends LinkedHashMap {

   private Object _;
   private boolean closed = false;

   @Override
   public Object put(Object key, Object value){
      if (key != null && key.toString().equals("_")) {
         _ = value;
         return _;
      }
      return super.put(key, value);
   }

   @Override
   public Object get(Object obj){
      if (obj != null && obj.toString().equals("_")) {
         return _;
      }
      if (obj != null && obj.toString().equals("$")) {
         this.clear();
         _ = null;
         return null;
      }

      if (! super.containsKey(obj) && ! closed) {
         super.put(obj, new VarMap());
      }
      return super.get(obj);
   }

   public void _(Object obj){
      _ = obj;
   }

   public Map map(){
      return new LinkedHashMap(this);
   }
   
   public Map close() {
      closed = true;
      return this;
   }
   
   public Map open() {
      closed = false;
      return this;
   }

}
