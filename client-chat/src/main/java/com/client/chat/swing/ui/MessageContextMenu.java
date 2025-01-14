package com.client.chat.swing.ui;

import com.client.chat.swing.protocol.CustomColors;
import com.client.chat.swing.protocol.JsonMessage;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MessageContextMenu extends JPopupMenu {
    // attributes
    private JsonMessage message;
    private JPanel copyPanel;
    private JPanel editPanel;
    private JPanel removePanel;

    // constructors
    public MessageContextMenu(JsonMessage message) {
        this.message = message;

        setBackground(CustomColors.SECONDARY.getColor());
        setBorder(new LineBorder(CustomColors.BORDER.getColor(), 1));

        createContextMenuItems();
    }

    private void createContextMenuItems() {
        // copy message option
        copyPanel = createContextMenuItem("Copy");

        // edit message option
        editPanel = createContextMenuItem("Edit");

        // remove message option
        removePanel = createContextMenuItem("Remove");

        add(copyPanel);
        add(editPanel);
        add(removePanel);
    }

    private JPanel createContextMenuItem(String text) {
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(CustomColors.SECONDARY.getColor());

        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(CustomColors.MAIN_FOREGROUND.getColor());
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createEmptyBorder(7, 13, 7, 13));

        innerPanel.add(label, BorderLayout.CENTER);

        /*
         * add hover effect.
         * both on 
         * mouse entered &
         * mouse exited
         */
         innerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                innerPanel.setBackground(CustomColors.SECONDARY.getColor().brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                innerPanel.setBackground(CustomColors.SECONDARY.getColor());
            }
        });

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(CustomColors.SECONDARY.getColor());
        outerPanel.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3));
        outerPanel.add(innerPanel, BorderLayout.CENTER);
        return outerPanel;
    }

    public void setCopyMessageListener(MouseListener listener) {
        // add new listener to panel and its children
        copyPanel.addMouseListener(listener);
        for (Component comp : copyPanel.getComponents()) {
            comp.addMouseListener(listener);
        }
    }

    public void setEditMessageListener(MouseListener listener) {
        // add new listener to panel and its children
        editPanel.addMouseListener(listener);
        for (Component comp : editPanel.getComponents()) {
            comp.addMouseListener(listener);
        }
    }
    
    public void setRemoveMessageListener(MouseListener listener) {  
        // add new listener to panel and its children
        removePanel.addMouseListener(listener);
        for (Component comp : removePanel.getComponents()) {
            comp.addMouseListener(listener);
        }
    }

    // getters
    public JsonMessage getMessage() {
        return message;
    }
}