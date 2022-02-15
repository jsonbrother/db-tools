package com.priv.listener;

import com.priv.jdbc.BaseDao;
import com.priv.utils.JasonUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 数据连接"确定"按钮监听
 * @author Json
 * @date 2021/6/16 21:16
 */
public class ConnectButtonListener implements ActionListener {

    private JTextField driver;
    private JTextField url;
    private JTextField username;
    private JTextField password;
    private DefaultListModel mode1;
    private DefaultListModel mode2;
    private JButton connect;
    private JButton reset;
    private JButton export;
    private JPanel jPanel;

    /**
     * 构造方法
     */
    public ConnectButtonListener(JTextField driver, JTextField url, JTextField username, JTextField password,
                                 DefaultListModel mode1, DefaultListModel mode2, JButton dbConnect, JButton dbReset,
                                 JButton export, JPanel jPanel) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.mode1 = mode1;
        this.mode2 = mode2;
        this.connect = dbConnect;
        this.reset = dbReset;
        this.export = export;
        this.jPanel = jPanel;
    }

    public void actionPerformed(ActionEvent e) {

        // 确定
        if (e.getSource().equals(connect)) {

            // 1.1数据连接配置 判断 不可为空
            if (JasonUtil.isBlank(driver.getText()) || JasonUtil.isBlank(url.getText())
                    || JasonUtil.isBlank(username.getText()) || JasonUtil.isBlank(password.getText())) {
                JOptionPane.showMessageDialog(null, "Database connection configuration information cannot be empty !", "Tips", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BaseDao baseDao = new BaseDao(driver.getText(), url.getText(), username.getText(), password.getText());
            // 1.2开始查询
            try {
                List<Object> tables = baseDao.selectTableAll();
                // 1.3 循环 把查询结果 赋给列表对象list1
                for (Object table : tables) {
                    String[] arr = (String[]) table;
                    mode1.addElement(arr[0]);
                }
                // 1.4 文本框 不可编辑 按钮状态调整
                setTextFieldFalse();
                // 1.5 对话框弹出结果
                JOptionPane.showMessageDialog(null, "Successful database connection !");
                jPanel.repaint();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failure of database connection !", "Tips", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 重置
        if (e.getSource().equals(reset)) {
            setTextFieldTrue();
            jPanel.repaint();
        }
    }

    /**
     * 确定 文本框限制 按钮可用
     */
    private void setTextFieldFalse() {

        // 文本框 不可编辑
        driver.setEditable(false);
        url.setEditable(false);
        username.setEditable(false);
        password.setEditable(false);

        // 按钮状态调整
        connect.setEnabled(false);
        reset.setEnabled(true);
        export.setEnabled(true);

    }

    /**
     * 重置初始化 文本框可用 按钮限制
     */
    private void setTextFieldTrue() {
        // 文本框清空
        driver.setText("");
        url.setText("");
        username.setText("");
        password.setText("");

        // 文本框 可编辑
        driver.setEditable(true);
        url.setEditable(true);
        username.setEditable(true);
        password.setEditable(true);

        // 列表清空
        mode1.removeAllElements();
        mode2.removeAllElements();

        // 按钮状态调整
        connect.setEnabled(true);
        reset.setEnabled(false);
        export.setEnabled(false);
    }
}
