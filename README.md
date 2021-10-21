# SecureDataSharingForDTN


* ## Table of content
   * ### Project description  
      

   * ### Install and usage
    

   * ### Project overview
   * ### APP architecture
   * ### Documents
   * ### How to cite
  
## Project description
  The project "Secure data sharing for delay tolerate network (DTN) for battlefield" is an android app that disseminate important data through a DTN network without rely on the cellular network.  The project is to create an android App that share data securely in a DTN which is created by cellphones without Celluar network. The repository contains the Android part of the project. 
  
  The central authority of the project is in repository:
    
  https://github.com/cxfcdcpu/ReVo_webtest
    
  The frontend of the centrol authority is in repository:
    
  https://github.com/cxfcdcpu/secure_data_sharing_dashboard


##  Install and usage
  The project is currently in development stage. To test the current Android APP, you will first need to install the Android studio in your computer with the following instruction:
  
  https://developer.android.com/studio/?gclid=CjwKCAjwn8SLBhAyEiwAHNTJbWFkWSMrBpef7kVRc9aQuzuopc7L7M0bD_Gj6T0omT5YBsbzAeVN3hoCCwkQAvD_BwE&gclsrc=aw.ds
  
  Then download this repository following the code:
  
      git clone https://github.com/cxfcdcpu/SecureDataSharingForDTN.git
      
  In Android Studio, in tool bar, select File/Open.. to open the cloned project folder /SecureDataSharingForDTN
  
  Wait for Android studio setup the environment. 
  
  Open file app/java/com.example.securedatasharingfordtn/http/KtorHttpClient
  
  Change the parameter BASE_URL to be the ip of your backend of the central authority which is explained in the central authority repository (https://github.com/cxfcdcpu/ReVo_webtest)
  
  Connect your Device to the Computer. Then press the run button near the tool bar. Be sure your backend and frontend should also working properly.
  
  You will be directed to the login page. Type in your username and password. Use camera to get the mission code from the frontend page. You should be able to login to the main fragment successfully. 
  
  You can revoke user in the neighbors page.
  
  You can open the connection page to send and receive image securely. In the connected devices page, you should write your policy in order to allow valid user to decrypt. 
  
  Enjoy. If you got any question let me know. 
  
  
## APP architecture

  The App architecture is in the report files in the repository.
  
  
