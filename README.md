# Application Idea

MindLift is a mental wellness application designed to provide users a private space to express, record, and relieve their mental stress. In this application, users will have the opportunity to immerse themselves in a completely relaxing atmosphere according to their pace. Coming with 4 totally pragmatic features: diary, music, map and yoga, we hope to bring a dareful wish where users are able to eventually reach their inner peace, thus lifting their mind in a sense. Below, we will showcase our UI designs and the building blocks of the application.

# Component

# 1.Authentication Components
The app lets users sign up when an account is created for them, and the credentials are stored in the Firebase database. The user then logs in using their credentials, and the sign-in is authenticated, validating the username and password from Firebase. Once the user credentials are validated, the username is set in the UserViewModel to use the user sessions across different components. The user then lands on the Home Screen, where the four elements of the applications are displayed. When the user Signs out, the username in the UserViewModel is cleared, and the user lands on the login page.

# 2. Music Component
The music component serves a relaxing haven where users get to unload their mental stress through enjoying a list of handpicked music for them. From classical to hip hop, users can immerse themselves anytime in a wide selection of songs.

# 3. Yoga Component
The yoga feature in our app is meant to be a virtual personal assistant powered by AI algorithms to help users connect their body and soul by practicing the Surya Namaskar yoga stances/routines. From the home page the user clicks into the Balance and Bliss button which navigates to their personalized yoga home page as shown in (a-b). Currently we support 4 yoga stances in Surya Namaskar namely Dhadhasana, Pranamasana, Hasta Uttanasana and Bhujangasana. The home screen contains a horizontally scrollable menu bar that contains all the 4 poses as entry points. Each pose has a diagrammatic depiction of the correct stance, along with a textual description on how to perform the yoga pose. It also contains an animated progress spinner bar that tracks personalized statistics (accuracy of yoga stances performed by the specific user accumulated over all sessions during the current day).

Once the user navigates into a particular yoga stance, a front-facing camera screen opens up as shown in (c-e). For each pose we support there is a separate virtual instructor model that not only detects the key-points of the user in real-time but also gives tips/instructions in real-time on how to correct their pose to match the selected pose type. For instance in (d), the user has clicked into the PrayerPose and performs half of the pose correctly (standing in upright position). However, the user’s arms are stretched far apart, instead of facing each other. This is instantly detected by our AI based pose detector whereby an instruction is provided to the user as “Tip: Place your palm together in front of your chest” at the bottom of the camera view. The user then corrects their pose as seen in (e). Once mastered, the AI pose corrector displays a message saying “Beautiful alignment ! You’ve mastered the pose”. In addition to the tip, the top right of the screen contains a spinner bar showing the real-time accuracy of the yoga stance done by the user as visual cues in addition to textual ones. There are two buttons at the top of the screen “Home” and “Poses”, that takes the user back to our main Home page and Poses home screen resp. if they would like to switch the pose.

# 4. Diary Component
The user can speak out their thoughts in the microphone; the spoken notes are then transcribed and stored in the database for the day the user enters. From the diary notes, the user’s mood is predicted by passing a prompt to ChatGPT. This predicted mood will then be used to determine what genre of music should be recommended to the user on the music screen. The user can also view the diary notes on the View Diary Screen, displaying the diary for the current day. To view the diary on any other day, the user can navigate using the calendar; once a particular date is selected, the diary notes for that day are displayed.

# 5. Map Component
The map component is interactive and customized. It is not only used to locate users’ current location but also to immediately provide users with nearby places to work out and relax. It allows users to write down their review on a specific gym in a straightforward and simple way.


