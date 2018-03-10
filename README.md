# 炫彩计步器
<div align="center">
<img src="screenshots/主页.jpg" width="30%" height="30%"/>
<img src="screenshots/锻炼计划.jpg" width="30%" height="30%"/>
<img src="screenshots/历史记录.jpg" width="30%" height="30%"/>
</div>


# 计步算法参考

 * [xbase](http://www.jianshu.com/p/5d57f7fd84fa)

 * [finnfu](https://github.com/finnfu/stepcount/tree/master/demo%E4%BB%A5%E5%8F%8A%E7%AE%97%E6%B3%95%E6%96%87%E6%A1%A3)

# 注意：需要在AndroidManifest.xml中添加计步所需要的权限

```xml
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-feature android:name="android.hardware.sensor.accelerometer" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-feature
        android:name="android.hardware.sensor.stepcounter"
        android:required="true" />
    <uses-feature
        android:name="android.hardware.sensor.stepdetector"
        android:required="true" />
```

