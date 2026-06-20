package com.teamzeu.velo.common.email;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmailModel {
    private String from;
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateModel;
    private MultipartFile attachment;
}
