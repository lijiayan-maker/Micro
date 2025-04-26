## 简单说明
工程主要用来夯实基础，目的是为了技术整理和技术学习，个人主要定义为试验田，所以工程不可避免会臃肿，涉及CameraX Api，实际开发业务中自定义封装的功能组件
工作经验包括统相机，手机SystemUI，车机SystemUI，iOS端、鸿蒙端K歌应用，电话手表表端应用

### 1. Camera2Fragment
当前已实现能力：相机预览聚焦框、手动追焦、自定义快门、前置拍照、闪光灯

### 2. CameraXFragment
当前已实现能力：

### 3.RecyclerView Item 侧滑不可删除回弹能力
| 功能                 | 代码路径                           |
| 通知中心--常驻通知回弹逻辑  | com/example/View/anim/NItemTouchHelper            |
NItemTouchHelper拷贝自RecyclerView提供的ItemTouchHelper（侧滑删除、拖拽移动）
具体修改搜索：Micro.zhangcanyan

其实本质上NItemTouchHelper与ItemTouchHelper 一模一样，
但是为了实现同一个recyclerView列表不同的ViewHolder能区分出想要侧滑删除和侧滑不可删除的效果，改造了getSwipeEscapeVelocity方法

如何使用：
1.创建处理Item划走的工具类 xxxItemTouchHelper/Callback extends ItemTouchHelper.Callback
2.重写getSwipeEscapeVelocity和getSwipeThreshold方法，两者任一符合判断即判断会触发onSwiped会从数据源中移除该Item，并更新UI
3.getSwipeEscapeVelocity （滑动判定速度, 每秒移动的像素个数, 达到该速度后, 才可以被判定为滑动）
    设置为Integer.MAX_VALUE，确保无法触发onSwiped方法
4.getSwipeThreshold (用户滑动距离, 设置的是比例值, 默认返回值为 0.5 , 就意味着滑动宽度/高度的一半, 才触发侧滑 onSwiped 方法)
    同理设置为Integer.MAX_VALUE


## 组件能力
### ThreadManager 工作线程管理类
### HalfFragment 半屏展示

## 计划 不定时更新
目标是朝着现代应用架构MVI+Kotlin协程+Flow收集，Jetpack组件方向（以上工作中都有涉及）
全职工作，更聚焦工作本身，所以更新不定时。当起规划待实现能力如下：
1.抽屉式半屏增加毛玻璃
2.增加照片滤镜
3.Dragger2依赖注解
4.手帐Fragment
5.适配黑夜白天模式，规范化切换流程降低耗时
