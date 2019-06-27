package com.hanaset.sky.item;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsSQSItem {

    private String medium;

    private String recipient;

    private String text;
}
