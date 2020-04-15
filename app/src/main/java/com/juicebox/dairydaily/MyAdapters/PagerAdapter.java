package com.juicebox.dairydaily.MyAdapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.juicebox.dairydaily.CowChart.CowTab1;
import com.juicebox.dairydaily.CowChart.CowTab10;
import com.juicebox.dairydaily.CowChart.CowTab11;
import com.juicebox.dairydaily.CowChart.CowTab12;
import com.juicebox.dairydaily.CowChart.CowTab13;
import com.juicebox.dairydaily.CowChart.CowTab14;
import com.juicebox.dairydaily.CowChart.CowTab15;
import com.juicebox.dairydaily.CowChart.CowTab16;
import com.juicebox.dairydaily.CowChart.CowTab17;
import com.juicebox.dairydaily.CowChart.CowTab18;
import com.juicebox.dairydaily.CowChart.CowTab19;
import com.juicebox.dairydaily.CowChart.CowTab2;
import com.juicebox.dairydaily.CowChart.CowTab20;
import com.juicebox.dairydaily.CowChart.CowTab3;
import com.juicebox.dairydaily.CowChart.CowTab4;
import com.juicebox.dairydaily.CowChart.CowTab5;
import com.juicebox.dairydaily.CowChart.CowTab6;
import com.juicebox.dairydaily.CowChart.CowTab7;
import com.juicebox.dairydaily.CowChart.CowTab8;
import com.juicebox.dairydaily.CowChart.CowTab9;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numberOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int i) {

        switch(i){
            case 0:
                CowTab1 cowTab1 = new CowTab1();
                return cowTab1;
            case 1:
                CowTab2 cowTab2 = new CowTab2();
                return cowTab2;
            case 2:
                CowTab3 cowTab3 = new CowTab3();
                return cowTab3;
            case 3:
                CowTab4 cowTab4 = new CowTab4();
                return cowTab4;
            case 4:
                CowTab5 cowTab5 = new CowTab5();
                return cowTab5;
            case 5:
                CowTab6 cowTab6 = new CowTab6();
                return cowTab6;
            case 6:
                CowTab7 cowTab7 = new CowTab7();
                return cowTab7;
            case 7:
                CowTab8 cowTab8 = new CowTab8();
                return cowTab8;
            case 8:
                CowTab9 cowTab9 = new CowTab9();
                return cowTab9;
            case 9:
                CowTab10 cowTab10 = new CowTab10();
                return cowTab10;
            case 10:
                CowTab11 cowTab11 = new CowTab11();
                return cowTab11;
            case 11:
                CowTab12 cowTab12 = new CowTab12();
                return cowTab12;
            case 12:
                CowTab13 cowTab13 = new CowTab13();
                return cowTab13;
            case 13:
                CowTab14 cowTab14 = new CowTab14();
                return cowTab14;
            case 14:
                CowTab15 cowTab15 = new CowTab15();
                return cowTab15;
            case 15:
                CowTab16 cowTab16 = new CowTab16();
                return cowTab16;
            case 16:
                CowTab17 cowTab17 = new CowTab17();
                return cowTab17;
            case 17:
                CowTab18 cowTab18 = new CowTab18();
                return cowTab18;
            case 18:
                CowTab19 cowTab19 = new CowTab19();
                return cowTab19;
            case 19:
                CowTab20 cowTab20= new CowTab20();
                return cowTab20;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
