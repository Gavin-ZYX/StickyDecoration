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
    compile 'com.gavin.com.library:stickyDecoration:1.4.7'
}
```

**最新版本**
[![](https://jitpack.io/v/Gavin-ZYX/StickyDecoration.svg)](https://jitpack.io/#Gavin-ZYX/StickyDecoration)

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

| 方法 | 功能 | 默认 |
|-|-|-|
| setGroupBackground | 背景色 | #48BDFF |
| setGroupHeight | 高度 | 120px |
| setGroupTextColor | 字体颜色 | Color.WHITE |
| setGroupTextSize | 字体大小 | 50px |
| setDivideColor | 分割线颜色 | #CCCCCC |
| setDivideHeight | 分割线高宽度 | 0 |
| setTextSideMargin | 边距(靠左时为左边距  靠右时为右边距) |  10 |
| setHeaderCount | 头部Item数量（仅LinearLayoutManager） |  0 |

|方法|功能|描述|
|-|-|-|
| setOnClickListener | 点击事件 | 设置点击事件，返回当前分组下第一个item的position |
| resetSpan | 重置 | 使用GridLayoutManager时必须调用 |

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

| 方法 | 功能 | 默认 |
| -- | -- | -- |
| setGroupHeight | 高度 | 120px |
| setGroupBackground | 背景色 | #48BDFF |
| setDivideColor | 分割线颜色 | #CCCCCC |
| setDivideHeight | 分割线高宽度 | 0 |
| setCacheEnable | 是否使用缓存| 使用缓存 |
| setHeaderCount | 头部Item数量仅LinearLayoutManager | 0 |

|方法|功能|描述|
|-|-|-|
| setOnClickListener | 点击事件 | 设置点击事件，返回当前分组下第一个item的position以及对应的viewId |
|  resetSpan | 重置span |使用GridLayoutManager时必须调用 |
| notifyRedraw | 通知重新绘制 | 使用场景：网络图片加载后调用方法使用) |
| clearCache | 清空缓存 | 在使用缓存的情况下，数据改变时需要清理缓存 |

**注意：**
**若使用网络图片时，在图片加载完成后需要调用**
```java
decoration.notifyRedraw(mRv, view, position);
```

**使用缓存时，若数据源改变，需要调用clearCache清除数据**

# 更新日志

----------------------------- 1.4.7 （2018-08-16）----------------------------

- fix：数据变化后，布局未刷新问题

----------------------------- 1.4.6 （2018-07-29）----------------------------

- 修改缓存方式
- 加入性能检测

----------------------------- 1.4.5 （2018-06-17）----------------------------

- 在GridLayoutManager中使用setHeaderCount方法导致布局错乱问题

----------------------------- 1.4.4 （2018-06-2）----------------------------

- 添加setHeaderCount方法
- 修改README
- 修复bug

----------------------------- 1.4.3 （2018-05-27）----------------------------

- 修复一些bug，更改命名

----------------------------- 1.4.2 （2018-04-2）----------------------------

- 增强点击事件，现在可以得到悬浮条内View点击事件（没有设置id时，返回View.NO_ID）

- 修复加载更多返回null崩溃或出现多余的悬浮Item问题（把加载更多放在Item中的加载方式）

----------------------------- 1.4.1 （2018-03-21）----------------------------

- 默认取消缓存，避免数据改变时显示出问题

- 添加clearCache方法用于清理缓存

----------------------------- 1.4.0 （2018-03-04）----------------------------

- 支持异步加载后的重新绘制（如网络图片加载）

- 优化缓存

- 优化GridLayoutManager的分割线

----------------------------- 1.3.1 （2018-01-30）----------------------------

- 修改测量方式

----------------------------- 1.3.0 （2018-01-28）----------------------------

- 删除isAlignLeft()方法，需要靠右时，直接在布局中处理就可以了。

- 优化缓存机制。
