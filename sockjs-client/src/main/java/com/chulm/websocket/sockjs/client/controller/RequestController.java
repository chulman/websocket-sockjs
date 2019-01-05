package com.chulm.websocket.sockjs.client.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * '/' 경로 제외 , 모두 404 페이지
 */
@Controller
public class RequestController {

    @RequestMapping(value = "/{path}", method = RequestMethod.GET)
    public ModelAndView get(@PathVariable String path, ModelAndView modelAndView) {
        modelAndView.addObject("error","/" + path +" is not found");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND);
        modelAndView.setViewName("error/4xx_error");
        return modelAndView;
    }

    @RequestMapping(value = "/{path}", method = RequestMethod.POST)
    public ModelAndView post(@PathVariable String path, ModelAndView modelAndView) {
        modelAndView.addObject("error", "/" + path +" is not found");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND);
        modelAndView.setViewName("error/4xx_error");
        return modelAndView;
    }
}
