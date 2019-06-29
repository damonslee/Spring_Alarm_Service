package com.hanaset.sky.web.controller;

import com.hanaset.sky.web.controller.support.SkyControllerSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SkySwaggerController extends SkyControllerSupport {

    @GetMapping("/swagger")
    public String redirect() {return redirect("/swagger-ui.html");}
}
