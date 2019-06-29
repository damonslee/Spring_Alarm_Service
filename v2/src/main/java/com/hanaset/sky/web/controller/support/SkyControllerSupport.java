package com.hanaset.sky.web.controller.support;

public abstract class SkyControllerSupport {

    protected String redirect(String url) {
        return "redirect:".concat(url);
    }
}
