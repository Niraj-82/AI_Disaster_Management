# Firebase and Firestore rules
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep all of your data model classes.
# This is crucial for Firebase Firestore to be able to serialize/deserialize them.
-keep class com.example.resqai.model.** { *; }

# Keep names of fields and methods in data classes to prevent issues with reflection
-keepnames class com.example.resqai.model.** { *; }

# Keep public constructors of all data model classes
-keepclassmembers class com.example.resqai.model.** {
  <init>(...);
}
