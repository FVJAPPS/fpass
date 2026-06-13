# Room
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class * extends com.fvjapps.fpass.db.AppDatabase { *; }
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-dontwarn androidx.room.paging.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep public class * extends com.bumptech.glide.GeneratedAppGlideModule

# Custom views referenced in XML
-keep public class com.fvjapps.fpass.views.SaturationBrightnessView
-keep public class com.fvjapps.fpass.views.HueSliderView

# DialogFragments
-keep public class * extends androidx.fragment.app.DialogFragment
