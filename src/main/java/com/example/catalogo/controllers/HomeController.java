package com.example.catalogo.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/index")
    public String index(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            String role = auth.getAuthorities().stream().findFirst().get().getAuthority();
            model.addAttribute("userRole", role.replace("ROLE_", ""));
        } else {
            model.addAttribute("userRole", "GUEST");
        }
        return "index";
    }

}
