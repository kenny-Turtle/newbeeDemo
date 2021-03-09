package com.kenny.mall.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IdeaController {

    @GetMapping("/info")
    @ResponseBody
    public String getInfoFromIdea(){
        return "this is a spring boot project from idea";
    }

}
