package com.priv.listener;

import com.priv.jdbc.BaseDao;
import com.priv.utils.JasonUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 生成并导出数据库说明文档（word）
 *
 * @author Json
 * @date 2021/6/16 21:39
 */
public class ExportButtonListener implements ActionListener {

    /**
     * 数据库连接配置
     */
    private JTextField driver;
    private JTextField url;
    private JTextField username;
    private JTextField password;
    JTextField schema = null;
    /**
     * 文件目录
     */
    private JTextField directoryInput;
    /**
     * 选择的表
     */
    private DefaultListModel mode;

    /**
     * 构造方法
     */
    public ExportButtonListener(JTextField driver, JTextField url, JTextField username, JTextField password,
                                JTextField directoryInput, DefaultListModel mode2) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.directoryInput = directoryInput;
        this.mode = mode2;
    }

    public void actionPerformed(ActionEvent e) {
        // 1.1 判断是否为空
        if (mode.size() == 0 || mode == null) {
            JOptionPane.showMessageDialog(null, "Table not selected for export !", "Tips", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 1.2 判断 文件目录是否为空
        if (JasonUtil.isBlank(directoryInput.getText())) {
            JOptionPane.showMessageDialog(null, "Select the directory where the file is exported !", "Tips", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // 1.3 初始化连接信息
            BaseDao baseDao = new BaseDao(driver.getText(), url.getText(), username.getText(), password.getText());
            // 1.4 导出文档
            baseDao.getWord(mode, directoryInput.getText());
            // 1.5 对话框
            JOptionPane.showMessageDialog(null, "Export success !");
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Export failure !", "Tips", JOptionPane.ERROR_MESSAGE);
        }

    }
}
