package com.teamzeu.velo.user.controller;

import com.teamzeu.velo.common.cloudinary.CloudinaryService;
import com.teamzeu.velo.common.cloudinary.CloudinaryService.UploadResult;
import com.teamzeu.velo.user.model.User;
import com.teamzeu.velo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @PostMapping("/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable UUID id,
                                          @RequestParam("file") MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        UploadResult uploadResult = cloudinaryService.upload(file, "avatars");
        user.setAvatar_url(uploadResult.url());
        userRepository.save(user);
        return ResponseEntity.ok().body(Map.of("avatarUrl", uploadResult.url()));
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> changeAvatar(@PathVariable UUID id,
                                          @RequestParam("file") MultipartFile file) throws IOException {
        return uploadAvatar(id, file);
    }
}
