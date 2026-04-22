package com.caseapp.socialengine.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.caseapp.socialengine.entity.Post;
import com.caseapp.socialengine.enums.PostStatus;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    List<Post> findByStatus(PostStatus status);
    // NEW: Fetch posts that are PENDING and their scheduled time has arrived
    
List<Post> findByStatusAndScheduledTimeLessThanEqual(String status, LocalDateTime scheduledTime);
}