package top.wuhaojie.utils;


import top.wuhaojie.constant.Constant;
import top.wuhaojie.entities.PointItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Author: wuhaojie
 * E-mail: w19961009@126.com
 * Date: 2016/4/20 7:30
 * Version: 1.0
 */
public class DataDao {

    private Connection connection;

    private DataDao() {
        Properties properties = new Properties(); // 读取配置文件
        try {
            properties.load(new FileInputStream(new File(Constant.CONFIG_FILE_PATH)));
            System.out.println(properties.getProperty("SQLDriver"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            Class.forName(properties.getProperty("SQLDriver"));
        } catch (ClassNotFoundException e) {
            System.out.println("MySql load failed.");
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(
                    properties.getProperty("SQLAddr"),
                    properties.getProperty("SQLUserName"),
                    properties.getProperty("SQLUserPwd"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private volatile static DataDao dao;

    public static DataDao getInstance() {
        if (dao == null) {
            synchronized (DataDao.class) {
                if (dao == null) {
                    dao = new DataDao();
                }
            }
        }
        return dao;
    }


    private void closeStatement(Statement statement) {
        if (statement != null)
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                statement = null;
            }
    }

    /**
     * 释放资源
     */
    private void dispose() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
            dao = null;
        }
    }

//    # 创建表
//    CREATE TABLE pointdb.POINTS
//            (
//    id INTEGER PRIMARY KEY AUTO_INCREMENT,
//    latitude VARCHAR(20),
//    longitude VARCHAR(20),
//    height VARCHAR(10),
//    currtime VARCHAR(30)
//    );
//    ALTER TABLE pointdb.POINTS COMMENT = 'store points';


//    INSERT INTO POINTS(id, latitude, longitude, height, currtime)
//    VALUES (0, '30.22', '104.55', '500', '2016-4-20 08:50');


    public void insertNewPointList(List<PointItem> itemList) throws SQLException {

        long lastTime = System.currentTimeMillis();

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO POINTS(id, latitude, longitude, height, currtime) " +
                "VALUES (0, ?, ?, ?, ?);");

        for (PointItem p : itemList) {

//            System.out.println(p.time);
            preparedStatement.setString(1, p.lat);
            preparedStatement.setString(2, p.lng);
            preparedStatement.setString(3, p.height);
            preparedStatement.setString(4, p.time);
            preparedStatement.execute();

        }
        closeStatement(preparedStatement);

        long deltaTime = System.currentTimeMillis() - lastTime;
        int size = itemList.size();
        System.out.println("deltaTime" + deltaTime);
        listeners.forEach((l) -> l.onEventCompleted(deltaTime, size));
    }


    public void deleteAllPoints() throws SQLException {
        long lastTime = System.currentTimeMillis();
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM POINTS;");
        closeStatement(statement);
        listeners.forEach((l) -> l.onEventCompleted(System.currentTimeMillis() - lastTime, 0));
    }

    public List<PointItem> queryAllPoints() throws SQLException {
        long lastTime = System.currentTimeMillis();
        List<PointItem> itemList = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM POINTS;");
        while (resultSet.next()) {
            String latitude = resultSet.getString("latitude");
            String longitude = resultSet.getString("longitude");
            String height = resultSet.getString("height");
            String currtime = resultSet.getString("currtime");
            itemList.add(new PointItem(latitude, longitude, height, currtime));
        }
        closeStatement(statement);
        listeners.forEach((l) -> l.onEventCompleted(System.currentTimeMillis() - lastTime, itemList.size()));
        return itemList;
    }


    public static void main(String[] args) {
        try {
//            List<PointItem> pointItems = Converter.getConverter().readXml(new File("data/test.xml"));
            DataDao.getInstance().setOnStatusChangedListener((d, s) -> System.out.println(d + "----" + s));
//            DataDao.getInstance().insertNewPointList(pointItems);

            DataDao.getInstance().deleteAllPoints();
//            List<PointItem> itemList = DataDao.getInstance().queryAllPoints();
//            int count = 0;
//            for (PointItem p : itemList) {
//                System.out.println(p.time);
//                count++;
//            }
//            System.out.println("------" + count);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnStatusChangedListener {
        void onEventCompleted(long deltaTime, long eventMount);
    }

    private List<OnStatusChangedListener> listeners = new ArrayList<>();

    public void setOnStatusChangedListener(OnStatusChangedListener listener) {
        listeners.add(listener);
    }

}
