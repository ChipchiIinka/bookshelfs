package io.petprojects.bookshelfs.utill;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class MimeTypeUtil {
    private static final Map<String, String> mimeTypeMap = new HashMap<>();

    static {
        mimeTypeMap.put("application/pdf", "PDF Document");
        mimeTypeMap.put("application/msword", "Microsoft Word Document");
        mimeTypeMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word Document 2007");
        mimeTypeMap.put("text/plain", "Text");
        mimeTypeMap.put("text/html", "HTML Document");
        mimeTypeMap.put("image/jpeg", "JPEG Image");
        mimeTypeMap.put("image/png", "PNG Image");
        mimeTypeMap.put("image/gif", "GIF Image");
        mimeTypeMap.put("image/svg+xml", "Scalable Vector Graphics (SVG)");
        mimeTypeMap.put("image/tiff", "Tagged Image File Format (TIFF)");
        mimeTypeMap.put("image/bmp", "Bitmap Image (BMP)");
        mimeTypeMap.put("image/webp", "WebP Image");
    }

    public static String getReadableMimeType(String mimeType) {
        return mimeTypeMap.getOrDefault(mimeType, mimeType);
    }
}

