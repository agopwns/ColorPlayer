package com.example.colorplayer.utils;

public class YoutubeUtil {

    public static String convertDurationString(String time) {
        time = time.substring(2);

        if(time.contains("H"))
            time = time.replace("H", ":");

        if(time.contains("M"))
            time = time.replace("M", ":");

        if(time.contains("S"))
            time = time.replace("S", "");

        return time;
    }

    public static String convertViewCount(String viewCount){

        if(viewCount.length() > 9){
            // 소수점 없는 억 ex) 64억, 100억
            viewCount = viewCount.substring(0, viewCount.length() - 8) + "억";

        } else if(viewCount.length() == 9){
            // 억 ex) 6.4억
            viewCount = viewCount.substring(0, 1) + "." + viewCount.substring(1, 2) + "억";

        } else if(viewCount.length() > 4 && viewCount.length() < 9){
            // 만 ex) 1000만, 100만, 10만, 1만
            viewCount = viewCount.substring(0, viewCount.length() - 4)+ "만";

        }

        return viewCount;
    }

    public long convertDuration(String time) {
        time = time.substring(2);
        long duration = 0L;
        Object[][] indexs = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for(int i = 0; i < indexs.length; i++) {
            int index = time.indexOf((String) indexs[i][0]);
            if(index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexs[i][1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }
        return duration;
    }
}
