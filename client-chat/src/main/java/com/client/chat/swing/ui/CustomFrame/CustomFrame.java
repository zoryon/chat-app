package com.client.chat.swing.ui.CustomFrame;

import javax.swing.*;

import com.client.chat.swing.protocol.CustomColors;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class CustomFrame extends JFrame {
    // attributes
    private WindowListener exitListener;
    private Rectangle maxBounds;

    // constructors
    public CustomFrame() {
        maxBounds = null;

        setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, CustomColors.WINDOW_BORDER.getColor()));
        setMinimumSize(new Dimension(10, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // add custom title bar
        CustomTitleBar titleBar = new CustomTitleBar(this, "");
        titleBar.setCloseAction(() -> {
            if (exitListener != null) {
                WindowEvent windowEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
                exitListener.windowClosing(windowEvent);
            } else {
                dispose();
            }
        });
        add(titleBar, BorderLayout.NORTH);

        // add content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // add window resizer
        new WindowResizer(this);
    }

    /**
     * 
     * @overload CustomFrame
     * with signature ()
     */
    public CustomFrame(boolean isResizable) {
        maxBounds = null;

        setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, CustomColors.WINDOW_BORDER.getColor()));
        setMinimumSize(new Dimension(10, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // add custom title bar
        CustomTitleBar titleBar = new CustomTitleBar(this, "", isResizable);
        titleBar.setCloseAction(() -> {
            if (exitListener != null) {
                WindowEvent windowEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
                exitListener.windowClosing(windowEvent);
            } else {
                dispose();
            }
        });
        add(titleBar, BorderLayout.NORTH);

        // add content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        if (isResizable) {
            // add window resizer if needed
            new WindowResizer(this);
        }
    }

    public void addWindowExitListener(WindowListener listener) {
        this.exitListener = listener;
    }

    @Override
    public Rectangle getMaximizedBounds() {
        return maxBounds;
    }

    @Override
    public synchronized void setMaximizedBounds(Rectangle maxBounds) {
        this.maxBounds = maxBounds;
        super.setMaximizedBounds(maxBounds);
    }

    @Override
    public synchronized void setExtendedState(int state) {
        if (maxBounds == null &&
                (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
            Insets screenInsets = getToolkit().getScreenInsets(getGraphicsConfiguration());
            Rectangle screenSize = getGraphicsConfiguration().getBounds();
            Rectangle maxBounds = new Rectangle(screenInsets.left + screenSize.x,
                    screenInsets.top + screenSize.y,
                    screenSize.x + screenSize.width - screenInsets.right - screenInsets.left,
                    screenSize.y + screenSize.height - screenInsets.bottom - screenInsets.top);

            super.setMaximizedBounds(maxBounds);
        }

        super.setExtendedState(state);
    }
}
