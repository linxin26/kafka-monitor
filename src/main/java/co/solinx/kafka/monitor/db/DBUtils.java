package co.solinx.kafka.monitor.db;

import co.solinx.kafka.monitor.common.DateUtils;
import co.solinx.kafka.monitor.model.KafkaMonitorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

/**
 * @author linxin
 * @version v1.0
 *          Copyright (c) 2015 by solinx
 * @date 2016/4/12.
 */
public class DBUtils<T> {

    private static Logger logger = LoggerFactory.getLogger(DBUtils.class);
    private Connection conn;
    Class actualClass;

    public DBUtils(Class cla) {
        try {

            logger.debug("{}", cla);
            this.actualClass = cla;
            this.open();
        } catch (SQLException e) {
            logger.error("open connection error {}", e);
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException error {}", e);

        }
    }

    /**
     * 打开连接
     *
     * @throws SQLException
     */
    private void open() throws SQLException, ClassNotFoundException {
//        URL path = DBUtils.class.getClassLoader().getResource("db");
        String url = "jdbc:mysql://10.10.1.104:3306/kafkaMonitor";    //JDBC的URL
        String path = System.getProperty("user.dir").toString();
        Class.forName("com.mysql.jdbc.Driver");
        logger.debug("path : {} ", path);
        conn = DriverManager.getConnection(url, "root", "123456");

    }

    /**
     * 更新操作
     *
     * @param sql
     * @throws SQLException
     */
    public void executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        checkConnect();
        //开启WAL模式提高并发  减少SQLITE_BUSY问题的出现概率
        //updateJournalModel("PRAGMA journal_mode=WAL");
        Statement stmt = conn.createStatement();
        PreparedStatement pst = conn.prepareStatement(sql);


        logger.debug(" sql : {}", sql);
        stmt.executeUpdate(sql);
        stmt.close();
        this.close();

    }

    /**
     * 更新JournalModel模式
     *
     * @param model
     * @throws SQLException
     */
    public void updateJournalModel(String model) throws SQLException, ClassNotFoundException {
        Statement stmt = null;
        try {
            this.checkConnect();
            stmt = conn.createStatement();
            stmt.executeUpdate(model);
        } finally {
            stmt.close();
        }
    }

    /**
     * 查询
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public List<T> query(String sql) throws Exception {
        List<T> resultList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();

        this.checkConnect();
        //开启WAL模式提高并发  减少SQLITE_BUSY问题的出现概率
        // updateJournalModel("PRAGMA journal_mode=WAL");
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(sql);
        logger.debug(" sql : {}", sql);

        //获取表中的各列名称
        if (set != null) {
            ResultSetMetaData metaData = set.getMetaData();
            int count = metaData.getColumnCount();
            logger.debug(" columnCount: {}", count);
            for (int i = 1; i <= count; i++) {
                columnList.add(metaData.getColumnName(i));
            }
        }


        while (set.next()) {
            Object model = Class.forName(actualClass.getName()).newInstance();
            for (String column :
                    columnList) {

                Field field = model.getClass().getDeclaredField(column);
                field.setAccessible(true);
                String type = field.getType().toString();
                if (type.startsWith("long") || type.startsWith("int")) {
                    field.setLong(model, set.getLong(column));
                } else if (type.endsWith("String")) {
                    field.set(model, set.getString(column));
                } else if (type.endsWith("Date")) {
                    field.set(model, DateUtils.convertFromStringToDate(set.getString(column), "yyyy-MM-dd HH:mm:ss"));
                } else if (type.endsWith("double")) {
                    field.set(model, set.getDouble(column));
                } else {
                    field.set(model, set.getObject(column).toString());
                }
//                logger.debug("{}",model);
            }
            resultList.add((T) model);
        }
        conn.close();
//        logger.debug("{}",resultList);
        return resultList;
    }


    public static Class<Object> getSuperClassGenricType(Class clazz, int index) {

        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];

    }

    /**
     * 检查连接
     *
     * @throws SQLException
     */
    public void checkConnect() throws SQLException, ClassNotFoundException {
        if (conn.isClosed()) {
            this.open();
        }
    }

    public List<Map> executeQuery(String sql) throws SQLException, ClassNotFoundException {


        checkConnect();
        //开启WAL模式提高并发  减少SQLITE_BUSY问题的出现概率
        updateJournalModel("PRAGMA journal_mode=WAL");
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(sql);
        List<Map> mapList = new ArrayList<>();
        List<String> columnList = new ArrayList<>();

        //获取表中的各列名称
        if (set != null) {
            ResultSetMetaData metaData = set.getMetaData();
            int count = metaData.getColumnCount();
            logger.debug(" columnCount: {}", count);
            for (int i = 1; i <= count; i++) {
                columnList.add(metaData.getColumnName(i));
            }
        }

        logger.debug(" columnList : {}", columnList);

        while (set.next()) {
            for (String column :
                    columnList) {
                Map map = new HashMap<>();
                map.put(column, set.getObject(column));
                mapList.add(map);
            }
        }

        set.close();
        stmt.close();
        this.close();
        logger.debug("{}", mapList);
        return mapList;
    }

    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return
     */
    public boolean tableIsExit(String tableName) {
        boolean result = false;
        String sql = "select * from sqlite_master where type='table' and name='" + tableName + "'";

        try {
            logger.debug("sql : {}", sql);

            List<Map> mapList = executeQuery(sql);
            if (mapList.size() > 0) {
                result = true;
            }

        } catch (SQLException e) {
            logger.error("tableIsExit error {}", e);
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException error {}", e);

        }
        return result;
    }

    private void close() throws SQLException {
        if (!conn.isClosed()) {
            conn.close();
        }
    }


    public static void main(String[] args) {

        DBUtils<KafkaMonitorData> dbUtils = new DBUtils<>(KafkaMonitorData.class);
        // boolean result = dbUtils.tableIsExit("logdata");

        try {
            // dbUtils.executeUpdate("PRAGMA journal_mode=WAL");

            String startTime = DateUtils.toDisplayStr("2016-12-30 10:00:00", DateUtils.HYPHEN_DISPLAY_DATE);
            String endTime = DateUtils.toDisplayStr("2016-12-30 10:50:00", DateUtils.HYPHEN_DISPLAY_DATE);

            String where = " where currentTime >= '" + startTime + "' and currentTime <='" + endTime + "'";

            List<KafkaMonitorData> dataList = dbUtils.query("select * from kafkaMonitorData " + where);

            logger.debug("{}", dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println(result);
    }

}
