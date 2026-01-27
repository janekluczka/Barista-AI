# Supabase Repository Plan

## 1. Repository List and Responsibilities
- `BrewMethodsRepository`: public catalog; list + get by id/slug.
- `GenerationRequestsRepository`: CRUD for user requests; list with filters.
- `RecipesRepository`: CRUD for user recipes; list + filter + similar recipes query.
- `RecipeActionLogsRepository`: create/list logs for recipe decisions.

## 2. Domain Interfaces (Kotlin)
- Method signatures with input/output types
- Error handling strategy

```kotlin
sealed interface RepositoryError {
    data class Unauthorized(val message: String) : RepositoryError
    data class NotFound(val message: String) : RepositoryError
    data class Validation(val message: String) : RepositoryError
    data class Network(val message: String) : RepositoryError
    data class Unknown(val message: String, val cause: Throwable? = null) : RepositoryError
}

sealed interface RepositoryResult<out T> {
    data class Success<T>(val value: T) : RepositoryResult<T>
    data class Failure(val error: RepositoryError) : RepositoryResult<Nothing>
}

data class PageRequest(val limit: Int, val offset: Int)
enum class SortDirection { ASC, DESC }

data class SortOption(
    val field: String,
    val direction: SortDirection
)

interface BrewMethodsRepository {
    suspend fun listBrewMethods(
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.ASC)
    ): RepositoryResult<List<BrewMethod>>
    suspend fun getBrewMethodById(id: String): RepositoryResult<BrewMethod>
    suspend fun getBrewMethodBySlug(slug: String): RepositoryResult<BrewMethod>
}

data class GenerationRequestFilter(
    val brewMethodId: String? = null,
    val createdAfterIso: String? = null,
    val createdBeforeIso: String? = null
)

interface GenerationRequestsRepository {
    suspend fun listGenerationRequests(
        filter: GenerationRequestFilter,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<GenerationRequest>>
    suspend fun getGenerationRequest(id: String): RepositoryResult<GenerationRequest>
    suspend fun createGenerationRequest(input: CreateGenerationRequest): RepositoryResult<GenerationRequest>
    suspend fun updateGenerationRequest(
        id: String,
        input: UpdateGenerationRequest
    ): RepositoryResult<GenerationRequest>
    suspend fun deleteGenerationRequest(id: String): RepositoryResult<Unit>
}

data class RecipeFilter(
    val brewMethodId: String? = null,
    val status: RecipeStatus? = null,
    val generationRequestId: String? = null,
    val createdAfterIso: String? = null,
    val createdBeforeIso: String? = null
)

data class SimilarRecipeQuery(
    val brewMethodId: String,
    val coffeeAmount: Double,
    val ratioCoffee: Int,
    val ratioWater: Int,
    val temperature: Int,
    val toleranceCoffee: Double = 2.0,
    val toleranceRatio: Int = 1,
    val toleranceTemperature: Int = 5
)

interface RecipesRepository {
    suspend fun listRecipes(
        filter: RecipeFilter,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<Recipe>>
    suspend fun getRecipe(id: String): RepositoryResult<Recipe>
    suspend fun createRecipe(input: CreateRecipe): RepositoryResult<Recipe>
    suspend fun updateRecipe(id: String, input: UpdateRecipe): RepositoryResult<Recipe>
    suspend fun deleteRecipe(id: String): RepositoryResult<Unit>
    suspend fun findSimilarRecipes(
        query: SimilarRecipeQuery,
        page: PageRequest
    ): RepositoryResult<List<Recipe>>
}

data class RecipeActionLogFilter(
    val recipeId: String? = null,
    val action: RecipeAction? = null
)

interface RecipeActionLogsRepository {
    suspend fun listRecipeActionLogs(
        filter: RecipeActionLogFilter,
        page: PageRequest,
        sort: SortOption = SortOption("created_at", SortDirection.DESC)
    ): RepositoryResult<List<RecipeActionLog>>
    suspend fun createRecipeActionLog(input: CreateRecipeActionLog): RepositoryResult<RecipeActionLog>
    suspend fun deleteRecipeActionLog(id: String): RepositoryResult<Unit>
}
```

## 3. Data Implementations (Kotlin)
- Supabase queries per method
- DTO/domain mapping notes

**Mapping**
- DTOs in `data/models` map to domain models via mappers.
- Validate numeric constraints before insert/update to avoid server errors.
- `user_id` is always taken from current session to satisfy RLS.

**Query Examples (PostgREST via Supabase Kotlin SDK)**

- `BrewMethodsRepositoryImpl.listBrewMethods`
  - `from("brew_methods").select().order("created_at").range(offset, offset + limit - 1)`
- `BrewMethodsRepositoryImpl.getBrewMethodBySlug`
  - `from("brew_methods").select().eq("slug", slug).single()`

- `GenerationRequestsRepositoryImpl.listGenerationRequests`
  - `from("generation_requests").select().eq("user_id", currentUserId)`
  - optional filters: `eq("brew_method_id", ...)`, `gte("created_at", ...)`, `lte("created_at", ...)`
  - `order("created_at", descending = true)` + `range(...)`

- `GenerationRequestsRepositoryImpl.createGenerationRequest`
  - `insert(mapOf("user_id" to currentUserId, ...))`
  - `select()` to return inserted row.

- `RecipesRepositoryImpl.listRecipes`
  - `from("recipes").select().eq("user_id", currentUserId)`
  - optional filters: `eq("status", ...)`, `eq("brew_method_id", ...)`, `eq("generation_request_id", ...)`
  - `order("created_at", descending = true)` + `range(...)`

- `RecipesRepositoryImpl.findSimilarRecipes`
  - `from("recipes").select().eq("user_id", currentUserId)`
  - `eq("brew_method_id", query.brewMethodId)`
  - `gte("coffee_amount", query.coffeeAmount - query.toleranceCoffee)`
  - `lte("coffee_amount", query.coffeeAmount + query.toleranceCoffee)`
  - `gte("ratio_coffee", query.ratioCoffee - query.toleranceRatio)`
  - `lte("ratio_coffee", query.ratioCoffee + query.toleranceRatio)`
  - `gte("ratio_water", query.ratioWater - query.toleranceRatio)`
  - `lte("ratio_water", query.ratioWater + query.toleranceRatio)`
  - `gte("temperature", query.temperature - query.toleranceTemperature)`
  - `lte("temperature", query.temperature + query.toleranceTemperature)`
  - `order("created_at", descending = true)` + `range(...)`

- `RecipeActionLogsRepositoryImpl.createRecipeActionLog`
  - `insert(mapOf("user_id" to currentUserId, ...))`
  - `select()` to return inserted row.

## 4. Data Sources and DI
- Proposed data sources or gateways
- Hilt bindings

**Data Source**
```kotlin
interface SupabaseDataSource {
    val client: SupabaseClient
    suspend fun currentUserId(): String
}
```

**Implementation Notes**
- `currentUserId()` reads from Supabase auth session and throws `RepositoryError.Unauthorized` if missing.
- Repositories depend on `SupabaseDataSource` + mappers.

**Hilt**
- Bind `SupabaseDataSourceImpl` as singleton.
- Bind repository implementations to domain interfaces:
  - `@Binds fun bindRecipesRepository(impl: RecipesRepositoryImpl): RecipesRepository`

## 5. Edge Cases and Validation
- Constraints, RLS, pagination, sorting
- Enforce numeric constraints before writes:
  - `coffee_amount >= 0`, `water_amount >= 0`, `ratio_* >= 1`, `temperature 0..100`
- RLS: all user-owned operations must use `currentUserId` and include `user_id` on insert.
- `generation_request_id` is optional for manual recipes; avoid inserting empty string.
- Pagination: guard `limit` (e.g., 1..100); handle empty pages gracefully.
- Sorting: only allow known fields (`created_at`, `updated_at`) to prevent invalid queries.
- Update flows should set `updated_at` on `recipes` to `now()`.

## 6. Package Structure
```
app/src/main/java/com/luczka/baristaai/
  domain/
    model/
      BrewMethod.kt
      GenerationRequest.kt
      Recipe.kt
      RecipeActionLog.kt
      RecipeStatus.kt
      RecipeAction.kt
    repository/
      BrewMethodsRepository.kt
      GenerationRequestsRepository.kt
      RecipesRepository.kt
      RecipeActionLogsRepository.kt
    error/
      RepositoryError.kt
      RepositoryResult.kt
  data/
    models/ (DTOs)
      BrewMethodDto.kt
      GenerationRequestDto.kt
      RecipeDto.kt
      RecipeActionLogDto.kt
    mapper/
      BrewMethodMapper.kt
      GenerationRequestMapper.kt
      RecipeMapper.kt
      RecipeActionLogMapper.kt
    datasource/
      SupabaseDataSource.kt
      SupabaseDataSourceImpl.kt
    repository/
      BrewMethodsRepositoryImpl.kt
      GenerationRequestsRepositoryImpl.kt
      RecipesRepositoryImpl.kt
      RecipeActionLogsRepositoryImpl.kt
```
