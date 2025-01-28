package com.balsamic.sejongmalsami_monitoring.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

  @GetMapping("/login")
  public String loginPage() {
    return "pages/login";
  }

  @GetMapping("/dashboard")
  public String dashboardPage(Model model){
    return "pages/dashboard";
  }
}
