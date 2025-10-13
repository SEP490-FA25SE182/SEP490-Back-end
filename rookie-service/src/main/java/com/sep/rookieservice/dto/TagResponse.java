package com.sep.rookieservice.dto;

import com.sep.rookieservice.enums.IsActived;
import lombok.Data;

@Data
public class TagResponse {
    private String tagId;
    private String name;
    private IsActived isActived;
}
