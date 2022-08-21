package com.wjx.community.controller;

import com.wjx.community.common.CommunityConstant;
import com.wjx.community.common.Page;
import com.wjx.community.common.UserLocal;
import com.wjx.community.service.DiscussPostService;
import com.wjx.community.service.Impl.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author wjx
 * @description
 */

@Controller
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;


    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String root() {
        return "forward:/index";
    }


    @GetMapping("/index")
    public String getIndexPage(Model model, Page page,@RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        int rows = discussPostService.getAllDiscussPostRows(0);
        page.setRows(rows);
        page.setPath("/index?orderMode=" + orderMode);
        List<Map<String,Object>> discussPosts = discussPostService.findAllDiscussPosts(0,page.getOffset(),page.getLimit());
        model.addAttribute("discussPosts",discussPosts);
        //排行模式
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }

    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "/error/404";
    }
}
