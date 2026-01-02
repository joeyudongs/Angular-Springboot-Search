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

    /**
     * A) Remote search endpoint:
     * Example:
     * GET /api/users/search?orgId=1&q=first&limit=20
     */
    @GetMapping("/search")
    public List<UserInfoDto> search(
            @RequestParam long orgId,
            @RequestParam String q,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return service.search(orgId, q, limit);
    }

    /**
     * B) Fetch all (up to limit) for local filtering experiment:
     * Example:
     * GET /api/users/all?orgId=2&limit=1000
     */
    @GetMapping("/all")
    public List<UserInfoDto> all(
            @RequestParam long orgId,
            @RequestParam(defaultValue = "1000") int limit
    ) {
        return service.all(orgId, limit);
    }

    
    // // A: remote search
    // @GetMapping("/search")
    // public List<UserInfoDto> search(
    //         @RequestParam(defaultValue = "1") Long orgId,
    //         @RequestParam String q,
    //         @RequestParam(defaultValue = "20") int limit
    // ) {
    //     return service.search(orgId, q, limit);
    // }

    // // B: fetch all once (for your “1000 users local filter” experiment)
    // @GetMapping("/all")
    // public List<UserInfoDto> all(
    //         @RequestParam(defaultValue = "1") Long orgId,
    //         @RequestParam(defaultValue = "1000") int limit
    // ) {
    //     return service.all(orgId, limit);
    // }

    
}

