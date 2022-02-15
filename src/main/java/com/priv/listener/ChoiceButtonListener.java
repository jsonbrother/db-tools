package com.priv.listener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 选择文件导出的目录地址
 *
 * @author Json
 * @date 2021/6/16 21:37
 */
public class ChoiceButtonListener implements ActionListener {

    private JFrame programFrame;
    private JTextField directoryInput;

    /**
     * 文件选择器
     */
    private JFileChooser jfc = new JFileChooser();

    public ChoiceButtonListener(JFrame programFrame, JTextField directoryInput) {
        this.programFrame = programFrame;
        this.directoryInput = directoryInput;
    }

    public void actionPerformed(ActionEvent e) {
        // 文件选择器的初始目录定为d盘
        jfc.setCurrentDirectory(new File("d://"));
        // 设定只能选择到文件夹
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // 设置对话标题 按钮名称
        jfc.setDialogTitle("Directory selection");
        jfc.setApproveButtonText("确定");
        // 此句是打开文件选择器界面的触发语句
        int state = jfc.showDialog(programFrame, null);
        if (state != 1) {
            // f为选择到的目录
            File f = jfc.getSelectedFile();
            directoryInput.setText(f.getAbsolutePath());
        }
    }

}
