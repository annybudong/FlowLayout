# FlowLayout-安卓热门标签


## 使用

1. 直接在FlowLayout里面嵌入普通View或者在代码里addView，就可达到热门标签效果
2. 通过`flow_childSpacing`和`flow_rowSpacing`两个属性来控制横向间距和行间距

```
<com.github.annybudong.flowlayout.FlowLayout
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:flow_childSpacing="10dp"
    app:flow_rowSpacing="20dp">
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="30px"
        android:textColor="@android:color/black"
        android:text="Hello"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="30px"
        android:textColor="@android:color/black"
        android:text="ggegegg"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="30px"
        android:textColor="@android:color/black"
        android:text="各个恶搞文"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="30px"
        android:textColor="@android:color/black"
        android:text="咯咯咯"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="30px"
        android:textColor="@android:color/black"
        android:text="啾啾啾啾啾啾"/>
    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="@android:color/black"
        android:text="Android"/>
</com.github.annybudong.flowlayout.FlowLayout>
```