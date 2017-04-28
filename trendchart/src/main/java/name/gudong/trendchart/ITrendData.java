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

package name.gudong.trendchart;

import android.support.annotation.ColorInt;

/**
 * Created by GuDong on 2017/2/17 14:26.
 * Contact with ruibin.mao@moji.com.
 */

public interface ITrendData {
    /**
     * time for data (unit millisecond )
     * @return
     */
    long timestamp();

    /**
     * value for data
     * @return
     */
    int value();

    /**
     * aqi 精确数值对应的大概数值 非精确
     * @return
     */
    int warpValue();

    /**
     * text info in pop view used indicator current status
     * @return
     */
    String popTextInfo();

    /**
     * 等级级别值
     * @return
     */
    int colourLevel();


    @ColorInt int levelColor();
}