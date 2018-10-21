# README

# ElderCare

This is a **client-side** Android application created by **Team Colombia**.

Team Colombia consists of members:
  * Emmanuel Macario -`macarioe@student.unimelb.edu.au`
  * James Marshall - `marshall1@student.unimelb.edu.au`
  * Alastair Paterson - `atp@student.unimelb.edu.au`
  * Kwan Hoe Marco Ho - `kwanh1@student.unimelb.edu.au`
  * Samuel Wright - `sam.wright787@gmail.com`
  
Our project was supervised by tutor **William Voorsluys**.

## Android Application
The **minimum required version** of Android is **Oreo**.

Before checking out the project, please ensure you have installed **Android 8.0 SDK** and
**Google Play Services** from the **Android SDK Manager**. Note that the **minimum targeted SDK version**
is **API Level 26**, whereas the **actual target version** is **API Level 28**.

## Application Summary & Usage
On start-up of the application, the user will be taken to the login page. From there,
they can choose to register a new account, or sign in with an existing account. If the
user is successfully authenticated, they will be taken to the main user interface of the
application, where they will be greeted with a map and a sliding up panel where they can
access all the features of the application.

## Features
In this section, we will describe the functionality of core features within our application.

### Authentication
We have chosen to use four different authentication methods to login, due to the varying
age demographics of our users. For the more younger and tech savvy demographic, we have
decided to leverage Facebook and Google sign-in. This allows users to quickly register or
sign into the application, without the inconvenience of registering a new account via
email and password. Moreover, users may be given peace of mind with respect to security
due to the credibility of these companies.

We have also chosen to add sign-in with email and phone number. These were added keeping
in mind the elderly users, who may not have access to a Facebook or Google account. These
are the traditional methods one would expect in a user-based mobile application.

### Map
The map is the base user interface for the application. We use the Google Maps API in order to 
implement the map which provides either a bird's-eye-view or street-view of the logged-in
user and their connected user (dependent or carer). Additionally, the user can enter
their destination in a search bar, which will then load a route to walk to the destination.

### User Tracking
Our application has the capability of one-to-one location based tracking. To achieve this,
a user must first register a connected user (dependent or carer) via their email address
in the settings. Once this is achieved, the user can toggle tracking the location of their
connected user via the a button that is overlayed on top of the map. One thing to note is that
a user may opt of having their location tracked at any point, by disabling permissions on start-up
of the application.

### Instant Text Messaging
Text chat is integrated into the application, allowing a user to send basic text messages
and images directly to any other user.

### Voice Call
Voice calling is another core feature of the application. We leverage the Sinch SDK
to be able to perform app-to-app calling between users. The default ringtone of the
user's Android phone will be used as the ringtone if an incoming call is received.
One thing to note is that the user must be in the application to receive calls from 
other users.

### Events
What differentiates our application is the ability to create real-time events that other
users can see and join. While creating an event, you can set the event's name, description,
location, and the maximum number of users which can join the event. These events can be added
to the user's Google calendar, which is then responsible for sending push notifications to
the user once the time of the event is near.

### Add Friends
The user can also search for other users, then send them friend requests. Once the request is accepted
by the other user, this will be registered in our database.


## Unit Testing
Unit tests for our application can be found in the directory `test/java/au/edu/unimelb/eldercare`.
