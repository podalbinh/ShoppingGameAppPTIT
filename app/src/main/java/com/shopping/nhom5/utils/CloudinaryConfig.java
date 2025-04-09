package com.shopping.nhom5.utils;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dgvl8woja");
            config.put("api_key", "996433731162577");
            config.put("api_secret", "IKbP7v8tTUNS5fGNxHKb2EIJvCs");
            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
} 