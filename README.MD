# JLikeWidget

模仿即刻APP点赞的效果动画.

## 效果预览

![preview](/preview/animation.gif)

## 实现过程

即刻APP的效果 ![jike](/preview/jike.gif)



主要分为两个部分点赞左侧触碰缩小、离开放大的icon和滑动增大减小的数字。

### InteractionView

很简单,触碰(ACTION_DOWN)的时候就触发 `animateShrink()`方法. 缩小icon

离开(ACTION_UP)时触发 `animateExpand()`方法.将icon回归原来大小.

另外得在放大动画的过程中有一个不断扩散的圆环,下面是圆环绘制的代码

```java
        //在扩张动画中绘制一个扩散的圆圈.
        if (isExpanding && isSelected && scaleCoefficient <= 1) {
            //由于scaleCoefficient的扩展时变化的范围是 [0.8,1]
            //所以先将其映射成 [0,1]的区间
            final float scale = 1 - (1 - scaleCoefficient) * 5;
            //对圆圈进行放大或者缩小
            scale(scaleCircleRect, scale * MAX_COEFFICIENT);
            //确保圆圈的中点绘制在中央
            int dx = getWidth() / 2 - scaleCircleRect.width() / 2;
            int dy = getHeight() / 2 - scaleCircleRect.height() / 2;
            scaleCircleRect.offset(dx, dy);
            drawableCircle.setBounds(scaleCircleRect);
            //圆圈在扩散过程中,慢慢变得透明
            drawableCircle.setAlpha((int) ((1 - scale) * 255));
            drawableCircle.draw(canvas);
            //重置scaleCircleRect
            scaleCircleRect.set(0, 0, getWidth(), getHeight());
        }
```

### SlidingNumberView

让数字切换产生滑动效果,也不算太复杂吧.把主要实现的思路写一下.

假设有 10219 这一个数字 ,那么首先将它切分开,成为 1,0,2,1,9 再分别绘制

![10219](/preview/10219_1.png)

这样绘制整个数字的过程就被切分开来,只需要绘制一个数字,然后偏移一下横坐标,再接着绘制下一个数字就行.

就拿绘制最后一个数字的过程来说一下吧.

![9](/preview/10219_2.png)

绘制的时候使用一个纵坐标偏移offsets来实现滚动效果

假设我们现在是从 10219的状态跳转到 10218的状态.

那么此时第5个数字的偏移offsets[4] = -H  (H是数字的高度)

那么将 offset[4] 慢慢从 -H 变为 0 的过程,就是将 8 慢慢从9上方滑动到9位置的过程.

于是从 10219 滑动到 10220 时,实际过程就是 offset[3]和 offset[4]从 偏移 H 递减到0的过程.

把绘制一个数字(显示效果是绘制一个数字,其实是绘制一列数字)的代码贴出来吧.

```java
    /**
     * 把数字分为多列
     * 绘制出指定一列的数字
     *
     * @param i       数字在 [numbers] 中的序号
     * @param offsetX 该数字绘制时的起始横坐标
     */
    private void drawDigitColumn(Canvas canvas, int i, final int offsetX) {
        if (DEBUG) {
            Log.i(TAG, "开始绘制第 " + i + " 列数字");
        }
        //当前需要绘制的数字
        final int currentDigit = numbers[i];
        //找到目的数字的偏移
        int offsetY = offsets[i];
        if (offsetY > 0) {
            //如果需要绘制的数字的偏移大于0
            //则表明此时的动画状态在处于将目的数字向下滚动，如将0向下滚动，替换掉当前显示的1

            //由于数字滚动的距离可能会跨越其他数字，比如 0 向下滚动替换掉 8，所以需要一个循环
            int n = currentDigit;
            while (true) {
                int height = drawDigit(canvas, n, offsetX, offsetY);
                offsetY -= height;
                //从当前数字开始,不断向上绘制,直到超过上界 -height
                if (offsetY < -height) {
                    break;
                }
                n = getPreviousDigit(n);
            }

        } else {
            //表示需要向上滚动
            int n = currentDigit;
            while (true) {
                int height = drawDigit(canvas, n, offsetX, offsetY);
                offsetY += height;
                //从当前数字开始,不断向下绘制,直到超过下界 height
                if (offsetY >= height) {
                    break;
                }
                n = getNextDigit(n);
            }
        }

    }
```

