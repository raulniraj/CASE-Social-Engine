package com.caseapp.socialengine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.caseapp.socialengine.dto.LlmResponse;
import com.caseapp.socialengine.entity.User;
import com.caseapp.socialengine.repository.UserRepository;
import com.caseapp.socialengine.service.LlmAnalyzerService;
// If you use a PasswordEncoder, you might need to inject it here later!

@SpringBootApplication
public class SocialengineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialengineApplication.class, args);
    }

    // THIS IS OUR TEMPORARY TEST
    @Bean
    CommandLineRunner testBrain(LlmAnalyzerService aiService) {
        return args -> {
            System.out.println("=========================================");
            System.out.println("TESTING THE AI BRAIN WITH MULTI-CONTEXT...");
            
            String testPost = "Don't let the gloomy skies ruin your day! Grab our new waterproof jacket at 20% off.";
            
            // We now provide 3 arguments to match the upgraded Service
            String mockWeather = "Rainy and 18°C";
            String mockNews = "Heavy rainfall alerts issued for Northern India | Market hits all-time high";

            LlmResponse response = aiService.analyzePostContext(testPost, mockWeather, mockNews);
            
            System.out.println("Status: " + response.getStatus()); 
            System.out.println("AI Reason: " + response.getReason());
            System.out.println("AI Suggestion: " + response.getSuggestedContent());
            System.out.println("=========================================");
        };
    }

    // THIS SEEDS THE DATABASE SO IT IS NEVER EMPTY
    // THIS SEEDS THE DATABASE SO IT IS NEVER EMPTY
    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User testUser = new User();
                
                // NO setUsername() because it doesn't exist in your Entity!
                testUser.setEmail("admin"); // You are using email as the login!
                
                // You named the field passwordHash in the Entity, so Lombok created setPasswordHash()
                testUser.setPasswordHash("password123"); 
                
                testUser.setRole("ROLE_ADMIN"); // Good practice to set a role
                
                userRepository.save(testUser);
                System.out.println("=========================================");
                System.out.println("TEST USER #1 SEEDED INTO THE DATABASE!");
                System.out.println("=========================================");
            }
        };
    
    }
} 