package com.sultanapp.moralstories.helper;

import android.content.Context;
import android.content.SharedPreferences;

public  class ApppreferenceManager {
     private SharedPreferences sharedPreferences;
     private Context context;
     public ApppreferenceManager (Context context){
         this.context=context;
         sharedPreferences = context.getSharedPreferences("APP_PREFERENCES",Context.MODE_PRIVATE);
     }

     public void setDarkModeState (boolean enable){
         SharedPreferences.Editor editor =  sharedPreferences.edit();
         editor.putBoolean("darkMode",enable);
         editor.apply();

     }

     public boolean  getDarkModeState (){
         return sharedPreferences.getBoolean("darkMode",false);
     }
}
