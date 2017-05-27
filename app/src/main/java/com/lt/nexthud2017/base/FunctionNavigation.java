package com.lt.nexthud2017.base;

import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;

import android.R.bool;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lt.nexthud2017.R;

/**
 * Created by Administrator on 2017/5/27.
 */

public class FunctionNavigation extends AbsoluteLayout{
    private ImageView bluetooth_nav;
    private ImageView dashboard_nav;
    private ImageView location_nav;
    private ImageView wechat_nav;
    private ImageView music_nav;

    private ImageView currentView;
    private ImageView nextView;
    private ImageView previousView;

    public int currentIndex;
    private View view;

    private ArrayList<ImageView> imageList;

    public Boolean isDisplayed = false;

    public  FunctionNavigation(Context context) {
        super(context);
    }

    public  FunctionNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.function_navigation, this,true);
        bluetooth_nav = (ImageView)findViewById(R.id.bluetooth_nav);
        dashboard_nav = (ImageView) view.findViewById(R.id.dashboard_nav);
        location_nav = (ImageView) view.findViewById(R.id.location_nav);
        wechat_nav = (ImageView) view.findViewById(R.id.wechat_nav);
        music_nav = (ImageView) view.findViewById(R.id.music_nav);
        currentIndex = 0;
        imageList = new ArrayList<ImageView>();
        initialImageListWithoutNav();
        view.setVisibility(View.INVISIBLE);
    }

    public void initialImageListWithoutNav() {
        imageList.clear();
        imageList.add(dashboard_nav);
        imageList.add(location_nav);
        imageList.add(wechat_nav);
        imageList.add(music_nav);
        imageList.add(bluetooth_nav);
    }

    public void pressNextButton() {
        if(isDisplayed){
            turnToSmall(currentIndex);
            if (currentIndex==imageList.size()-1) {
                currentIndex = 0;
            }
            else {
                currentIndex++;
            }
            turnToBig(currentIndex);
        }else {
            turnToBig(currentIndex);
            displayNav();
            isDisplayed =true;
        }
    }

    public void pressCenterButton() {
        if (isDisplayed) {
            turnToSmall(currentIndex);
            isDisplayed = false;
            undisplayNav();
        }
    }

    private void displayNav() {
        view.setVisibility(View.VISIBLE);
    }

    private void undisplayNav() {
        view.setVisibility(View.INVISIBLE);
    }

    private void turnToBig(int index) {
        ImageView imageView = imageList.get(index);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width += 40;
        lp.height += 40;
        imageView.setLayoutParams(lp);
        imageView.setY(imageView.getY()-35);
        imageView.setX(imageView.getX()-20);
    }

    private void turnToSmall(int index) {
        ImageView imageView = imageList.get(index);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.width -= 40;
        lp.height -= 40;
        imageView.setLayoutParams(lp);
        imageView.setY(imageView.getY()+35);

        imageView.setX(imageView.getX()+20);
    }

}
