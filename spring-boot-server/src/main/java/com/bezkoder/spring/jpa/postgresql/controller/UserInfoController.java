package com.bezkoder.spring.jpa.postgresql.controller;

import com.bezkoder.spring.jpa.postgresql.dto.UserInfoDto;
import com.bezkoder.spring.jpa.postgresql.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserInfoController {

    private final UserInfoService service;

    public UserInfoController(UserInfoService service) {
        this.service = service;
    }

    // A: remote search
    @GetMapping("/search")
    public List<UserInfoDto> search(
            @RequestParam(defaultValue = "1") Long orgId,
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.search(orgId, q, limit);
    }

    // B: fetch all once (for your “1000 users local filter” experiment)
    @GetMapping("/all")
    public List<UserInfoDto> all(
            @RequestParam(defaultValue = "1") Long orgId,
            @RequestParam(defaultValue = "1000") int limit
    ) {
        return service.all(orgId, limit);
    }
}

