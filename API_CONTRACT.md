# API Design for CASE

## 1. Create a "Smart" Post
**Endpoint:** `POST /api/v1/posts`
**Description:** Schedules a post with attached conditional rules.

**Request JSON:**
```json
{
  "userId": 1,
  "content": "Check out our new summer collection!",
  "platform": "TWITTER",
  "scheduledTime": "2026-06-01T10:00:00",
  "rules": [
    {
      "ruleType": "WEATHER",
      "conditionValue": "RAIN",
      "action": "BLOCK"
    }
  ]
}