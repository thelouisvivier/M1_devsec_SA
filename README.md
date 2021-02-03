# M1 Secure Development : Mobile applications

### Our team :
 - CHAIGNE Hyacinthe
 - MARLARD Axel
 - POTAGES Pierre-Yves
 - VIVIER Louis


## How to verify user identity on opening ?
In order to verify who's launching the app we first thought of putting a password on the app, based on a schema or a random pattern based on the time and date of the year at a special opening time. The issue here is that if an attacker know the trick or the password, the protection would be void. And what is still known to be a strong security protection is with biometric authentification. Meaning only the registred fingerprint can allow an access to the app. We use biometric framework of Android to verify user identity on opening.


## How to securely store data on the phone ?
We were asked to allow the user to open the app even without an internet connection. So we used a local database to store data. Each time the app is opened or the user manually refresh it, we fetch fresh data from the API and then store it in the local database. Data is only accessible by the app, or a root access.


## How to hide the API url ?
To guarantee a protected source code and be sure that the API ur can't be find back, we obfuscated the project with the default installed obfuscator, Proguard. To test our procedure we compiled the APK, extracted the data from it, decompiled *.dex files to attempt retrieving clear code. [screenshot 1] As you can see, code has changed and API url isn't present in the MainActivity.java. After further investigation we didn't find back the url at all. Considering it safe.

As the Apk was compiled as a release version, the debuggind information shouldn't be accessible. Few permissions were used to reduce unecessary possibility of recovering through pentesting softwares. Thus, drozer provide a feature that shrink or erase unecessary code. Using Drozer we tried, with our level, to exploit or catch precious data. 
Here a simple screen of our attempt just to run a specific activity prompted from the command line : 
<br><img src="img/scrn2.png" alt="screenshot2" height="300"/>

## App Screenshots
<img src="img/scrn3.png" alt="screenshot3"/>
<img src="img/scrn4.png" alt="screenshot4"/>
