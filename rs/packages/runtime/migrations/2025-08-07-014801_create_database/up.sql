CREATE TABLE IF NOT EXISTS "plugin" (
	"id" VARCHAR NOT NULL UNIQUE,
	"name" VARCHAR NOT NULL,
	"author" VARCHAR,
	"version" VARCHAR NOT NULL,
	"runtime_version" VARCHAR NOT NULL,
	"description" VARCHAR,
	"tags" VARCHAR,
	"supported_screen_types" VARCHAR,
	PRIMARY KEY("id")
);

CREATE INDEX IF NOT EXISTS "plugin_index_0"
ON "plugin" ("id");
CREATE TABLE IF NOT EXISTS "dataset" (
	"id" VARCHAR NOT NULL UNIQUE,
	"plugin_id" VARCHAR NOT NULL,
	"url" VARCHAR NOT NULL,
	"url_compact" VARCHAR,
	"url_medium" VARCHAR,
	"url_expanded" VARCHAR,
	"js" TEXT,
	"js_compact" TEXT,
	"js_medium" TEXT,
	"js_expanded" TEXT,
	"dsl_default" TEXT,
	"dsl_compact" TEXT,
	"dsl_medium" TEXT,
	"dsl_expanded" TEXT,
	PRIMARY KEY("id")
);

CREATE INDEX IF NOT EXISTS "dataset_index_0"
ON "dataset" ("id", "plugin_id");