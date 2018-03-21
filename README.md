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
    compile 'com.gavin.com.library:stickyDecoration:1.4.0'
}
```

## 使用

#### 文字悬浮——StickyDecoration
> **注意**
使用recyclerView.addItemDecoration()之前，必须先调用recyclerView.setLayoutManager()；

代码：
```java
GroupListener groupListener = new GroupListener() {
    @Override
    public String getGroupName(int position) {
        //获取分组名
        return mList.get(position).getProvince();
    }
};
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

| 功能 | 方法 | 默认 |
|-|-|-|
| 背景色 | setGroupBackground |#48BDFF |
| 高度 | setGroupHeight | 120px |
| 字体颜色 | setGroupTextColor |Color.WHITE |
| 字体大小 | setGroupTextSize | 50px |
|分割线颜色 | setDivideColor | #CCCCCC |
| 分割线高宽度 | setDivideHeight | 0 |
| 边距(靠左时为左边距  靠右时为右边距) | setTextSideMargin |  10 |

|功能|方法|描述|
|-|-|-|
| 点击事件 | setOnClickListener | 返回当前分组下第一个item的position |
| 重置 | resetSpan | 使用GridLayoutManager时必须调用 |

**使用如下**
```java
StickyDecoration decoration = StickyDecoration.Builder
        .init(groupListener)
        .setGroupBackground(Color.parseColor("#48BDFF"))  //背景色
        .setGroupHeight(DensityUtil.dip2px(this, 35))     //高度
        .setGroupTextColor(Color.BLACK)                   //字体颜色
        .setGroupTextSize(DensityUtil.sp2px(this, 15))    //字体大小
        .setDivideColor(Color.parseColor("#CCCCCC"))      //分割线颜色
        .setDivideHeight(DensityUtil.dip2px(this, 1))     //分割线高宽度
        .setTextSideMargin(DensityUtil.dip2px(this, 10))  //边距
        .setOnClickListener(new OnGroupClickListener() {  //点击事件
            @Override
            public void onClick(int position) {
                //处理点击事件
            }
        })
        .resetSpan(mRecyclerView, (GridLayoutManager) manager)   //重置span
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
        return mList.get(position).getProvince();
    }

    @Override
    public View getGroupView(int position) {
        //获取自定定义的组View
        View view = getLayoutInflater().inflate(R.layout.item_group, null, false);
        ((TextView) view.findViewById(R.id.tv)).setText(mList.get(position).getProvince());
        return view;
    }
};
PowerfulStickyDecoration decoration = PowerfulStickyDecoration.Builder
        .init(listener)
         //重置span（注意：使用GridLayoutManager时必须调用）
        //.resetSpan(mRecyclerView, (GridLayoutManager) manager)
        .build();

  ...
mRecyclerView.addItemDecoration(decoration);
```
效果：

![效果](http://upload-images.jianshu.io/upload_images/1638147-3fed255296a6c3db.gif?imageMogr2/auto-orient/strip)

**支持的方法：**

| 功能 | 方法 | 默认 |
| -- | -- | -- |
| 高度 | setGroupHeight | 120px |
| 背景色 | setGroupBackground | #48BDFF |
| 分割线颜色 | setDivideColor | #CCCCCC |
| 分割线高宽度 | setDivideHeight | 0 |
| 是否使用缓存| setCacheEnable | 不使用缓存 |
| 采用强引用缓存View| setStrongReference | 软引用 |

|功能|方法|描述|
|-|-|-|
| 点击事件 | setOnClickListener | 返回当前分组下第一个item的position |
| 重置span | resetSpan | 使用GridLayoutManager时必须调用 |
| 通知重新绘制 | notifyRedraw | 使用场景：网络图片加载后调用(建议：配合setStrongReference(boolean)方法使用，体验更佳) |
| 清空缓存 | cleanCache | 在使用缓存的情况下，数据改变时需要清理缓存 |

**使用如下**
```java
PowerfulStickyDecoration decoration = PowerfulStickyDecoration.Builder
        .init(listener)
        .setGroupHeight(DensityUtil.dip2px(this, 40))     //设置高度
        .setGroupBackground(Color.parseColor("#48BDFF"))  //设置背景   默认透明
        .setDivideColor(Color.parseColor("#CCCCCC"))      //分割线颜色
        .setDivideHeight(DensityUtil.dip2px(this, 1))     //分割线高度
        //.setCacheEnable(true)                           //是否使用缓存
        //.setStrongReference(true)                       //设置强引用
        .setOnClickListener(new OnGroupClickListener() {   //点击事件，返回当前分组下的第一个item的position
            @Override
            public void onClick(int position) {
                //处理点击事件
            }
        })
        .build();
```
**注意：若使用网络图片时，在图片加载完成后需要调用**

（配合`setStrongReference(true)`方法使用，体验更佳）
```java
decoration.notifyRedraw(mRv, view, position);
```


# 更新日志

----------------------------- 1.4.1 （2018-03-21）----------------------------

1、默认取消缓存，避免数据改变时显示出问题

2、添加cleanCache方法用于清理缓存

----------------------------- 1.4.0 （2018-03-04）----------------------------

1、支持异步加载后的重新绘制（如网络图片加载）

2、优化缓存

3、优化GridLayoutManager的分割线

----------------------------- 1.3.1 （2018-01-30）----------------------------

修改测量方式

----------------------------- 1.3.0 （2018-01-28）----------------------------

1、删除isAlignLeft()方法，需要靠右时，直接在布局中处理就可以了。

2、优化缓存机制。