package com.example.securedatasharingfordtn;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelperFunctions {


    public static Timestamp convertStringToTimestamp(String strDate) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            // you can change format of date
            Date date = formatter.parse(strDate);
            Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (Exception e) {
            System.out.println("Exception :" + e);

            return null;
        }
    }


    public static long convertDateStringToLong(String strDate){
        try{
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = formatter.parse(strDate);
            return date.getTime();
        }catch (Exception e){
            System.out.println("Exception :" + e);

        }
        return 0;
    }
}
