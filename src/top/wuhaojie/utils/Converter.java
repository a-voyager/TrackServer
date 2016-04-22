package top.wuhaojie.utils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import top.wuhaojie.entities.PointItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/19 19:18
 * Version: 1.0
 */
public class Converter {

    public interface OnConverterChanged {
        void onConvertCompleted();
    }

    public void setOnConverterChangedListener(OnConverterChanged listenner) {
        listeners.add(listenner);
    }

    public void removeOnConverterChangedListener(OnConverterChanged listenner) {
        listeners.remove(listenner);
    }

    private List<OnConverterChanged> listeners = new ArrayList<>();


    public static void main(String[] args) {
//        try {
//            Converter converter = Converter.getConverter();
//            converter.setOnConverterChangedListener(() -> {
//                System.out.println("序列化完成!");
//            });
//            converter.xml2Kml(new File("data/test.xml"), new File("data/result.kml"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    private Converter() {
    }

    private volatile static Converter converter;


    /**
     * 获取Converter
     *
     * @return
     */
    public static Converter getConverter() {
        if (converter == null) {
            synchronized (Converter.class) {
                if (converter == null) {
                    converter = new Converter();
                }
            }
        }
        return converter;
    }


    public List<PointItem> readXml(File file) throws Exception {
        ArrayList<PointItem> list = new ArrayList<>();
        // 获取SAX阅读器对象
        SAXReader saxReader = new SAXReader();
        // 获取文件对象
        Document document = saxReader.read(file);
        // <WayPoints>
        Element rootElement = document.getRootElement();
        // <TrackWayPoint>
        Iterator<Element> wayPoints = rootElement.elementIterator();
        while (wayPoints.hasNext()) {
            Element element = wayPoints.next();
            String lat = (String) element.element("经度").getData();
            String lng = (String) element.element("纬度").getData();
            String height = (String) element.element("高程").getData();
            String timeStamp = (String) element.element("TimeStamp").getData();
//            System.out.println(lng + lat + height + timeStamp);
            // 转换为格式
//            String str = lat + "," + lng + "," + height;
            list.add(new PointItem(lat, lng, height, timeStamp));
        }
        return list;
    }


    public void xml2Kml(File srcFile, File desFile) throws Exception {
        Document document = DocumentHelper.createDocument();
        Element kmlElement = document.addElement("kml");
        kmlElement.addAttribute("xmlns", "http://www.opengis.net/kml/2.2");
        kmlElement.addNamespace("gx", "http://www.google.com/kml/ext/2.2");
        kmlElement.addNamespace("kml", "http://www.opengis.net/kml/2.2");
        kmlElement.addNamespace("atom", "http://www.w3.org/2005/Atom");

        // <Document>
        Element documentElement = kmlElement.addElement("Document");
        documentElement.addElement("name").setText("2011-09-13成南高速到遂宁.kml");

        // <Style id="failed">
        Element style = documentElement.addElement("Style");
        style.addAttribute("id", "failed");
        Element lineStyle = style.addElement("LineStyle");
        lineStyle.addElement("color").setText("990000ff");
        lineStyle.addElement("width").setText("2");

        // <StyleMap id="failed0">
        Element styleMap = documentElement.addElement("StyleMap");
        styleMap.addAttribute("id", "failed0");
        Element pair = styleMap.addElement("Pair");
        pair.addElement("key").setText("normal");
        pair.addElement("styleUrl").setText("#failed1");
        pair = styleMap.addElement("Pair");
        pair.addElement("key").setText("highlight");
        pair.addElement("styleUrl").setText("#failed");

        // <Style id="failed1">
        style = documentElement.addElement("Style");
        style.addAttribute("id", "failed1");
        lineStyle = style.addElement("LineStyle");
        lineStyle.addElement("color").setText("990000ff");
        lineStyle.addElement("width").setText("2");

        // <Placemark>
        Element placemark = documentElement.addElement("Placemark");
        placemark.addElement("name").setText("2011-09-13成南高速到遂宁");
        placemark.addElement("description").setText("通过成南高速到遂宁的航迹");
        placemark.addElement("styleUrl").setText("#failed0");
        Element multiGeometry = placemark.addElement("MultiGeometry");
        Element lineString = multiGeometry.addElement("LineString");
        // <coordinates>
        Element coordinates = lineString.addElement("coordinates");

        List<PointItem> list = readXml(srcFile);
        for (PointItem s : list) {
            coordinates.addText(s.lat + "," + s.lng + "," + s.height + " ");
        }

        write2File(document, desFile);

    }

    private void write2File(Document document, File file) throws Exception {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), format);
        writer.write(document);
        writer.flush();
        writer.close();
        for (OnConverterChanged o : listeners) {
            o.onConvertCompleted();
        }

    }


    public void xml2Gpx(File srcFile, File desFile) throws Exception {
        Document document = DocumentHelper.createDocument();
        Element gpx = document.addElement("gpx");
        gpx.addAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
        gpx.addAttribute("creator", "MapSource 6.15.6");
        gpx.addAttribute("version", "1.1");
        gpx.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        gpx.addAttribute("xsi:schemaLocation", "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

        // <metadata>
        Element metadata = gpx.addElement("metadata");
        Element link = metadata.addElement("link");
        link.addAttribute("href", "http://www.garmin.com");
        link.addElement("text").setText("Garmin International");
        metadata.addElement("time").setText("2012-02-23T19:48:33Z");
        Element bounds = metadata.addElement("bounds");
        bounds.addAttribute("maxlat", "50");
        bounds.addAttribute("maxlon", "130");
        bounds.addAttribute("minlat", "20");
        bounds.addAttribute("minlon", "70");

        // <trk>
        Element trk = gpx.addElement("trk");
        trk.addElement("name").setText("2011-09-13成南高速到遂宁");
        Element extensions = trk.addElement("extensions");
        extensions.addNamespace("gpxx", "http://www.garmin.com/xmlschemas/GpxExtensions/v3");
        Element element = extensions.addElement("gpxx:TrackExtension");
        element.addElement("gpxx:DisplayColor").setText("DarkGreen");

        //<trkpt lat="30.75311" lon="104.17252">
        //<ele>495</ele>
        //<time>2011-09-13T14:11:25Z</time>
        //</trkpt>

        List<PointItem> list = readXml(srcFile);
        for (PointItem p : list) {
            Element trkpt = gpx.addElement("trkpt");
            trkpt.addAttribute("lat", p.lng);
            trkpt.addAttribute("lon", p.lat);
            trkpt.addElement("ele").setText(p.height);
            trkpt.addElement("time").setText(p.time.replaceFirst(" ", "T").concat("Z"));
        }


        write2File(document, desFile);
    }

}
