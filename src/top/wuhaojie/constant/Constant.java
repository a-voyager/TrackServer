package top.wuhaojie.constant;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/22 21:17
 * Version: 1.0
 */
public interface Constant {
        String FILE_PATH = "/home/data/";
//    String FILE_PATH = "D://test/";

    String ATTR_FILE_NAME = "FileName";
    String ATTR_UPLOADING = "isUploading";
    String ATTR_FALSE = "false";
    String ATTR_TRUE = "true";

        String CONFIG_FILE_PATH = "/home/track_config.properties";
//    String CONFIG_FILE_PATH = "D://track_config.properties";
    String CONFIG_FINISHED = "finished";
    String CONFIG_FINISHED_FILE_PATH_WITH_OUT_POSTFIX = "fileName";

    /**
     * kml file type
     */
    String KML = "kml";
    /**
     * xml file type
     */
    String XML = "xml";
    /**
     * gpx file type
     */
    String GPX = "gpx";

    /**
     * header of file type
     */
    String FILE_TYPE_HEADER = "fileType";

}
