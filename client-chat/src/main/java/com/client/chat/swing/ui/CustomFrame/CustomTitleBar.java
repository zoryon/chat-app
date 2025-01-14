package com.client.chat.swing.ui.CustomFrame;

import javax.swing.*;
import com.client.chat.swing.protocol.CustomColors;
import java.awt.*;
import java.awt.event.*;

public class CustomTitleBar extends JPanel {
    // attributes
    private Point initialClick;
    private final JFrame parentFrame;
    private Runnable closeAction;

    // constructors
    public CustomTitleBar(JFrame frame, String titleText) {
        this.parentFrame = frame;
        this.closeAction = frame::dispose;
        setBackground(CustomColors.BORDER.getColor());
        setPreferredSize(new Dimension(frame.getWidth(), 30));
        setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(title, BorderLayout.WEST);

        // add window controls
        add(createWindowControls(), BorderLayout.EAST);

        // add dragging functionality
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // check if window is maximized
                if (parentFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    // restore to normal state
                    parentFrame.setExtendedState(JFrame.NORMAL);
                    
                    // calculate new initial position for the restored window
                    // we want the mouse to "grab" the window at a proportional position
                    double proportionalX = e.getX() / (double) getWidth();
                    int newWidth = parentFrame.getWidth();
                    int newX = e.getXOnScreen() - (int)(proportionalX * newWidth);
                    
                    // set the new location
                    parentFrame.setLocation(newX, 0);
                    
                    // update initial click to prevent jump
                    initialClick = new Point((int)(proportionalX * newWidth), e.getY());
                } else {
                    // normal dragging behavior
                    int thisX = parentFrame.getLocation().x;
                    int thisY = parentFrame.getLocation().y;

                    int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                    int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                    parentFrame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        });
    }

    /**
     * 
     * @overload CustomTitleBar
     * with signature (JFrame frame, String titleText)
     */
    public CustomTitleBar(JFrame frame, String titleText, boolean isResizable) {
        this.parentFrame = frame;
        this.closeAction = frame::dispose;
        setBackground(CustomColors.BORDER.getColor());
        setPreferredSize(new Dimension(frame.getWidth(), 30));
        setLayout(new BorderLayout());

        JLabel title = new JLabel(titleText);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        add(title, BorderLayout.WEST);

        // add window controls
        add(createWindowControls(isResizable), BorderLayout.EAST);

        // add dragging functionality
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // check if window is maximized
                if (parentFrame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    // restore to normal state
                    parentFrame.setExtendedState(JFrame.NORMAL);
                    
                    // calculate new initial position for the restored window
                    // we want the mouse to "grab" the window at a proportional position
                    double proportionalX = e.getX() / (double) getWidth();
                    int newWidth = parentFrame.getWidth();
                    int newX = e.getXOnScreen() - (int)(proportionalX * newWidth);
                    
                    // set the new location
                    parentFrame.setLocation(newX, 0);
                    
                    // update initial click to prevent jump
                    initialClick = new Point((int)(proportionalX * newWidth), e.getY());
                } else {
                    // normal dragging behavior
                    int thisX = parentFrame.getLocation().x;
                    int thisY = parentFrame.getLocation().y;

                    int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                    int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                    parentFrame.setLocation(thisX + xMoved, thisY + yMoved);
                }
            }
        });
    }

    public void setCloseAction(Runnable action) {
        this.closeAction = action;
    }

    @SuppressWarnings("unused")
    private JPanel createWindowControls() {
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowControls.setOpaque(false);

        windowControls.add(createWindowButton("−", e -> parentFrame.setState(JFrame.ICONIFIED)));

        windowControls.add(createWindowButton("□", e -> {
            if (parentFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            } else {
                parentFrame.setExtendedState(JFrame.NORMAL);
            }
        }));
        
        windowControls.add(createWindowButton("×", e -> closeAction.run()));

        return windowControls;
    }

    @SuppressWarnings("unused")
    private JPanel createWindowControls(boolean isResizable) {
        JPanel windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        windowControls.setOpaque(false);

        windowControls.add(createWindowButton("−", e -> parentFrame.setState(JFrame.ICONIFIED)));

        if (isResizable) {
            windowControls.add(createWindowButton("□", e -> {
                if (parentFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                } else {
                    parentFrame.setExtendedState(JFrame.NORMAL);
                }
            }));
        }
        
        windowControls.add(createWindowButton("×", e -> closeAction.run()));

        return windowControls;
    }

    private JButton createWindowButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(45, 30));
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(CustomColors.SOFT_BACKGROUND.getColor());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(CustomColors.SOFT_BACKGROUND.getColor());
            }
        });
        return button;
    }
}