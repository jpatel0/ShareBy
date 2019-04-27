# ShareBy
This is an Android based, social media App.
Its main purpose is to bring neighborhood activities and chats closer by providing an online medium.
Create your neighborhood group via Google Maps Location and invite your neighbors, friends, and families.
Ask questions, borrow something, help others, and much more.
Be friendly to others and keep growing.
Inbuilt Chats to discuss among each other as well as among the whole group.


The whole Project is based on Firebase Cloud Service. User data is NOT stored locally on the device, so Internet connection is necessary to run the App.

More features coming soon...

### Screenshot
<p>
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190419-181817.png" width="30%" height="30%">
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190330-211703.png" width="30%" height="30%">
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190330-211559.png" width="30%" height="30%">
<br>
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190330-211722.png" width="30%" height="30%">
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190330-211927.png" width="30%" height="30%">
<br>
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190424-112317.png" width="30%" height="30%">
<img src="https://github.com/jpatel0/ShareBy/raw/master/scrnshots/Screenshot_20190424-112327.png" width="30%" height="30%">
</p>

 #### Quick explanation of project directory :
1. [firebase function](https://github.com/jpatel0/ShareBy/tree/master/firebase%20function) : It contains single file named index.js having cloud function.
2. [scrnshots](https://github.com/jpatel0/ShareBy/tree/master/scrnshots): Sample screenshot from mobile.

### Prerequisites

- Firebase project with Realtime database for android having package name "com.zero.shareby".
- google-services.json:  during firebase project creation, you will get the google-services.json file, download and save this file.
- Android SDK v28
-  Android Support Repository

### Open and Run Project

<b>For Android App:</b>
1. open android studio, select File -> Import -> "Existing Projects into your workspace".
2. Go to the path where you cloned the Repo: (repoFolder)\code
3. paste the google-services.json to "app" folder.
4. rebuild the project and run.

<b>For cloud function:</b>
1. initialize Firebase SDK for Cloud Functions as explained [here](https://firebase.google.com/docs/functions/get-started),
2. open index.js and paste the code from  "(repoFolder)\cloud function\index.js".
3. deploy the cloud function.


### Built With

- Language:  java for android, javascript for cloud function.
- [Firebase](https://firebase.google.com) : Realtime database, Firebase Auth, Firebase storage, Firebase config, Functions

### Author

- **Jay Patel**


### Support

Please feel free to submit [issues](https://github.com/jpatel0/ShareBy/issues) with any bugs or other unforeseen issues you experience.