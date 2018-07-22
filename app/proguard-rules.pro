# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-printmapping build/outputs/mapping/release/mapping.txt

-dontwarn com.gc.materialdesign.views.**

-keep public class com.totemsoft.screenlookcount.db.** {
  *;
}

-keep public class com.totemsoft.screenlookcount.fragment.** {
  *;
}

-keep class com.totemsoft.screenlookcount.utils.Utils**
-keepclassmembers class com.totemsoft.screenlookcount.utils.Utils** {
    *;
}

-keep class com.totemsoft.screenlookcount.ActivityMain**
-keepclassmembers class com.totemsoft.screenlookcount.ActivityMain** {
    *;
}

-keep class com.totemsoft.screenlookcount.calendar.CalendarAdapter**
-keepclassmembers class com.totemsoft.screenlookcount.calendar.CalendarAdapter** {
    *;
}