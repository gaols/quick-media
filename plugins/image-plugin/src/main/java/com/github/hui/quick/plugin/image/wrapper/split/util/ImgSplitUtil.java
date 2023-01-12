package com.github.hui.quick.plugin.image.wrapper.split.util;

import com.github.hui.quick.plugin.base.awt.GraphicUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author YiHui
 * @date 2023/1/9
 */
public class ImgSplitUtil {

    public static List<BufferedImage> split(BufferedImage origin, Predicate<Integer> predicate) {
        List<BufferedImage> ans = new ArrayList<>();
        while (true) {
            BufferedImage o = pickOneImg(origin, predicate);
            if (o != null) {
                ans.add(o);
            } else {
                break;
            }
        }
        return ans;
    }

    private static BufferedImage pickOneImg(BufferedImage img, Predicate<Integer> predicate) {
        int pointX = 0, pointY = 0;
        boolean pointChoose = false;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (!bgColor(predicate, img.getRGB(x, y))) {
                    // 找到第一个边界点
                    pointX = x;
                    pointY = y;
                    pointChoose = true;
                    break;
                }
            }
            if (pointChoose) {
                break;
            }
        }

        if (!pointChoose) {
            return null;
        }

        int upY = pointY, downY = pointY, leftX = pointX, rightX = pointX;
        // 采用边框识别的方式，获取最高点、最低点、最左点、最右点
        List<Point> scanPoints = new ArrayList<>();
        Point current = new Point(pointX, pointY);
        int scanIndex = 0;
        while (true) {
            if (!scanPoints.contains(current)) {
                scanPoints.add(current);
            }

            // 上
            Point next = new Point(current.x, current.y - 1);
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                current = next;
                upY = Math.min(upY, next.y);
                scanIndex = 0;
                continue;
            }

            // 下
            next.y = current.y + 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                current = next;
                downY = Math.max(downY, next.y);
                scanIndex = 0;
                continue;
            }

            // 左
            next.y = current.y;
            next.x = current.x - 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                current = next;
                leftX = Math.min(leftX, next.x);
                scanIndex = 0;
                continue;
            }

            // 右
            next.x = current.x + 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                current = next;
                rightX = Math.max(rightX, next.x);
                scanIndex = 0;
                continue;
            }

            next.x = current.x - 1;
            next.y = current.y - 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                // 左上
                current = next;
                leftX = Math.min(leftX, next.x);
                upY = Math.min(upY, next.y);
                scanIndex = 0;
                continue;
            }

            next.x = current.x + 1;
            next.y = current.y - 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                // 右上
                current = next;
                rightX = Math.max(rightX, next.x);
                upY = Math.min(upY, next.y);
                scanIndex = 0;
                continue;
            }


            next.x = current.x - 1;
            next.y = current.y + 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                // 左下
                current = next;
                leftX = Math.min(leftX, next.x);
                downY = Math.max(downY, next.y);
                scanIndex = 0;
                continue;
            }

            next.x = current.x + 1;
            next.y = current.y + 1;
            if (!scanPoints.contains(next) && isBorderPoint(img, next.x, next.y, predicate)) {
                // 右下
                current = next;
                rightX = Math.max(rightX, next.x);
                downY = Math.max(downY, next.y);
                scanIndex = 0;
                continue;
            }

            if (scanIndex >= scanPoints.size()) {
                break;
            }
            current = scanPoints.get(scanIndex);
            scanIndex++;
        }

        // 上下左右已找到，直接切图
        int w = rightX - leftX, h = downY - upY;
        BufferedImage out = GraphicUtil.createImg(w, h, 0, 0, null);
        Graphics2D g2d = GraphicUtil.getG2d(out);
        for (int x = leftX; x <= rightX; x++) {
            for (int y = upY; y <= downY; y++) {
                g2d.setColor(new Color(img.getRGB(x, y), true));
                g2d.drawRect(x - leftX, y - upY, 1, 1);
                // 将裁剪出来的区域全部设置为空
                img.setRGB(x, y, 0);
            }
        }
        g2d.dispose();
        return out;
    }

    private static boolean isBorderPoint(BufferedImage img, int x, int y, Predicate<Integer> predicate) {
        if (bgColor(predicate, img.getRGB(x, y))) {
            return false;
        }

        if (y > 0 && bgColor(predicate, img.getRGB(x, y - 1))) {
            // 若上面的一个点，是背景点，则是边界
            return true;
        }

        if (x > 0 && bgColor(predicate, img.getRGB(x - 1, y))) {
            return true;
        }

        if (x < img.getWidth() - 1 && bgColor(predicate, img.getRGB(x + 1, y))) {
            return true;
        }

        if (y < img.getHeight() - 1 && bgColor(predicate, img.getRGB(x, y + 1))) {
            return true;
        }

        if (y > 0 && x > 0 && bgColor(predicate, img.getRGB(x - 1, y - 1))) {
            // 左上
            return true;
        }

        if (y > 0 && x < img.getWidth() - 1 && bgColor(predicate, img.getRGB(x + 1, y - 1))) {
            // 右上
            return true;
        }

        if (x > 0 && y < img.getHeight() - 1 && bgColor(predicate, img.getRGB(x - 1, y + 1))) {
            // 左下
            return true;
        }

        if (x < img.getWidth() - 1 && y < img.getHeight() - 1 && bgColor(predicate, img.getRGB(x + 1, y + 1))) {
            // right down
            return true;
        }

        return false;
    }

    private static boolean bgColor(Predicate<Integer> predicate, int rgbColor) {
        return predicate != null ? predicate.test(rgbColor) : new Color(rgbColor, true).getAlpha() == 0;
    }

}
