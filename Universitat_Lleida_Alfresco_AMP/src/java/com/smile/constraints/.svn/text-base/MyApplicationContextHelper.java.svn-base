package com.smile.constraints;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class MyApplicationContextHelper implements ApplicationContextAware {

   private static ApplicationContext applicationContext;
   
   public void setApplicationContext(ApplicationContext arg0) throws BeansException {
      applicationContext = arg0;
   }
   
   public static ApplicationContext getApplicationContext(){
      return applicationContext;
   }
   
   public static Object getBean(String beanName){
      if(beanName == null || beanName.trim().length() == 0){
         return null;
      }
      else{
         return applicationContext.getBean(beanName);
      }
   }
}