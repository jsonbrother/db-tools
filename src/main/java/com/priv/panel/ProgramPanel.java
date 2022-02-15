package com.priv.panel;

import com.priv.listener.ChoiceButtonListener;
import com.priv.listener.ConnectButtonListener;
import com.priv.listener.ExportButtonListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

/**
 * @author Json
 * @date 2021/6/16 17:36
 */
public class ProgramPanel extends JPanel {

    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;

    public ProgramPanel() {
        createPanel();
    }

    private JList list1 = null;
    private JList list2 = null;
    private DefaultListModel mode1 = null;
    private DefaultListModel mode2 = null;

    /**
     * 创建程序面板
     */
    private void createPanel() {
        // 设置边框
        this.setBorder(new EmptyBorder(5, 15, 5, 15));

        // 创建框架
        JFrame programFrame = createFrame();

        // 第一二行 驱动、url、用户、密码输入框
        JLabel driver = new JLabel("Driver :");
        JLabel url = new JLabel("Url :");
        JLabel userName = new JLabel("UserName :");
        JLabel passWord = new JLabel("PassWord :");
        final JTextField driverInput = new JTextField(25);
        final JTextField urlInput = new JTextField(25);
        final JTextField userNameInput = new JTextField(25);
        final JPasswordField passWordInput = new JPasswordField(25);

        // 第三行 连接、重置按钮
        JButton dbConnect = new JButton("Confirm");
        JButton dbReset = new JButton(" Reset ");
        dbConnect.setFocusPainted(false);
        dbReset.setEnabled(false);
        dbReset.setFocusPainted(false);

        // 占位组件
        JButton seat1 = new JButton("占位1");
        JButton seat2 = new JButton("占位2");
        JButton seat3 = new JButton("占位3");
        JButton seat4 = new JButton("占位4");
        seat1.setVisible(false);
        seat2.setVisible(false);
        seat3.setVisible(false);
        seat4.setVisible(false);

        // 第四行 列表JList组件
        mode1 = new DefaultListModel();
        list1 = new JList(mode1);
        list1.setBorder(BorderFactory.createTitledBorder("All current tables :"));
        // 添加鼠标事件
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseDoubleClick(e);
            }
        });

        // 列表整体高度
        list1.setVisibleRowCount(24);
        // 列表cell宽
        list1.setFixedCellWidth(350);
        // 可间段多选 后期做
        // list1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        mode2 = new DefaultListModel();
        list2 = new JList(mode2);
        list2.setBorder(BorderFactory.createTitledBorder("Table you need to export :"));
        // 添加鼠标事件
        list2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseDoubleClick(e);
            }
        });

        list2.setVisibleRowCount(24);
        list2.setFixedCellWidth(350);

        // 第五行
        JLabel directory = new JLabel("Directory:");
        final JTextField directoryInput = new JTextField(20);
        JButton choice = new JButton("Choice");
        JButton export = new JButton("Confirm export");
        export.setEnabled(false);
        directoryInput.setEditable(false);

        // 各个控件 开始布局
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.weightx = 6;
        gridBagConstraints.weighty = 5;

        // 使用网格组布局添加控件
        // 第一行
        add(driver, gridBagConstraints, 0, 0, 1, 1);
        add(url, gridBagConstraints, 3, 0, 1, 1);
        add(driverInput, gridBagConstraints, 1, 0, 2, 1);
        add(urlInput, gridBagConstraints, 4, 0, 2, 1);

        // 第二行
        add(userName, gridBagConstraints, 0, 1, 1, 1);
        add(passWord, gridBagConstraints, 3, 1, 1, 1);
        add(userNameInput, gridBagConstraints, 1, 1, 2, 1);
        add(passWordInput, gridBagConstraints, 4, 1, 2, 1);

        // 第三行
        add(seat1, gridBagConstraints, 0, 2, 1, 1);
        add(seat2, gridBagConstraints, 1, 2, 1, 1);
        add(seat3, gridBagConstraints, 2, 2, 1, 1);
        add(seat4, gridBagConstraints, 3, 2, 1, 1);
        add(dbConnect, gridBagConstraints, 4, 2, 1, 1);
        add(dbReset, gridBagConstraints, 5, 2, 1, 1);

        // 第四行
        add(new JScrollPane(list1), gridBagConstraints, 0, 3, 3, 1);
        add(new JScrollPane(list2), gridBagConstraints, 3, 3, 3, 1);

        // 第五行
        gridBagConstraints.ipadx = 0;
        gridBagConstraints.ipady = 0;
        add(directory, gridBagConstraints, 0, 4, 1, 1);
        add(directoryInput, gridBagConstraints, 1, 4, 1, 1);
        add(choice, gridBagConstraints, 2, 4, 1, 1);
        add(export, gridBagConstraints, 4, 4, 1, 1);

        // 不可手动拉伸窗口
        programFrame.setResizable(false);

        programFrame.getContentPane().add(this);

        // 设置其顶层容器的关闭性
        programFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // (a) 构造器初始化 按钮绑定
        ConnectButtonListener a = new ConnectButtonListener(driverInput, urlInput, userNameInput, passWordInput, mode1,
                mode2, dbConnect, dbReset, export, this);
        dbConnect.addActionListener(a);
        dbReset.addActionListener(a);

        // (b)
        ChoiceButtonListener b = new ChoiceButtonListener(programFrame, directoryInput);
        choice.addActionListener(b);

        // (c)
        ExportButtonListener c = new ExportButtonListener(driverInput, urlInput, userNameInput, passWordInput,
                directoryInput, mode2);
        export.addActionListener(c);

        // 初始化文本框的对象
        programFrame.setVisible(true);

        programFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private JFrame createFrame() {
        // 设置顶层容器
        JFrame programFrame = new JFrame("dbGui");
        URL iconPath = getClass().getResource("/img/favicon.png");
        programFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconPath));
        programFrame.setLocationRelativeTo(null);

        // 创建网格组布局方式对象
        GridBagLayout lay = new GridBagLayout();
        setLayout(lay);
        programFrame.getContentPane().add(this, BorderLayout.WEST);
        programFrame.setSize(PANEL_WIDTH, PANEL_HEIGHT);

        // 设置顶层容器框架为居中
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = screenSize.width;
        int height = screenSize.height;
        int x = (width - PANEL_WIDTH) / 2;
        int y = (height - PANEL_HEIGHT) / 2;
        this.setLocation(x, y);

        return programFrame;
    }

    @Override
    public void paint(Graphics g) {
        if (mode1.getSize() != 0) {
            list1.setVisibleRowCount(18);
            list1.setFixedCellWidth(350);
        } else {
            list1.setVisibleRowCount(24);
            list1.setFixedCellWidth(350);
        }
        if (mode2.getSize() != 0) {
            list2.setVisibleRowCount(18);
            list2.setFixedCellWidth(350);
        } else {
            list2.setVisibleRowCount(24);
            list2.setFixedCellWidth(350);
        }
        super.paint(g);
    }

    /**
     * 处理双击事件
     */
    private void mouseDoubleClick(MouseEvent e) {
        // 序号
        int index = 0;
        // 双击时
        if (e.getClickCount() == 2) {
            // 当双击左边列表框中选项时，会在左边将此项去掉，在右边列表框中将此项添加
            if (e.getSource() == list1) {
                index = list1.locationToIndex(e.getPoint());
                mode1 = (DefaultListModel) list1.getModel();
                if (mode1 == null || mode1.getSize() == 0) {
                    return;
                }
                ;
                String tmp = (String) mode1.getElementAt(index);
                mode2.addElement(tmp);
                mode1.removeElementAt(index);
            }
            // 当双击右边列表框中选项时，会在左边将此项去掉，在左边列表框中将此项添加
            if (e.getSource() == list2) {
                index = list2.locationToIndex(e.getPoint());
                mode2 = (DefaultListModel) list2.getModel();
                if (mode2 == null || mode2.getSize() == 0) {
                    return;
                }
                ;
                String tmp = (String) mode2.getElementAt(index);
                mode1.addElement(tmp);
                mode2.removeElementAt(index);
            }

            this.repaint();
        }
    }

    /**
     * 容器添加控件
     */
    private void add(Component c, GridBagConstraints constraints, int x, int y, int w, int h) {
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.gridwidth = w;
        constraints.gridheight = h;
        // 设置按钮边框和标签之间的空白 上 左 下 右
        constraints.insets = new Insets(5, 5, 5, 5);
        add(c, constraints);
    }

}
