package com.hanaset.sky.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KakaoSQSItem {

    private String medium;

    private String recipient;

    private String template;

    private String text;
}
