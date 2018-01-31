# StickyDecoration
利用`RecyclerView.ItemDecoration`实现顶部悬浮效果

![效果](http://upload-images.jianshu.io/upload_images/1638147-89986d7141741cdf.gif?imageMogr2/auto-orient/strip)

## 支持
- **LinearLayoutManager**
- **GridLayoutManager**
- **点击事件**
- **分割线**

## 添加依赖
项目要求： `minSdkVersion` >= 14.
在你的`build.gradle`中 :
```gradle
repositories {
    jcenter()// If not already there
}
dependencies {
    compile 'com.gavin.com.library:stickyDecoration:1.3.1'
}
```

## 使用

#### 文字悬浮——StickyDecoration
> 注意
1：使用GridLayoutManager时，需要调用resetSpan；
2：使用recyclerView.addItemDecoration()之前，必须先调用recyclerView.setLayoutManager()；

代码：
```java
//回调
GroupListener groupListener = new GroupListener() {
    @Override
    public String getGroupName(int position) {
        //根据position获取对应的组名称
        return dataList.get(position).getProvince();
    }
};
//创建StickyDecoration，实现悬浮栏
StickyDecoration decoration = StickyDecoration.Builder
        .init(groupListener)
        //重置span（使用GridLayoutManager时必须调用）
        //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
        .build();
...
mRecyclerView.setLayoutManager(manager);
//需要在setLayoutManager()之后调用addItemDecoration()
mRecyclerView.addItemDecoration(decoration);
```
效果：

![LinearLayoutManager](http://upload-images.jianshu.io/upload_images/1638147-f3c2cbe712aa65fb.gif?imageMogr2/auto-orient/strip)
![GridLayoutManager](http://upload-images.jianshu.io/upload_images/1638147-e5e0374c896110d0.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**支持的方法：**
- **背景色（默认 #48BDFF）**
- **高度 (默认120px)**
- **字体颜色 （默认 Color.WHITE）**
- **字体大小 （默认 50px）**
- **分割线颜色（默认 #CCCCCC）**
- **分割线高宽度 (默认 0)**
- **边距   左边距（默认 10，仅适用于StickyDecoration）**
- **点击事件（返回当前分组下第一个item的position）**
- **重置（span注意：使用GridLayoutManager时必须调用）**

```java
StickyDecoration decoration = StickyDecoration.Builder
        .init(groupListener)
        .setGroupBackground(Color.parseColor("#48BDFF"))  //背景色（默认 #48BDFF）
        .setGroupHeight(DensityUtil.dip2px(this, 35))     //高度 (默认120px)
        .setGroupTextColor(Color.BLACK)                   //字体颜色 （默认 Color.WHITE）
        .setGroupTextSize(DensityUtil.sp2px(this, 15))    //字体大小 （默认 50px）
        .setDivideColor(Color.parseColor("#CCCCCC"))      //分割线颜色（默认 #CCCCCC）
        .setDivideHeight(DensityUtil.dip2px(this, 1))     //分割线高宽度 (默认 0)
        .setTextSideMargin(DensityUtil.dip2px(this, 10))  //边距   靠左时为左边距  靠右时为右边距（默认 10）
        .setOnClickListener(new OnGroupClickListener() {  //点击事件，返回当前分组下第一个item的position
            @Override
            public void onClick(int position) {
                //处理点击事件
            }
        })
        .resetSpan(mRecyclerView, (GridLayoutManager) manager)   //重置span（注意：使用GridLayoutManager时必须调用）
        .build();
```

### 自定义View悬浮——PowerfulStickyDecoration

先创建布局`item_group`
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
      xmlns:android="http://schemas.android.com/apk/res/android"
      android:id="@+id/ll"
      android:orientation="horizontal"
      ...>

    <ImageView
        android:id="@+id/iv"
        .../>

    <TextView
        android:id="@+id/tv"
        .../>
</LinearLayout>
```
创建`PowerfulStickyDecoration`，实现自定`View`悬浮
```java
PowerGroupListener listener = new PowerGroupListener() {
    @Override
    public String getGroupName(int position) {
        return dataList.get(position).getProvince();
    }

    @Override
    public View getGroupView(int position) {
        //获取自定定义的组View
        View view = getLayoutInflater().inflate(R.layout.item_group, null, false);
        ((TextView) view.findViewById(R.id.tv)).setText(dataList.get(position).getProvince());
        return view;
    }
};
PowerfulStickyDecoration decoration = PowerfulStickyDecoration.Builder
        .init(listener)
        .setGroupHeight(DensityUtil.dip2px(this, 40))       //高度 (默认120px)
        .setGroupBackground(Color.parseColor("#48BDFF"))    //背景色（默认 #48BDFF）
        .setDivideColor(Color.parseColor("#CCCCCC"))        //分割线颜色（默认 #CCCCCC）
        .setDivideHeight(DensityUtil.dip2px(this, 1))       //分割线高宽度 (默认 0)
        .setOnClickListener(new OnGroupClickListener() {    //点击事件，返回当前分组下第一个item的position
            @Override
            public void onClick(int position) {
                //处理点击事件
            }
        })
        .resetSpan(mRecyclerView, (GridLayoutManager) manager)   //重置span（注意：使用GridLayoutManager时必须调用）
        .build();

  ...
mRecyclerView.addItemDecoration(decoration);
```
效果：

![效果](http://upload-images.jianshu.io/upload_images/1638147-3fed255296a6c3db.gif?imageMogr2/auto-orient/strip)

**支持的方法：**
-  **高度 (默认120px)**
- **背景色（默认 #48BDFF）**
- **分割线颜色（默认 #CCCCCC）**
- **分割线高宽度 (默认 0)**
- **靠左/右显示  （默认 靠左）**
- **点击事件（返回当前分组下第一个item的position）**
- **重置span（注意：使用GridLayoutManager时必须调用）**

# 更新日志
----------------------------- 1.3.0 （2018-1-28）----------------------------

1、删除isAlignLeft()方法，需要靠右时，直接在布局中处理就可以了。

2、优化缓存机制。

----------------------------- 1.3.1 （2018-1-30）----------------------------
修改测量方式