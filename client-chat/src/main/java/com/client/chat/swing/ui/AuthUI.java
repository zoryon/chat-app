package com.client.chat.swing.ui;

import javax.swing.*;
import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.ui.CustomFrame.CustomFrame;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class AuthUI extends CustomFrame {
    // constructors
    public AuthUI() throws IOException {
        super(false);
        initComponents();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        JLabel jLabel5 = new JLabel();
        JPanel jPanel1 = new JPanel();
        JPanel jPanel3 = new JPanel();
        JLabel jLabel1 = new JLabel();
        JPanel jPanel5 = new JPanel();
        JPanel jPanel7 = new JPanel();
        jTextField2 = new JTextField();
        jTextField2.setBackground(CustomColors.SOFT_BACKGROUND.getColor());
        jTextField2.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JLabel jLabel3 = new JLabel();
        JPanel jPanel6 = new JPanel();
        JLabel jLabel2 = new JLabel();
        jPasswordField1 = new JPasswordField();
        jPasswordField1.setBackground(CustomColors.SOFT_BACKGROUND.getColor());
        jPasswordField1.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JPanel jPanel8 = new JPanel();
        jButton2 = new JButton();
        jButton3 = new JButton();
        JPanel jPanel2 = new JPanel();
        JLabel jLabel7 = new JLabel();

        jLabel5.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/login_icon.png"))));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(CustomColors.BACKGROUND.getColor());
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 500));

        jPanel3.setPreferredSize(new java.awt.Dimension(445, 160));
        jPanel3.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Segue UI", Font.BOLD, 18));
        jLabel1.setText("Login Required");
        jLabel1.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(152, 152, 152)
                                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(154, Short.MAX_VALUE)));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(80, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addGap(35, 35, 35)));

        jPanel1.add(jPanel3);

        jPanel5.setPreferredSize(new Dimension(400, 150));
        jPanel5.setOpaque(false);
        jPanel5.setForeground(CustomColors.MAIN_FOREGROUND.getColor());

        jPanel7.setPreferredSize(new Dimension(300, 50));
        jPanel7.setLayout(new BorderLayout(4, 4));
        jPanel7.add(jTextField2, BorderLayout.CENTER);

        jLabel3.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        jLabel3.setText("Username");
        jPanel7.add(jLabel3, BorderLayout.PAGE_START);
        jPanel7.setBackground(CustomColors.BACKGROUND.getColor());

        jPanel5.add(jPanel7);

        jPanel6.setPreferredSize(new Dimension(300, 50));
        jPanel6.setLayout(new BorderLayout(4, 4));

        jLabel2.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        jLabel2.setText("Password");
        jPanel6.add(jLabel2, BorderLayout.PAGE_START);
        jPanel6.setBackground(CustomColors.BACKGROUND.getColor());

        jPasswordField1.setPreferredSize(new Dimension(90, 200));
        jPanel6.add(jPasswordField1, BorderLayout.CENTER);

        jPanel5.add(jPanel6);

        jPanel1.add(jPanel5);

        jPanel8.setPreferredSize(new Dimension(400, 50));
        jPanel8.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));

        jButton2.setBorderPainted(false);
        jButton2.setBackground(new Color(204, 204, 204));
        jButton2.setText("Sign Up");
        jButton2.setPreferredSize(new Dimension(160, 30));
        jPanel8.add(jButton2);

        jButton3.setBorderPainted(false);
        jButton3.setBackground(new Color(204, 204, 204));
        jButton3.setText("Sign In");
        jButton3.setPreferredSize(new Dimension(160, 30));
        jPanel8.add(jButton3);

        jPanel8.setBackground(CustomColors.BACKGROUND.getColor());
        jPanel1.add(jPanel8);

        getContentPane().add(jPanel1, BorderLayout.WEST);

        jPanel2.setBackground(CustomColors.SOFT_BACKGROUND.getColor());
        jPanel2.setMaximumSize(new Dimension(0, 0));
        jPanel2.setPreferredSize(new Dimension(600, 430));
        jPanel2.setLayout(new GridBagLayout());

        jLabel7.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/icons/login_icon.png"))));
        jPanel2.add(jLabel7);

        getContentPane().add(jPanel2, BorderLayout.CENTER);

        pack();
    }

    private JButton jButton2;
    private JButton jButton3;
    private JPasswordField jPasswordField1;
    private JTextField jTextField2;

    // getters and setters
    public String getUsername() {
        return jTextField2.getText();
    }

    public String getPassword() {
        return new String(jPasswordField1.getPassword());
    }

    // listeners
    public void addSignUpListener(java.awt.event.ActionListener listener) {
        jButton2.addActionListener(listener);
    }

    public void addSignInListener(java.awt.event.ActionListener listener) {
        jButton3.addActionListener(listener);
    }
}
