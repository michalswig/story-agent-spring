# story-agent-spring

Tiny Spring Boot AI “story agent”. Send a few words → get a short children’s story.

- **API:** `POST /api/story` → `{ title, story, moral }`
- **Models:** stub (default) or OpenAI (activate with `llm` profile)
- **Guardrails:** includes required words, trims to `maxWords`, extracts a single `Moral:` line
- **UI:** simple one-page frontend at `/` (calls the API with `fetch`)

---

## Tech
Java 21 • Spring Boot 3.5 • Jackson • (optional) OpenAI Java SDK

---

## Quickstart

### Run (stub model, no cost)
```bash
mvn spring-boot:run
# open http://localhost:8080/
