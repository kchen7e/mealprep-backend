# MealPrep Backend

Spring Boot 3.5.3 REST API for the MealPrep application — a weekly meal planner that generates shopping lists tailored to local supermarkets.

**Version:** 0.8.0 | **Java:** 21 | **Database:** PostgreSQL 15 | **Object Storage:** MinIO (S3-compatible)

## Overview

The backend handles user authentication (JWT), recipe management with ingredient/unit tracking, and shopping list aggregation. Recipe images are stored in MinIO and served through a proxy endpoint. Ingredient units support mass (mg/g/kg) and volume (ml/l/gal/c/pt/qt/tsp/tbsp) with automatic conversion when combining.

## Quick Start

**Prerequisites:** Java 21, PostgreSQL 15, MinIO

```bash
# 1. Ensure PostgreSQL is running with a mealprep database
createdb mealprep

# 2. Ensure MinIO is running on localhost:9000 (default creds: minioadmin/minioadmin)

# 3. Start with local dev profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

The server starts on `http://localhost:8080`. Tables are auto-created via Hibernate `ddl-auto=update`.

## Configuration

| File | Purpose |
|---|---|
| `src/main/resources/application.properties` | Base config, reads env vars for secrets |
| `src/main/resources/application-local.properties` | Local dev overrides (hardcoded creds, localhost) |

### Key Environment Variables

| Variable | Default (local) | Description |
|---|---|---|
| `DB_HOSTNAME` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_NAME` | `mealprep` | Database name |
| `DB_USER` | `app` | Database user |
| `DB_PASSWORD` | `app` | Database password |
| `MINIO_ENDPOINT` | `http://localhost:9000` | MinIO S3 endpoint |
| `MINIO_ACCESS_KEY` | `minioadmin` | MinIO access key |
| `MINIO_SECRET_KEY` | `minioadmin` | MinIO secret key |
| `JWT_SECRET` | (dev key) | HMAC-SHA signing secret |
| `JWT_EXPIRY_MS` | `31536000000` | Token expiry (1 year default) |

## API Endpoints

### Users (`/api/user`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register` | No | Register user, returns JWT |
| POST | `/get` | No | Authenticate, returns JWT |
| PATCH | `/update` | Yes | Update profile fields |
| POST | `/logout` | No | Logout (stateless, no-op) |

### Recipes (`/api/recipe`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register` | Yes | Register/update a recipe |
| POST | `/get` | Yes | Get recipe by name |
| GET | `/get/all` | No | Get all recipes |
| PATCH | `/ingredients` | Yes | Update recipe ingredients |
| PATCH | `/seasonality` | Yes | Update recipe seasonality |
| PATCH | `/type` | Yes | Update meal type |
| POST | `/{name}/image` | Yes | Upload recipe image to MinIO |
| GET | `/{name}/image` | No | Serve recipe image from MinIO |

### Shopping List (`/api/shopping`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/get` | No | Aggregate ingredients from week's meals |

### Ingredients (`/api/ingredient`)

| Method | Path | Auth | Description |
|---|---|---|---|
| POST | `/register` | Yes | Register ingredient |
| POST | `/get` | No | Get ingredient by name |
| PATCH | `/dependencies` | Yes | Update ingredient dependencies |
| PATCH | `/seasonality` | Yes | Update ingredient seasonality |

### Food & Nutrients (`/api/food`, `/api/nutrient`)

CRUD endpoints for food items (with commercial forms) and nutrient lookup table.

## Project Structure

```
src/main/java/com/mealprep/MealPrep/
  MealPrepApplication.java       # Entry point
  config/                        # Security, JWT, MinIO, CORS
  controllers/                   # REST controllers
  service/                       # Business logic with retry/transaction handling
  database/                      # Spring Data JPA repositories
  entities/
    recipe/                      # Recipe, RecipeIngredient (@Embeddable)
    user/                        # User, UserCredentials
    calendar/                    # Day, Week
    market/                      # Food, Meat, Vegetable, CommercialForm
    nutrient/                    # Nutrient
    api/                         # DTOs for request/response
  measures/                      # Unit (@Embeddable) with mass/volume conversion
  utilities/                     # Argon2 password encoder
```

## Key Design Decisions

- **Stateless JWT auth** — no server-side sessions, tokens signed with HMAC-SHA
- **Retry with exponential backoff** — all write operations retry on lock contention (up to 5 attempts, 200ms–2s range)
- **Sorted ingredient inserts** — prevents PostgreSQL circular deadlocks under concurrent writes
- **Idempotent recipe registration** — registering an existing recipe updates its data instead of failing
- **String-based enum persistence** — `@Enumerated(EnumType.STRING)` for robust storage
- **MinIO image proxy** — images stored in MinIO, served through backend (no direct S3 access from frontend)

