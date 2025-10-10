package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

@Data
public class BlogImageResponse {
    private String blogImageId;
    private String imageUrl;
    private String altText;
    private Integer position;
    private String blogId;
    private IsActived isActived;
}
