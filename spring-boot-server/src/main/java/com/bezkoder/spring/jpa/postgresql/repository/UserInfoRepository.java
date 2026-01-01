package com.bezkoder.spring.jpa.postgresql.repository;

import com.bezkoder.spring.jpa.postgresql.entity.UserInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("""
        select u from UserInfo u
        where u.orgId = :orgId
          and (
            lower(u.email) like lower(concat('%', :q, '%'))
            or lower(u.uname) like lower(concat('%', :q, '%'))
            or lower(u.firstname) like lower(concat('%', :q, '%'))
            or lower(u.lastname) like lower(concat('%', :q, '%'))
          )
        order by u.id asc
        """)
    List<UserInfo> searchByOrgIdAndQuery(@Param("orgId") Long orgId, @Param("q") String q);
}

