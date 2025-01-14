package com.client.chat.swing.ui.CustomFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WindowResizer {
    // attributes
    private final JFrame frame;
    private final int RESIZE_BORDER = 5;
    private boolean isResizing = false;
    private int resizeDirection = 0;

    // direction constants
    private static final int NONE = 0;
    private static final int NW = 1;
    private static final int N = 2;
    private static final int NE = 3;
    private static final int E = 4;
    private static final int SE = 5;
    private static final int S = 6;
    private static final int SW = 7;
    private static final int W = 8;
    
    private Point startPos;
    private Point startLocation;
    private Dimension startSize;

    // constructors
    public WindowResizer(JFrame frame) {
        this.frame = frame;
        setupResizeListeners();
    }

    // methods
    private void setupResizeListeners() {
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isInResizeZone(e.getPoint())) {
                    isResizing = true;
                    startPos = e.getLocationOnScreen();
                    startLocation = frame.getLocation();
                    startSize = frame.getSize();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isResizing = false;
                resizeDirection = NONE;
                frame.setCursor(Cursor.getDefaultCursor());
            }
        });

        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (frame.getExtendedState() != Frame.MAXIMIZED_BOTH) {
                    updateResizeCursor(e.getPoint());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isResizing) {
                    Point currentPos = e.getLocationOnScreen();
                    int dx = currentPos.x - startPos.x;
                    int dy = currentPos.y - startPos.y;

                    int newX = startLocation.x;
                    int newY = startLocation.y;
                    int newWidth = startSize.width;
                    int newHeight = startSize.height;

                    switch (resizeDirection) {
                        case NW:
                            newX = startLocation.x + dx;
                            newY = startLocation.y + dy;
                            newWidth = startSize.width - dx;
                            newHeight = startSize.height - dy;
                            break;
                        case N:
                            newY = startLocation.y + dy;
                            newHeight = startSize.height - dy;
                            break;
                        case NE:
                            newY = startLocation.y + dy;
                            newWidth = startSize.width + dx;
                            newHeight = startSize.height - dy;
                            break;
                        case E:
                            newWidth = startSize.width + dx;
                            break;
                        case SE:
                            newWidth = startSize.width + dx;
                            newHeight = startSize.height + dy;
                            break;
                        case S:
                            newHeight = startSize.height + dy;
                            break;
                        case SW:
                            newX = startLocation.x + dx;
                            newWidth = startSize.width - dx;
                            newHeight = startSize.height + dy;
                            break;
                        case W:
                            newX = startLocation.x + dx;
                            newWidth = startSize.width - dx;
                            break;
                    }

                    // ensure minimum size
                    if (newWidth >= frame.getMinimumSize().width && 
                        newHeight >= frame.getMinimumSize().height) {
                        frame.setBounds(newX, newY, newWidth, newHeight);
                        frame.revalidate();
                    }
                }
            }
        });
    }

    private boolean isInResizeZone(Point p) {
        if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
            return false;
        }

        int x = p.x;
        int y = p.y;
        int width = frame.getWidth();
        int height = frame.getHeight();

        // check corners first (they take precedence)
        if (x <= RESIZE_BORDER && y <= RESIZE_BORDER) {
            resizeDirection = NW;
            return true;
        }
        if (x >= width - RESIZE_BORDER && y <= RESIZE_BORDER) {
            resizeDirection = NE;
            return true;
        }
        if (x >= width - RESIZE_BORDER && y >= height - RESIZE_BORDER) {
            resizeDirection = SE;
            return true;
        }
        if (x <= RESIZE_BORDER && y >= height - RESIZE_BORDER) {
            resizeDirection = SW;
            return true;
        }

        // then check borders
        if (y <= RESIZE_BORDER) {
            resizeDirection = N;
            return true;
        }
        if (x >= width - RESIZE_BORDER) {
            resizeDirection = E;
            return true;
        }
        if (y >= height - RESIZE_BORDER) {
            resizeDirection = S;
            return true;
        }
        if (x <= RESIZE_BORDER) {
            resizeDirection = W;
            return true;
        }

        return false;
    }

    private void updateResizeCursor(Point p) {
        if (isInResizeZone(p)) {
            switch (resizeDirection) {
                case NW:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    break;
                case N:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    break;
                case NE:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    break;
                case E:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    break;
                case SE:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    break;
                case S:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    break;
                case SW:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    break;
                case W:
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    break;
            }
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }
}