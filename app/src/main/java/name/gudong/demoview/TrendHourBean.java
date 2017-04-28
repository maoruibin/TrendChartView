/*
 * Copyright 2017 GuDong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package name.gudong.demoview;

import android.support.annotation.ColorInt;

import java.text.SimpleDateFormat;
import java.util.Date;

import name.gudong.trendchart.ITrendData;

/**
 * Created by GuDong on 2017/4/28 15:05.
 * Contact with gudong.name@gmail.com.
 */

class TrendHourBean implements ITrendData{
    public int colour_level;
    public String level;
    public long time;
    public int value;
    public int valueWrap;
    /**
     * 等级的颜色值
     */
    public @ColorInt
    int colorLevelInt;

    public TrendHourBean(){}
    public TrendHourBean(long time, int value,int colour_level,int colorInt,int valueWrap,String level) {
        this.time = time;
        this.value = value;
        this.colour_level = colour_level;
        this.colorLevelInt = colorInt;
        this.valueWrap = valueWrap;
        this.level = level;
    }

    @Override
    public long timestamp() {
        return time;
    }

    @Override
    public int value() {
        return value;
    }

    @Override
    public int warpValue() {
        return valueWrap;
    }
    @Override
    public String popTextInfo() {
//        return value+level;
        return parseTime(time)+" "+value + level;
    }

    private String parseTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date(time));
    }

    @Override
    public int colourLevel() {
        return this.colour_level ;
    }

    @Override
    public @ColorInt int levelColor() {
        return colorLevelInt;
    }
}
