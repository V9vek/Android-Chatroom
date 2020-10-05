# Android-Chatroom
Firebase Chat Application where you can create chatrooms, join and leave

This is an application based on modern Android application tech-stacks and MVVM architecture.
This project is for focusing especially on the [Cloud Firestore](https://firebase.google.com/docs/firestore) and other features of Firebase

### Tech stack & Open-source libraries
- Minimum SDK level 21
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- Dagger-Hilt (alpha) for dependency injection.
- JetPack
  - LiveData - notify domain layer data to views.
  - Lifecycle - dispose of observing data when lifecycle state changes.
  - ViewModel - UI related data holder, lifecycle aware.
- Architecture
  - MVVM Architecture
  - Repository pattern
- [Navigation](https://developer.android.com/guide/navigation)
- [Glide](https://github.com/bumptech/glide)
- [Material-Components](https://github.com/material-components/material-components-android)
- [Circular Image View](https://github.com/hdodenhof/CircleImageView)

### Architecture
App is based on MVVM architecture and a repository pattern.

### Screenshots
<pre>
<img src="screenshots/screen_login.png" alt="login-screen" width="200"/> <img src="screenshots/screen_signup.png" alt="signup-screen" width="200"/> <img src="screenshots/screen_chats.png" alt="chat-screen" width="200"/> <img src="screenshots/screen_create_chatroom.png" alt="create-chatroom-screen" width="200"/> <img src="screenshots/screen_profile.png" alt="profile-screen" width="200"/> <img src="screenshots/screen_chatroom.png" alt="chatroom-screen" width="200"/>  <img src="screenshots/screen_chatroom_details.png" alt="chatroom-details-screen" width="200"/>  <img src="screenshots/screen_map.png" alt="map-screen" width="200"/>
</pre>

### Getting Started
* Clone or download repository as a zip file.
* Open project in Android Studio.
* Create Firebase project.
* Paste google-services.json file in app/ folder
* In Firebase console enable services Authentication, Cloud Firestore and Storage
* Finally run the app `SHIFT+F10`.

### TODO
- Realtime GPS updates of user location
- Directions with Google Directions API and much more
