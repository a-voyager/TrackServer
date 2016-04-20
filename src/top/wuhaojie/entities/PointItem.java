package top.wuhaojie.entities;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/20 12:20
 * Version: 1.0
 */
public class PointItem {

    public PointItem(String lat, String lng, String height, String time) {
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.time = time;
    }

    public String lat;
    public String lng;
    public String height;
    public String time;
}