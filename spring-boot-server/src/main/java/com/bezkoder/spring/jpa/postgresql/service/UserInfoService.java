package com.bezkoder.spring.jpa.postgresql.service;


import com.bezkoder.spring.jpa.postgresql.dto.UserInfoDto;
import com.bezkoder.spring.jpa.postgresql.entity.UserInfo;
import com.bezkoder.spring.jpa.postgresql.repository.UserInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository repo;

    public UserInfoService(UserInfoRepository repo) {
        this.repo = repo;
    }

    public List<UserInfoDto> search(Long orgId, String q, int limit) {
        if (q == null || q.isBlank()) return List.of();

        return repo.searchByOrgIdAndQuery(orgId, q.trim())
                .stream()
                .limit(limit)
                .map(this::toDto)
                .toList();
    }

    public List<UserInfoDto> all(Long orgId, int limit) {
        // 简化：直接全表取再过滤 orgId（1000条无所谓）
        // 真正生产可以加 findByOrgId(...) + Pageable
        return repo.findAll().stream()
                .filter(u -> u.getOrgId().equals(orgId))
                .limit(limit)
                .map(this::toDto)
                .toList();
    }

    private UserInfoDto toDto(UserInfo u) {
        return new UserInfoDto(u.getId(), u.getEmail(), u.getUname(), u.getFirstname(), u.getLastname());
    }
}
