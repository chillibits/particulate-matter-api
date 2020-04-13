/*
 * Copyright © Marc Auberer 2019 - 2020. All rights reserved
 */

package com.chillibits.particulatematterapi.repository;

import com.chillibits.particulatematterapi.model.db.main.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Integer> {

    @Query("SELECT l FROM Link l WHERE l.user.id = ?1")
    List<Link> getLinksByUserId(Integer userId);

    @Modifying
    @Query("UPDATE Link l SET l.owner = ?2, l.name = ?3, l.color = ?4 WHERE l.id = ?1")
    Integer updateLink(
            Integer id,
            boolean owner,
            String name,
            int color
    );
}
