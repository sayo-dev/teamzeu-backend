package com.teamzeu.velo.common.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public UploadResult upload(MultipartFile file, String folder) throws IOException {
        var params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "unique_filename", true,
                "use_filename", true
        );
        Map<?,?> result = cloudinary.uploader().upload(file.getBytes(), params);
        return new UploadResult((String) result.get("secure_url"), (String) result.get("public_id"));
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public record UploadResult(String url, String publicId) {}
}

