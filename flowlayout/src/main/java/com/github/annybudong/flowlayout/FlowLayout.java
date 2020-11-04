package com.github.annybudong.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 * Idea: 不要使用自定义属性，就一个java类，要简洁要轻量级，这样才能maven依赖
 */
public class FlowLayout extends ViewGroup {

    //按行存储所有的子view
    private List<List<View>> allViews = new ArrayList<>();

    //存储某一行的子view
    private List<View> lineViews = new ArrayList<>();

    //存储每一行的高度
    private List<Integer> lineHeights = new ArrayList<>();

    private int childSpacing;
    private int rowSpacing;

    //直接new时会调用
    public FlowLayout(Context context) {
        this(context, null);
    }

    //从布局文件加载时（无自定义属性）
    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //从布局文件加载时（有自定义属性）
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        childSpacing = ta.getDimensionPixelSize(R.styleable.FlowLayout_flow_childSpacing, 0);
        rowSpacing = ta.getDimensionPixelSize(R.styleable.FlowLayout_flow_rowSpacing, 0);
        ta.recycle();
    }

    /**
     * 三部曲之一onMeasure，测量View自身的宽高，测量子view的宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);      //MeasureSpec.EXACTLY对应match_parent和100dp
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);    //MeasureSpec.AT_MOST对应wrap_content

        //wrap_content
        int width = 0;
        int height = 0;

        //记录每一行的宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {               //如果是Gone，则不参与FlowLayout宽高计算
                if (i == childCount - 1) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);   //测量子View宽高
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();   //子View的LayoutParams是由包含它的父容器决定的
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //不换行
            if (lineWidth + childWidth + getPaddingLeft() + getPaddingRight() <= widthSize) {
                lineWidth = lineWidth + childSpacing + childWidth;  //行宽累加
                lineHeight = Math.max(lineHeight, childHeight);     //得到当前行最大的高度
            } else {
                width = Math.max(width, lineWidth);
                height = height + lineHeight + rowSpacing;           //换行后FlowLayout的高度需要增加
                lineWidth = childWidth;                             //换行后行宽为换行后的第一个child的宽度
                lineHeight = childHeight;                           //换行后行高为换行后的第一个child的高度
            }

            /**
             * 如果是最后一个view，它所在行的的行宽和行高还没有参与FlowLayout的宽高计算，需要特殊处理一下
             */
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        System.out.println("zhaomin widthSize=" + widthSize + " heightSize=" + heightSize);
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : width + getPaddingLeft() + getPaddingRight(),       //如果宽度模式为EXACTLY，择直接取容器的宽度，否则取我们测量出的宽度
                heightMode == MeasureSpec.EXACTLY ? heightSize : height + getPaddingTop() + getPaddingBottom());   //如果高度模式为EXACTLY，择直接取容器的高度，否则取我们测量出的高度
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        allViews.clear();
        lineHeights.clear();
        lineViews.clear();

        int width = getWidth();     //此时可以调用getWidth()，因为onMeasure已经执行完毕

        //记录每一行的宽高
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //换行
            //换行
            if (lineWidth + childWidth + getPaddingLeft() + getPaddingRight() > width) {
                lineHeights.add(lineHeight + rowSpacing);
                allViews.add(lineViews);
                lineWidth = 0;
                lineHeight = childHeight;      //重置
                lineViews = new ArrayList<>();                                  //重置lineViews集合
            }

            lineWidth += childWidth + childSpacing;
            lineHeight = Math.max(lineHeight, childHeight);
            lineViews.add(child);
        } //for end

        lineHeights.add(lineHeight);
        allViews.add(lineViews);


        //设置子view的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNumber = allViews.size();
        for (int i = 0; i < lineNumber; i++) {
            lineViews = allViews.get(i);
            lineHeight = lineHeights.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left;
                int tc = top;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                //子View布局
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + childSpacing;
            }

            left = getPaddingLeft();
            top += lineHeight;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
