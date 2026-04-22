package com.caseapp.socialengine.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caseapp.socialengine.dto.PostRequestDTO;
import com.caseapp.socialengine.entity.Post;
import com.caseapp.socialengine.entity.PostRule;
import com.caseapp.socialengine.entity.User;
import com.caseapp.socialengine.enums.Platform;
import com.caseapp.socialengine.enums.RuleAction;
import com.caseapp.socialengine.enums.RuleType;
import com.caseapp.socialengine.repository.PostRepository;
import com.caseapp.socialengine.repository.UserRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Post createPost(PostRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User with ID " + requestDTO.getUserId() + " not found."));

        Post post = Post.builder()
                .user(user)
                .content(requestDTO.getContent())
                .platform(Platform.valueOf(requestDTO.getPlatform().toUpperCase()))
                .scheduledTime(requestDTO.getScheduledTime())
                .build();

        if (requestDTO.getRules() != null && !requestDTO.getRules().isEmpty()) {
            List<PostRule> rules = requestDTO.getRules().stream().map(ruleDto ->
                    PostRule.builder()
                            .ruleType(RuleType.valueOf(ruleDto.getRuleType().toUpperCase()))
                            .conditionValue(ruleDto.getConditionValue())
                            .action(RuleAction.valueOf(ruleDto.getAction().toUpperCase()))
                            .post(post) 
                            .build()
            ).collect(Collectors.toList());
            
            post.setRules(rules);
        }

        return postRepository.save(post);
    }

    // NEW: Fetch all posts for a specific user
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // NEW: Fetch a single post by its ID
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
    }
}