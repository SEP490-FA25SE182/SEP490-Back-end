package com.sep.arservice.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public class AprilTagEmbedder {

    // A5: 148mm x 210mm
    private static final double A5_W_M_PORTRAIT = 0.148;
    private static final double A5_H_M_PORTRAIT = 0.210;

    private AprilTagEmbedder() {}

    /**
     * Embed AprilTag vào góc top-right theo hướng "chìm tối đa" (Hướng A):
     * - Tag được "tint" theo màu nền vùng đặt tag (không còn đen/trắng rõ).
     * - Giảm delta luma mạnh (minDelta rất thấp) -> mắt người khó thấy.
     * - Quiet-zone không trắng, mà là nền tint nhẹ + feather.
     * - Thêm film grain nhẹ toàn trang.
     *
     * WARNING: càng chìm -> càng cần Unity preprocess (auto-contrast + Otsu).
     */
    public static byte[] embedTopRight(
            byte[] illustrationBytes,
            byte[] tagPngBytes,
            double tagSizeMeters,              // 0.03m
            Double camoStrengthNullable,        // 0..1 (khuyến nghị 0.85..1.0)
            Integer quietZoneAlphaNullable,     // 0..255 (khuyến nghị 25..60)
            Integer assumedDpiNullable,         // 300
            Double grainStrengthNullable        // 0..1 (khuyến nghị 0.18..0.28)
    ) {
        double camoStrength = camoStrengthNullable == null ? 0.95 : clamp01(camoStrengthNullable);
        int quietZoneAlpha = quietZoneAlphaNullable == null ? 40 : clamp255(quietZoneAlphaNullable);
        int assumedDpi = assumedDpiNullable == null ? 300 : Math.max(72, assumedDpiNullable);
        double grainStrength = grainStrengthNullable == null ? 0.22 : clamp01(grainStrengthNullable);

        BufferedImage illustration = readImage(illustrationBytes);
        BufferedImage tag = readImage(tagPngBytes);

        BufferedImage canvas = toARGB(illustration);

        // 0) Film grain nhẹ toàn trang để phá mảng phẳng (nhẹ thôi để không "bẩn")
        applyFilmGrain(canvas, grainStrength, false, 1337);

        // 1) pxPerMeter (A5 max)
        boolean landscape = canvas.getWidth() >= canvas.getHeight();
        double a5W = landscape ? A5_H_M_PORTRAIT : A5_W_M_PORTRAIT;
        double a5H = landscape ? A5_W_M_PORTRAIT : A5_H_M_PORTRAIT;

        double pxPerMeterFromA5 = Math.min(canvas.getWidth() / a5W, canvas.getHeight() / a5H);
        double pxPerMeterFromDpi = assumedDpi * 39.37007874;
        double pxPerMeter = (pxPerMeterFromA5 > 10) ? pxPerMeterFromA5 : pxPerMeterFromDpi;

        int tagPx = (int) Math.round(tagSizeMeters * pxPerMeter);
        tagPx = Math.max(tagPx, 160); // 0.03m: đừng quá nhỏ
        tagPx = Math.min(tagPx, Math.min(canvas.getWidth(), canvas.getHeight()) / 3);

        BufferedImage tagResized = resizeNearest(tag, tagPx, tagPx);

        // 2) Quiet-zone
        int qz = Math.max(12, (int) Math.round(tagPx * 0.22)); // 22%
        int pad = Math.max(12, (int) Math.round(Math.min(canvas.getWidth(), canvas.getHeight()) * 0.02));

        int zoneW = tagPx + 2 * qz;
        int zoneH = tagPx + 2 * qz;

        int zoneX = canvas.getWidth() - pad - zoneW;
        int zoneY = pad;

        zoneX = Math.max(0, zoneX);
        zoneY = Math.max(0, zoneY);
        if (zoneX + zoneW > canvas.getWidth()) zoneX = canvas.getWidth() - zoneW;
        if (zoneY + zoneH > canvas.getHeight()) zoneY = canvas.getHeight() - zoneH;

        int tagX = zoneX + qz;
        int tagY = zoneY + qz;

        // 3) Lấy màu nền vùng tag (mean RGB)
        int[] bgMean = meanRgb(canvas, tagX, tagY, tagPx, tagPx, 6);
        int bgR = bgMean[0], bgG = bgMean[1], bgB = bgMean[2];

        // 4) Tag "chìm tối đa": tint theo nền + delta luma nhỏ
        // minDelta cực thấp nhưng vẫn để Unity còn cứu được
        int minDelta = 26; // cực chìm (22~35). Nếu detect khó, tăng lên 34.
        BufferedImage tagPrepared = applyAdaptiveTintCamouflageMaxHidden(
                tagResized,
                bgR, bgG, bgB,
                camoStrength,
                minDelta,
                tagX, tagY
        );

        // 5) Vẽ quiet-zone dạng tint nền + feather mạnh (đỡ lộ mảng)
        int feather = Math.max(14, (int) Math.round(tagPx * 0.18));
        drawQuietZoneTintedFeather(
                canvas,
                zoneX, zoneY, zoneW, zoneH,
                bgR, bgG, bgB,
                quietZoneAlpha,
                feather,
                grainStrength
        );

        // 6) Vẽ tag
        Graphics2D g = canvas.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(tagPrepared, tagX, tagY, null);
        } finally {
            g.dispose();
        }

        return writePng(canvas);
    }

    // ----------------- “Max hidden” tag tint -----------------
    /**
     * Chuyển tag thành 2 tone theo màu nền (RGB), nhưng giữ chênh luma tối thiểu.
     * - strength càng cao => delta càng nhỏ => chìm hơn.
     * - minDelta: đáy cứng để còn cơ hội detect (Unity sẽ auto-contrast + Otsu).
     */
    private static BufferedImage applyAdaptiveTintCamouflageMaxHidden(
            BufferedImage tagBinaryLike,
            int bgR, int bgG, int bgB,
            double strength01,
            int minDelta,
            int seedX, int seedY
    ) {
        strength01 = clamp01(strength01);
        minDelta = Math.max(18, Math.min(60, minDelta));

        int w = tagBinaryLike.getWidth();
        int h = tagBinaryLike.getHeight();

        int bgLum = lum(bgR, bgG, bgB);

        // Delta mục tiêu: cực chìm -> delta tiệm cận minDelta
        // strength=1 => delta=minDelta
        // strength=0 => delta~70
        int delta = (int) Math.round(minDelta + (70 - minDelta) * (1.0 - strength01));
        delta = Math.max(minDelta, Math.min(80, delta));

        // 2 mức luma quanh nền
        int darkLum = clamp255(bgLum - delta);
        int lightLum = clamp255(bgLum + delta);

        // Đảm bảo không dính sát 128 quá (kẻo Otsu khó)
        // nhưng vẫn ưu tiên chìm
        if (darkLum > 118) darkLum = 118;
        if (lightLum < 138) lightLum = 138;

        // map luma -> RGB theo hướng "giữ hue nền"
        // scale = targetLum / bgLum, nhưng bgLum có thể 0 => fallback
        double eps = 1e-6;
        double bgLumSafe = Math.max(bgLum, 20);

        // Noise rất nhỏ trong tag (dither nhẹ) để bớt “blocky”
        int noiseAmp = (int) Math.round(1 + 3 * strength01); // 1..4
        noiseAmp = Math.max(1, Math.min(5, noiseAmp));
        Random rnd = new Random((((long) seedX) << 32) ^ (seedY * 1315423911L) ^ (w * 2654435761L) ^ h);

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = tagBinaryLike.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;

                int l = lum(r, g, b);
                boolean isWhite = l >= 128;

                int targetLum = isWhite ? lightLum : darkLum;

                int j = rnd.nextInt(2 * noiseAmp + 1) - noiseAmp;
                targetLum = clamp255(targetLum + j);

                double scale = targetLum / (bgLumSafe + eps);
                int rr = clamp255((int) Math.round(bgR * scale));
                int gg = clamp255((int) Math.round(bgG * scale));
                int bb = clamp255((int) Math.round(bgB * scale));

                // safety: dark vẫn phải tối hơn light rõ ràng (dù ít)
                if (!isWhite) {
                    rr = Math.min(rr, 140);
                    gg = Math.min(gg, 140);
                    bb = Math.min(bb, 140);
                } else {
                    rr = Math.max(rr, 90);
                    gg = Math.max(gg, 90);
                    bb = Math.max(bb, 90);
                }

                out.setRGB(x, y, (a << 24) | (rr << 16) | (gg << 8) | bb);
            }
        }
        return out;
    }

    // ----------------- Quiet-zone tinted feather -----------------
    private static void drawQuietZoneTintedFeather(
            BufferedImage canvas,
            int x, int y, int w, int h,
            int bgR, int bgG, int bgB,
            int baseAlpha,
            int featherPx,
            double grainStrength
    ) {
        baseAlpha = clamp255(baseAlpha);
        featherPx = Math.max(8, featherPx);

        BufferedImage overlay = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // jitter alpha nhẹ để không “mảng phẳng”
        int jitterAmp = (int) Math.round(4 + 10 * clamp01(grainStrength)); // 4..14
        jitterAmp = Math.max(3, Math.min(18, jitterAmp));
        Random rnd = new Random((((long) x) << 32) ^ (y * 2654435761L) ^ (w * 97531L) ^ h);

        int x2 = w - 1;
        int y2 = h - 1;

        // overlay không trắng: tint nền + hơi nâng sáng 1 chút (để vẫn còn “quiet zone” cho detector)
        int lift = 10; // rất nhẹ
        int oR = clamp255(bgR + lift);
        int oG = clamp255(bgG + lift);
        int oB = clamp255(bgB + lift);

        for (int yy = 0; yy < h; yy++) {
            for (int xx = 0; xx < w; xx++) {
                int dLeft = xx;
                int dRight = x2 - xx;
                int dTop = yy;
                int dBottom = y2 - yy;
                int dEdge = Math.min(Math.min(dLeft, dRight), Math.min(dTop, dBottom));

                double f = clamp01(dEdge / (double) featherPx);
                int a = (int) Math.round(baseAlpha * f);

                int j = rnd.nextInt(2 * jitterAmp + 1) - jitterAmp;
                a = clamp255(a + j);

                overlay.setRGB(xx, yy, (a << 24) | (oR << 16) | (oG << 8) | oB);
            }
        }

        Graphics2D g = canvas.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

            g.setComposite(AlphaComposite.SrcOver);
            g.drawImage(overlay, x, y, null);
        } finally {
            g.dispose();
        }
    }

    // ----------------- Film grain -----------------
    private static void applyFilmGrain(BufferedImage img, double strength01, boolean colored, long seed) {
        if (img == null || strength01 <= 0) return;

        int w = img.getWidth(), h = img.getHeight();

        // 0.22 -> amp ~3..4 (nhẹ)
        int amp = (int) Math.round(1 + strength01 * 4); // 1..5
        amp = Math.max(1, Math.min(6, amp));

        Random rnd = new Random(seed);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = img.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;

                int nR, nG, nB;
                if (colored) {
                    nR = rnd.nextInt(2 * amp + 1) - amp;
                    nG = rnd.nextInt(2 * amp + 1) - amp;
                    nB = rnd.nextInt(2 * amp + 1) - amp;
                } else {
                    int n = rnd.nextInt(2 * amp + 1) - amp;
                    nR = nG = nB = n;
                }

                int rr = clamp255(r + nR);
                int gg = clamp255(g + nG);
                int bb = clamp255(b + nB);

                img.setRGB(x, y, (a << 24) | (rr << 16) | (gg << 8) | bb);
            }
        }
    }

    // ----------------- helpers -----------------
    private static BufferedImage readImage(byte[] bytes) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            if (img == null) throw new IllegalArgumentException("Invalid image bytes");
            return img;
        } catch (IOException e) {
            throw new RuntimeException("Read image failed", e);
        }
    }

    private static byte[] writePng(BufferedImage img) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Write PNG failed", e);
        }
    }

    private static BufferedImage toARGB(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_ARGB) return src;
        BufferedImage dst = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dst;
    }

    private static BufferedImage resizeNearest(BufferedImage src, int w, int h) {
        BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dst.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return dst;
    }

    private static int[] meanRgb(BufferedImage img, int x, int y, int w, int h, int stride) {
        long sr = 0, sg = 0, sb = 0;
        long cnt = 0;

        int xEnd = Math.min(img.getWidth(), x + w);
        int yEnd = Math.min(img.getHeight(), y + h);

        int xs = Math.max(0, x);
        int ys = Math.max(0, y);

        for (int yy = ys; yy < yEnd; yy += stride) {
            for (int xx = xs; xx < xEnd; xx += stride) {
                int argb = img.getRGB(xx, yy);
                int r = (argb >>> 16) & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb) & 0xFF;
                sr += r; sg += g; sb += b;
                cnt++;
            }
        }
        if (cnt == 0) return new int[]{255, 255, 255};
        return new int[]{
                (int) Math.round(sr / (double) cnt),
                (int) Math.round(sg / (double) cnt),
                (int) Math.round(sb / (double) cnt)
        };
    }

    private static int lum(int r, int g, int b) {
        return (r * 77 + g * 150 + b * 29) >> 8;
    }

    private static double clamp01(double v) { return Math.max(0, Math.min(1, v)); }
    private static int clamp255(int v) { return Math.max(0, Math.min(255, v)); }
}
