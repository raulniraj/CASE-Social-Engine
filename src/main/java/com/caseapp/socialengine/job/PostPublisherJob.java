package com.caseapp.socialengine.job;

import java.time.LocalDateTime;
import java.util.List;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.caseapp.socialengine.dto.LiveContext;
import com.caseapp.socialengine.entity.Post;
import com.caseapp.socialengine.entity.PostRule;
// NOTICE: PostStatus import is completely gone!
import com.caseapp.socialengine.repository.PostRepository;
import com.caseapp.socialengine.service.ContextGatheringService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PostPublisherJob extends QuartzJobBean {

    private final PostRepository postRepository;
    private final KieContainer kieContainer;
    private final ContextGatheringService contextGatheringService; 

    public PostPublisherJob(PostRepository postRepository, KieContainer kieContainer, ContextGatheringService contextGatheringService) {
        this.postRepository = postRepository;
        this.kieContainer = kieContainer;
        this.contextGatheringService = contextGatheringService;
    }

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();
        
        // CHANGED: We now pass the String "PENDING" to the repository
        List<Post> readyPosts = postRepository.findByStatusAndScheduledTimeLessThanEqual("PENDING", now);

        if (readyPosts.isEmpty()) return;

        // 1. Fetch the live data from the outside world!
        LiveContext liveContext = contextGatheringService.fetchCurrentWorldContext();

        for (Post post : readyPosts) {
            KieSession kieSession = kieContainer.newKieSession();
            
            // 2. Feed the Post, Rules, AND the Live Context into the AI Brain
            kieSession.insert(post);
            kieSession.insert(liveContext); 
            
            if (post.getRules() != null) {
                for (PostRule rule : post.getRules()) {
                    kieSession.insert(rule);
                }
            }
            
            kieSession.fireAllRules();
            kieSession.dispose();
            
            // CHANGED: Cleaned up the if-statement syntax and using Strings
            if ("PENDING".equals(post.getStatus()) || "PROCEED".equals(post.getStatus())) {
                post.setStatus("PUBLISHED");
                log.info("Post ID: {} successfully published!", post.getId());
            } else {
                log.warn("Post ID: {} intercepted by Drools. Status: {}", post.getId(), post.getStatus());
            }
            postRepository.save(post);
        }
    }
}