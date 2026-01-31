import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
};

type GenerateRecipesFromRequestPayload = {
  generation_request_id: string;
};

type OpenRouterRecipe = {
  coffee_amount: number;
  water_amount: number;
  ratio_coffee: number;
  ratio_water: number;
  temperature: number;
  assistant_tip?: string | null;
};

type OpenRouterResponse = {
  recipes: OpenRouterRecipe[];
};

const OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions";

async function fetchWithTimeout(input: RequestInfo, init: RequestInit, timeoutMs: number) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs);
  try {
    return await fetch(input, { ...init, signal: controller.signal });
  } finally {
    clearTimeout(timeoutId);
  }
}

function parseJsonSafely(value: string): OpenRouterResponse | null {
  try {
    const trimmed = value.trim();
    const withoutFence = trimmed
      .replace(/^```json\s*/i, "")
      .replace(/^```\s*/i, "")
      .replace(/```\s*$/i, "")
      .trim();
    return JSON.parse(withoutFence) as OpenRouterResponse;
  } catch {
    return null;
  }
}

function isValidNumber(value: unknown): value is number {
  return typeof value === "number" && Number.isFinite(value);
}

function normalizeRecipe(
  recipe: OpenRouterRecipe,
  canAdjustTemperature: boolean,
): OpenRouterRecipe | null {
  if (
    !isValidNumber(recipe.coffee_amount) ||
    !isValidNumber(recipe.water_amount) ||
    !isValidNumber(recipe.ratio_coffee) ||
    !isValidNumber(recipe.ratio_water) ||
    !isValidNumber(recipe.temperature)
  ) {
    return null;
  }

  const normalized: OpenRouterRecipe = {
    coffee_amount: Number(recipe.coffee_amount),
    water_amount: Number(recipe.water_amount),
    ratio_coffee: Math.trunc(recipe.ratio_coffee),
    ratio_water: Math.trunc(recipe.ratio_water),
    temperature: Math.trunc(recipe.temperature),
    assistant_tip: recipe.assistant_tip ?? null,
  };

  if (!canAdjustTemperature) {
    normalized.temperature = 100;
  }

  if (normalized.coffee_amount < 0 || normalized.water_amount < 0) {
    return null;
  }

  if (normalized.ratio_coffee < 1 || normalized.ratio_water < 1) {
    return null;
  }

  if (normalized.temperature < 0 || normalized.temperature > 100) {
    return null;
  }

  return normalized;
}

function hasDistinctRecipes(recipes: OpenRouterRecipe[]): boolean {
  const signatures = new Set(
    recipes.map(
      (recipe) =>
        `${recipe.coffee_amount}-${recipe.water_amount}-${recipe.ratio_coffee}-${recipe.ratio_water}-${recipe.temperature}`,
    ),
  );

  return signatures.size === recipes.length;
}

function buildPrompt(params: {
  brewMethodName: string;
  coffeeAmount: number;
  canAdjustTemperature: boolean;
  userComment?: string | null;
}): string {
  const temperatureRule = params.canAdjustTemperature
    ? "Użytkownik może regulować temperaturę."
    : "Użytkownik NIE może regulować temperatury. Ustaw temperaturę 100°C.";

  const comment = params.userComment
    ? `Dodatkowy komentarz użytkownika: ${params.userComment}`
    : "Brak dodatkowego komentarza.";

  return [
    "Wygeneruj DOKŁADNIE 3 przepisy na kawę w formacie JSON.",
    "Pierwszy to bazowy, dwa pozostałe to wyraźnie różne alternatywy.",
    "Każda alternatywa musi się różnić co najmniej jednym parametrem.",
    "Zwróć tylko JSON bez żadnego tekstu dodatkowego.",
    "",
    "Wymagany format:",
    `{"recipes":[{"coffee_amount":18,"water_amount":300,"ratio_coffee":1,"ratio_water":16,"temperature":92,"assistant_tip":"..."}]}`,
    "",
    `Metoda parzenia: ${params.brewMethodName}`,
    `Waga kawy (g): ${params.coffeeAmount}`,
    temperatureRule,
    comment,
  ].join("\n");
}

async function callOpenRouter(params: {
  apiKey: string;
  model: string;
  prompt: string;
  referer?: string;
  title?: string;
  timeoutMs: number;
}): Promise<OpenRouterResponse> {
  const headers: Record<string, string> = {
    Authorization: `Bearer ${params.apiKey}`,
    "Content-Type": "application/json",
  };

  if (params.referer) {
    headers["HTTP-Referer"] = params.referer;
  }

  if (params.title) {
    headers["X-Title"] = params.title;
  }

  const response = await fetchWithTimeout(
    OPENROUTER_URL,
    {
    method: "POST",
    headers,
    body: JSON.stringify({
      model: params.model,
      messages: [
        {
          role: "system",
          content:
            "Jesteś asystentem baristy. Odpowiadasz wyłącznie JSONem zgodnym ze specyfikacją.",
        },
        { role: "user", content: params.prompt },
      ],
      temperature: 0.6,
      }),
    },
    params.timeoutMs,
  );

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(`OpenRouter error: ${response.status} ${errorBody}`);
  }

  const payload = await response.json();
  const content = payload?.choices?.[0]?.message?.content;
  if (typeof content !== "string") {
    throw new Error("OpenRouter response missing content.");
  }

  const parsed = parseJsonSafely(content);
  if (!parsed) {
    throw new Error("OpenRouter returned invalid JSON.");
  }

  return parsed;
}

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const supabaseUrl = Deno.env.get("SUPABASE_URL") ?? "";
    const supabaseAnonKey = Deno.env.get("SUPABASE_ANON_KEY") ?? "";
    const supabaseServiceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
    const openRouterApiKey = Deno.env.get("OPENROUTER_API_KEY") ?? "";
    const openRouterModel = Deno.env.get("OPENROUTER_MODEL") ?? "openrouter/auto";
    const openRouterReferer = Deno.env.get("OPENROUTER_REFERER") ?? "";
    const openRouterTitle = Deno.env.get("OPENROUTER_TITLE") ?? "BaristaAI";
    const openRouterTimeoutMs = Number(
      Deno.env.get("OPENROUTER_TIMEOUT_MS") ?? "10000",
    );

    if (!supabaseUrl || !supabaseAnonKey || !supabaseServiceRoleKey) {
      return new Response(
        JSON.stringify({ error: "Missing Supabase environment variables." }),
        { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    if (!openRouterApiKey) {
      return new Response(
        JSON.stringify({ error: "Missing OPENROUTER_API_KEY." }),
        { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const authHeader = request.headers.get("Authorization") ?? "";
    if (!authHeader) {
      return new Response(
        JSON.stringify({ error: "Missing Authorization header." }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const authClient = createClient(supabaseUrl, supabaseAnonKey, {
      global: { headers: { Authorization: authHeader } },
    });
    const { data: authData, error: authError } = await authClient.auth.getUser();
    if (authError || !authData?.user) {
      return new Response(
        JSON.stringify({ error: "Invalid or expired token." }),
        { status: 401, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const payload = (await request.json()) as GenerateRecipesFromRequestPayload;
    if (!payload?.generation_request_id) {
      return new Response(
        JSON.stringify({ error: "Missing generation_request_id." }),
        { status: 400, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const adminClient = createClient(supabaseUrl, supabaseServiceRoleKey);

    const { data: generationRequest, error: requestError } = await adminClient
      .from("generation_requests")
      .select(
        "id, user_id, brew_method_id, coffee_amount, can_adjust_temperature, user_comment, created_at",
      )
      .eq("id", payload.generation_request_id)
      .maybeSingle();

    if (requestError || !generationRequest) {
      return new Response(
        JSON.stringify({ error: "Generation request not found." }),
        { status: 404, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    if (generationRequest.user_id !== authData.user.id) {
      return new Response(
        JSON.stringify({ error: "Forbidden." }),
        { status: 403, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const { data: brewMethod, error: brewMethodError } = await adminClient
      .from("brew_methods")
      .select("id, name")
      .eq("id", generationRequest.brew_method_id)
      .maybeSingle();

    if (brewMethodError || !brewMethod) {
      return new Response(
        JSON.stringify({ error: "Brew method not found." }),
        { status: 404, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    const prompt = buildPrompt({
      brewMethodName: brewMethod.name,
      coffeeAmount: generationRequest.coffee_amount,
      canAdjustTemperature: generationRequest.can_adjust_temperature,
      userComment: generationRequest.user_comment ?? null,
    });

    let openRouterPayload = await callOpenRouter({
      apiKey: openRouterApiKey,
      model: openRouterModel,
      prompt,
      referer: openRouterReferer || undefined,
      title: openRouterTitle || undefined,
      timeoutMs: openRouterTimeoutMs,
    });

    if (!openRouterPayload?.recipes || openRouterPayload.recipes.length !== 3) {
      return new Response(
        JSON.stringify({ error: "OpenRouter returned invalid recipes count." }),
        { status: 422, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    let normalizedRecipes = openRouterPayload.recipes
      .map((recipe) => normalizeRecipe(recipe, generationRequest.can_adjust_temperature))
      .filter((recipe): recipe is OpenRouterRecipe => recipe !== null);

    if (normalizedRecipes.length !== 3) {
      return new Response(
        JSON.stringify({ error: "OpenRouter returned invalid recipe values." }),
        { status: 422, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    if (!hasDistinctRecipes(normalizedRecipes)) {
      openRouterPayload = await callOpenRouter({
        apiKey: openRouterApiKey,
        model: openRouterModel,
        prompt: `${prompt}\n\nUpewnij się, że alternatywy różnią się parametrami.`,
        referer: openRouterReferer || undefined,
        title: openRouterTitle || undefined,
        timeoutMs: openRouterTimeoutMs,
      });

      if (!openRouterPayload?.recipes || openRouterPayload.recipes.length !== 3) {
        return new Response(
          JSON.stringify({ error: "OpenRouter retry returned invalid recipes." }),
          { status: 422, headers: { ...corsHeaders, "Content-Type": "application/json" } },
        );
      }

      normalizedRecipes = openRouterPayload.recipes
        .map((recipe) => normalizeRecipe(recipe, generationRequest.can_adjust_temperature))
        .filter((recipe): recipe is OpenRouterRecipe => recipe !== null);

      if (normalizedRecipes.length !== 3 || !hasDistinctRecipes(normalizedRecipes)) {
        return new Response(
          JSON.stringify({ error: "Recipes are not sufficiently distinct." }),
          { status: 422, headers: { ...corsHeaders, "Content-Type": "application/json" } },
        );
      }
    }

    const { data: insertedRecipes, error: insertRecipesError } = await adminClient
      .from("recipes")
      .insert(
        normalizedRecipes.map((recipe) => ({
          user_id: authData.user.id,
          generation_request_id: generationRequest.id,
          brew_method_id: generationRequest.brew_method_id,
          coffee_amount: recipe.coffee_amount,
          water_amount: recipe.water_amount,
          ratio_coffee: recipe.ratio_coffee,
          ratio_water: recipe.ratio_water,
          temperature: recipe.temperature,
          assistant_tip: recipe.assistant_tip ?? null,
          status: "draft",
        })),
      )
      .select(
        "id, user_id, generation_request_id, brew_method_id, coffee_amount, water_amount, ratio_coffee, ratio_water, temperature, assistant_tip, status, created_at, updated_at",
      );

    if (insertRecipesError || !insertedRecipes) {
      return new Response(
        JSON.stringify({ error: "Failed to persist recipes." }),
        { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } },
      );
    }

    return new Response(
      JSON.stringify({
        generation_request: generationRequest,
        recipes: insertedRecipes,
      }),
      { status: 200, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    );
  } catch (error) {
    return new Response(
      JSON.stringify({ error: "Unhandled error.", details: `${error}` }),
      { status: 500, headers: { ...corsHeaders, "Content-Type": "application/json" } },
    );
  }
});
