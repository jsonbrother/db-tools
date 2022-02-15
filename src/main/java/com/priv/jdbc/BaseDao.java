package com.priv.jdbc;

import com.lowagie.text.*;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfParagraphStyle;
import com.priv.utils.JasonUtil;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BaseDao类
 * 提供连接数据、调用数据、生成word说明文档
 *
 * @author Json
 * @date 2021/6/16 21:19
 */
public class BaseDao {

    /**
     * 键类型字典
     */
    private static Map<String, String> keyType = new HashMap<String, String>();

    /**
     * 数据库配置信息
     */
    private static String driver = "";
    private String url;
    private String username;
    private String password;
    private static String schema = "";
    private static String SQL_GET_ALL_TABLES = "";
    private static String SQL_GET_ALL_COLUMNS = "";

    private static final String ORACLE = "oracle";
    private static final String MYSQL = "mysql";

    /**
     * 构造方法
     */
    public BaseDao(String dbDriver, String url, String username, String password) {
        BaseDao.driver = dbDriver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * 查出所有表（表名 表头注释）
     */
    public List<Object> selectTableAll() throws Exception {
        // 查询开始
        Connection conn = getConnection(driver, url, username, password);

        sqlDistinguish(url);

        // 获取所有表
        assert conn != null;
        List<Object> tables = getDataBySql(SQL_GET_ALL_TABLES, conn);

        conn.close();

        return tables;
    }

    /**
     * 获取字段信息 生成文档
     */
    public void getWord(DefaultListModel model, String fileDirectory) throws Exception {
        // 初始化word文档
        Document document = new Document(PageSize.A4);
        RtfWriter2.getInstance(document, new FileOutputStream(fileDirectory + "\\数据库文档.doc"));
        document.open();

        // 不同数据库 主键区分
        keyDistinguish(url);

        // 查询开始
        Connection conn = getConnection(driver, url, username, password);

        for (int i = 0; i < model.size(); i++) {
            String tableName = model.getElementAt(i).toString();
            // 循环获取字段信息
            assert conn != null;
            List<Object> columns = getDataBySql(SQL_GET_ALL_COLUMNS.replace("{table_name}", tableName), conn);
            String[] arr = (String[]) columns.iterator().next();
            addTableMetaData(document, tableName, arr[6]);
            addTableDetail(document, columns);
            addBlank(document);
        }

        document.close();
        assert conn != null;
        conn.close();
    }

    /**
     * 增加表概要信息
     */
    private static void addTableMetaData(Document document, String tableName, String tableNotes) throws Exception {
        Paragraph ph = new Paragraph((tableNotes == null ? "" : tableNotes) + " " + tableName,
                RtfParagraphStyle.STYLE_HEADING_3);
        document.add(ph);
    }

    /**
     * 添加包含字段详细信息的表格
     */
    private static void addTableDetail(Document document, List columns) throws Exception {
        // 为了解决在插入段落与表之间产生多余的空格
        document.add(new com.lowagie.text.Phrase(""));
        // 列数必须设置，而行数则可以按照个人要求来决定是否需要设置
        Table table = new Table(5);
        // 宽度
        table.setWidth(100f);
        // 边框颜色
        table.setBorderColor(new Color(0, 125, 255));
        // 边框宽度
        table.setBorderWidth(1);
        // 单元格颜色
        table.setBorderColor(Color.BLACK);
        // 即单元格之间的间距
        table.setPadding(0);
        // 衬距
        table.setSpacing(0);
        // 边框
        table.setBorder(20);
        // 去掉段落与表直接的空行
        table.setOffset(1f);

        // 单元格
        Cell cell1 = new Cell("列名");
        cell1.setHeader(true);
        Cell cell2 = new Cell("类型");
        cell2.setHeader(true);
        Cell cell3 = new Cell("长度");
        cell3.setHeader(true);
        Cell cell4 = new Cell("可空");
        cell4.setHeader(true);
        Cell cell5 = new Cell("说明");
        cell5.setHeader(true);
        // 设置表头格式
        table.setWidths(new float[]{23f, 15f, 10f, 10f, 34f});
        cell1.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell1.setBackgroundColor(Color.gray);
        cell2.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell2.setBackgroundColor(Color.gray);
        cell3.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell3.setBackgroundColor(Color.gray);
        cell4.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell4.setBackgroundColor(Color.gray);
        cell5.setHorizontalAlignment(Cell.ALIGN_CENTER);
        cell5.setBackgroundColor(Color.gray);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
        table.addCell(cell5);
        table.endHeaders();// 表头结束
        for (Object column : columns) {
            String[] arr2 = (String[]) column;
            Cell c1 = new Cell(arr2[0]);
            Cell c2 = new Cell(arr2[1]);
            Cell c3 = new Cell(arr2[2]);

            String key = keyType.get(arr2[5]);
            if (key == null) {
                key = arr2[4];
            }
            Cell c4 = new Cell(key);
            Cell c5 = new Cell(arr2[3]);
            c1.setHorizontalAlignment(Cell.ALIGN_CENTER);
            c2.setHorizontalAlignment(Cell.ALIGN_CENTER);
            c3.setHorizontalAlignment(Cell.ALIGN_CENTER);
            c4.setHorizontalAlignment(Cell.ALIGN_CENTER);
            c5.setHorizontalAlignment(Cell.ALIGN_CENTER);
            table.addCell(c1);
            table.addCell(c2);
            table.addCell(c3);
            table.addCell(c4);
            table.addCell(c5);
        }
        document.add(table);
    }

    /**
     * 添加一个空行
     */
    private static void addBlank(Document document) throws Exception {
        Paragraph ph = new Paragraph("", RtfParagraphStyle.STYLE_NORMAL);
        ph.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(ph);
    }

    /**
     * 把SQL语句查询出列表
     */
    private List<Object> getDataBySql(String sql, Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;
        List<Object> list = new ArrayList<Object>();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String[] arr = new String[rs.getMetaData().getColumnCount()];
                for (int i = 0; i < arr.length; i++) {
                    arr[i] = rs.getString(i + 1);
                }
                list.add(arr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * 根据数据库的URL 不同SQL
     */
    private static void sqlDistinguish(String url) {
        // 主键区分
        if (JasonUtil.contains(url, ORACLE)) {
            SQL_GET_ALL_TABLES = "SELECT A.TABLE_NAME,B.COMMENTS FROM USER_TABLES A,USER_TAB_COMMENTS B WHERE A.TABLE_NAME=B.TABLE_NAME ORDER BY TABLE_NAME";
        }
        if (JasonUtil.contains(url, MYSQL)) {
            schema = getSchema(url);
            SQL_GET_ALL_TABLES = "select table_name,TABLE_COMMENT from INFORMATION_SCHEMA.tables where TABLE_SCHEMA='" + schema + "' and TABLE_TYPE='BASE TABLE'";
        }
    }

    /**
     * 根据数据库的URL 不同SQL 不同数据库的主键区分
     */
    private static void keyDistinguish(String url) {
        // 主键区分
        if (JasonUtil.contains(url, ORACLE)) {
            SQL_GET_ALL_COLUMNS = "SELECT T1.COLUMN_NAME,T1.DATA_TYPE,T1.DATA_LENGTH,T2.COMMENTS,T1.NULLABLE,(SELECT MAX(CONSTRAINT_TYPE)    FROM USER_CONSTRAINTS X LEFT JOIN USER_CONS_COLUMNS Y ON X.CONSTRAINT_NAME=Y.CONSTRAINT_NAME WHERE X.TABLE_NAME=T1.TABLE_NAME AND Y.COLUMN_NAME=T1.COLUMN_NAME), T3.COMMENTS  FROM USER_TAB_COLS T1, USER_COL_COMMENTS T2, USER_TAB_COMMENTS T3  WHERE T1.TABLE_NAME=T2.TABLE_NAME(+)  AND T1.COLUMN_NAME=T2.COLUMN_NAME(+)  AND T1.TABLE_NAME=T3.TABLE_NAME(+)  AND T1.TABLE_NAME='{table_name}' ORDER BY T1.COLUMN_ID";
            keyType.put("P", "主键");
        }
        if (JasonUtil.contains(url, MYSQL)) {
            schema = getSchema(url);
            SQL_GET_ALL_COLUMNS = "select a.column_name,a.data_type,substring_index(substring_index(a.COLUMN_TYPE,'(',-1),')',1),a.COLUMN_COMMENT,a.is_nullable,a.COLUMN_key,b.TABLE_COMMENT  from information_schema.`COLUMNS` a,information_schema.`TABLES` b where a.TABLE_NAME = b.TABLE_NAME and a.TABLE_NAME='{table_name}' and a.TABLE_SCHEMA='" + schema + "'";
            keyType.put("PRI", "主键");
            keyType.put("UNI", "唯一键");
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection(String driver, String url, String username, String password) {
        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final String URL_SIGN = "?";

    private static String getSchema(String url) {
        if (url.contains(URL_SIGN)) {
            return url.substring(url.lastIndexOf("/") + 1, url.indexOf("?"));
        } else {
            return url.substring(url.lastIndexOf("/") + 1);
        }
    }
}
