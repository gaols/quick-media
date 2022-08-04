package com.github.hui.quick.plugin.qrcode.v3.constants;

import com.github.hui.quick.plugin.qrcode.constants.QuickQrUtil;
import com.github.hui.quick.plugin.qrcode.v3.entity.render.RenderDot;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * 绘制二维码信息的样式
 */
public enum DrawStyle {
    /**
     * 几何 - 矩形
     */
    RECT(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            g2d.fillRect(x, y, w, h);
        }
    },
    /**
     * 几何 - 小矩形
     */
    MINI_RECT(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            int offsetX = w / 5, offsetY = h / 5;
            int width = w - offsetX * 2, height = h - offsetY * 2;
            g2d.fillRect(x + offsetX, y + offsetY, width, height);
        }
    },
    /**
     * 几何 - 圆
     */
    CIRCLE(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            g2d.fill(new Ellipse2D.Float(x, y, w, h));
        }
    },
    /**
     * 几何 - 三角形
     */
    TRIANGLE(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            int px[] = {x, x + (w >> 1), x + w};
            int py[] = {y + w, y, y + w};
            g2d.fillPolygon(px, py, 3);
        }
    },
    /**
     * 几何 - 五边形-钻石
     */
    DIAMOND(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            int size = Math.min(w, h);
            int cell4 = size >> 2;
            int cell2 = size >> 1;
            int px[] = {x + cell4, x + size - cell4, x + size, x + cell2, x};
            int py[] = {y, y, y + cell2, y + size, y + cell2};
            g2d.fillPolygon(px, py, 5);
        }
    },
    /**
     * 几何 - 六边形
     */
    SEXANGLE(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            int size = Math.min(w, h);
            int add = size >> 2;
            int px[] = {x + add, x + size - add, x + size, x + size - add, x + add, x};
            int py[] = {y, y, y + add + add, y + size, y + size, y + add + add};
            g2d.fillPolygon(px, py, 6);
        }
    },
    /**
     * 几何 - 八边形
     */
    OCTAGON(0) {
        @Override
        public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
            int size = Math.min(w, h);
            int add = size / 3;
            int px[] = {x + add, x + size - add, x + size, x + size, x + size - add, x + add, x, x};
            int py[] = {y, y, y + add, y + size - add, y + size, y + size, y + size - add, y + add};
            g2d.fillPolygon(px, py, 8);
        }
    },
    /**
     * 图片
     */
    IMAGE(1) {
        @Override
        public void imgDrawFunc(Graphics2D g2d, BufferedImage img, int x, int y, int w, int h) {
            if (img == null) {
                // 兜底的绘制方式
                g2d.setColor(Color.BLACK);
                g2d.fillRect(x, y, w, h);
            } else {
                g2d.drawImage(img.getScaledInstance(w, h, Image.SCALE_SMOOTH), x, y, null);
            }
        }
    },

    /**
     * 文字
     */
    TXT(2) {
        @Override
        public void txtImgDrawFunc(Graphics2D g2d, String txt, int x, int y, int size) {
            if (txt == null) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(x, y, size, size);
            }
            else{
                Font oldFont = g2d.getFont();
                if (oldFont.getSize() != size) {
                    Font newFont = QuickQrUtil.font(oldFont.getName(), oldFont.getStyle(), size);
                    g2d.setFont(newFont);
                }
                g2d.drawString(txt, x, y + size);
                g2d.setFont(oldFont);
            }
        }
    },

    /**
     * svg
     */
    SVG(3) {

    };

    /**
     * 0 几何图形
     * 1 图片
     * 2 文字
     * 3 svg
     */
    private int style;

    DrawStyle(int style) {
        this.style = style;
    }

    public void drawAsImg(Graphics2D g2d, RenderDot renderDot) {
        switch (style) {
            case 0:
                renderDot.renderGeometry(g2d, this::geometryDrawFunc);
                return;
            case 1:
                renderDot.renderImg(g2d, this::imgDrawFunc);
                return;
            case 2:
                renderDot.renderTxt(g2d, this::txtImgDrawFunc);
                return;
            case 3:
                return;
        }
    }

    public void geometryDrawFunc(Graphics2D g2d, int x, int y, int w, int h) {
    }

    public void imgDrawFunc(Graphics2D g2d, BufferedImage img, int x, int y, int w, int h) {
    }

    public void txtImgDrawFunc(Graphics2D g2d, String txt, int x, int y, int size) {
    }

}
