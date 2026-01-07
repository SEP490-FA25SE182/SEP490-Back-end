package com.sep.arservice.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public final class AprilTagImageUpscaler {

    private AprilTagImageUpscaler() {}

    public static byte[] upscaleNearestBinary(byte[] pngBytes, int targetPx) {
        if (targetPx < 200) throw new IllegalArgumentException("targetPx too small: " + targetPx);

        try {
            BufferedImage src = ImageIO.read(new ByteArrayInputStream(pngBytes));
            if (src == null) throw new IllegalArgumentException("Invalid PNG bytes");

            // 1) upscale nearest onto RGB
            BufferedImage up = new BufferedImage(targetPx, targetPx, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = up.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, targetPx, targetPx);
            g.drawImage(src, 0, 0, targetPx, targetPx, null);
            g.dispose();

            // 2) force binary (no gray)
            BufferedImage bin = new BufferedImage(targetPx, targetPx, BufferedImage.TYPE_BYTE_BINARY);
            for (int y = 0; y < targetPx; y++) {
                for (int x = 0; x < targetPx; x++) {
                    int rgb = up.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int gg = (rgb >> 8) & 0xff;
                    int b = (rgb) & 0xff;
                    int lum = (r * 77 + gg * 150 + b * 29) >> 8;

                    int out = (lum >= 128) ? 0xFFFFFF : 0x000000;
                    bin.setRGB(x, y, (0xFF << 24) | out);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bin, "png", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Upscale AprilTag PNG failed", e);
        }
    }
}
