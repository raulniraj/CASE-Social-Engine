# CASE: Context-Aware Social Engine 🧠📱

**CASE** is a next-generation social media scheduling tool that moves beyond simple time-based automation. Unlike traditional tools that blindly publish content at a set time, CASE evaluates **real-world context** before every post to ensure brand safety and relevance.

### 🚀 The Problem
Traditional schedulers fire "Happy Friday!" posts even during national crises or advertise products that are out of stock. They are "blind" to the world around them.

### 💡 The Solution
CASE introduces a **"Pre-Flight Check"** system using a Java-based Rule Engine. Before a post goes live, it checks:
* **Crisis Shield:** Is the current news sentiment negative? (via NewsAPI + Stanford CoreNLP)
* **Weather Triggers:** Is it raining in the target city? Swap the ad creative to a "Cozy/Indoor" version. (via OpenWeatherMap)
* **Inventory Guard:** Is the item in stock? (via Inventory Mock API)

### 🛠️ Tech Stack
* **Backend:** Java 21, Spring Boot 3.3
* **Logic Engine:** Apache Drools (Complex Decision Rules)
* **Scheduling:** Quartz Scheduler (Enterprise-grade Job Scheduling)
* **Database:** PostgreSQL (Structured Data) & MongoDB (Logs)
* **Frontend:** React.js + TypeScript
* **AI/NLP:** Stanford CoreNLP

### 🏗️ Architecture
Modular Monolith designed for high concurrency and fail-safe scheduling.
