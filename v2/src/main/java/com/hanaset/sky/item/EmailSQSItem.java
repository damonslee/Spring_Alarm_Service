package com.hanaset.sky.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSQSItem {

    private String medium;

    private String recipients;

    private String subject;

    private String html;
}
