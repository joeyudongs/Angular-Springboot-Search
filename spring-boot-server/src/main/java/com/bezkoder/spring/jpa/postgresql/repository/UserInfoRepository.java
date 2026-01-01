package com.bezkoder.spring.jpa.postgresql.repository;

import com.bezkoder.spring.jpa.postgresql.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    //fetch all, and limited in service
    // @Query("""
    //     select u from UserInfo u
    //     where u.orgId = :orgId
    //       and (
    //         lower(u.email) like lower(concat('%', :q, '%'))
    //         or lower(u.uname) like lower(concat('%', :q, '%'))
    //         or lower(u.firstname) like lower(concat('%', :q, '%'))
    //         or lower(u.lastname) like lower(concat('%', :q, '%'))
    //       )
    //     order by u.id asc
    //     """)
    // List<UserInfo> searchByOrgIdAndQuery(@Param("orgId") Long orgId, @Param("q") String q);


    /**
     * Search users within ONE orgId by matching query text against:
     * - email
     * - uname
     * - firstname
     * - lastname
     * - "firstname lastname"
     *
     * NOTE:
     * We use Pageable so the database does the LIMIT (e.g., 20 rows),
     * instead of fetching everything and then trimming in Java.
     */
    @Query("""
        select u from UserInfo u
        where u.orgId = :orgId
          and (
            lower(u.email) like lower(concat('%', :q, '%'))
            or lower(u.uname) like lower(concat('%', :q, '%'))
            or lower(u.firstname) like lower(concat('%', :q, '%'))
            or lower(u.lastname) like lower(concat('%', :q, '%'))
            or lower(concat(u.firstname, ' ', u.lastname)) like lower(concat('%', :q, '%'))
          )
        order by u.id asc
        """)
    Page<UserInfo> search(@Param("orgId") long orgId,
                          @Param("q") String q,
                          Pageable pageable);

    /**
     * Fetch users of a given orgId with Pageable (LIMIT).
     * This supports your "fetch 1000 once" local filtering experiment.
     */
    Page<UserInfo> findByOrgId(long orgId, Pageable pageable);

}

