package com.bezkoder.spring.jpa.postgresql.service;

import com.bezkoder.spring.jpa.postgresql.dto.UserInfoDto;
import com.bezkoder.spring.jpa.postgresql.entity.UserInfo;
import com.bezkoder.spring.jpa.postgresql.repository.UserInfoRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInfoService {

    private final UserInfoRepository repo;

    public UserInfoService(UserInfoRepository repo) {
        this.repo = repo;
    }

    /**
     * Remote search:
     * - Always filter by orgId (multi-tenant isolation).
     * - Use PageRequest(0, limit) so DB returns only "limit" rows.
     */
    public List<UserInfoDto> search(long orgId, String q, int limit) {
        if (q == null || q.isBlank()) {
            return List.of();
        }

        int safeLimit = clampLimit(limit, 1, 100); // avoid crazy limits
        String text = q.trim();

        var pageable = PageRequest.of(0, safeLimit, Sort.by("id").ascending());

        return repo.search(orgId, text, pageable)
                   .getContent()        // Extracts the actual list of entities from the Page object
                   .stream()            // Converts the List<UserInfo> to a Stream<UserInfo>
                   .map(this::toDto)    // Maps each entity to a DTO (decouples API from persistence model)
                   .toList();           // Collects the stream into an immutable List
    }

    /**
     * Local mode bootstrap:
     * - Fetch up to "limit" users for ONE orgId.
     * - DB does the LIMIT via PageRequest(0, limit).
     */
    public List<UserInfoDto> all(long orgId, int limit) {
        int safeLimit = clampLimit(limit, 1, 50000); // allow larger for experiments

        var pageable = PageRequest.of(0, safeLimit, Sort.by("id").ascending());

        return repo.findByOrgId(orgId, pageable)
                   .getContent()
                   .stream()
                   .map(this::toDto)
                   .toList();
    }

    private UserInfoDto toDto(UserInfo u) {
        // DTO keeps only fields needed by frontend; hides internal fields like createdTime, orgId...
        return new UserInfoDto(u.getId(), u.getEmail(), u.getUname(), u.getFirstname(), u.getLastname());
    }

    private int clampLimit(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }

    // public List<UserInfoDto> search(Long orgId, String q, int limit) {
    //     if (q == null || q.isBlank()) return List.of();

    //     return repo.searchByOrgIdAndQuery(orgId, q.trim())
    //             .stream()
    //             .limit(limit)
    //             .map(this::toDto)
    //             .toList();
    // }

    // public List<UserInfoDto> all(Long orgId, int limit) {
    //     // 简化：直接全表取再过滤 orgId（1000条无所谓）
    //     // 真正生产可以加 findByOrgId(...) + Pageable
    //     return repo.findAll().stream()
    //             .filter(u -> u.getOrgId().equals(orgId))
    //             .limit(limit)
    //             .map(this::toDto)
    //             .toList();
    // }

    // Page<T> is used to let the database handle pagination efficiently.
    // getContent() simply extracts the actual data list from that Page.
    // Returning List<DTO> from the service layer is a common and recommended practice when pagination metadata is not needed by the client.
}
