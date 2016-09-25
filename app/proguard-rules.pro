## General
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

## Xposed and Snorlax Module
-keep class com.icecream.snorlax.module.Snorlax{*;}
-keep class com.icecream.snorlax.app.SnorlaxApp{*;}
-keep class de.robv.android.xposed.**{*;}

## RxJava
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

## Retrolambda
-dontwarn java.lang.invoke.*
