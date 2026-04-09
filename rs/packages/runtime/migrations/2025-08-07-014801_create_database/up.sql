CREATE TABLE IF NOT EXISTS "plugin" (
   "id"                     varchar NOT NULL UNIQUE,
   "name"                   varchar NOT NULL,
   "author"                 varchar,
   "version"                varchar NOT NULL,
   "runtime_version"        varchar NOT NULL,
   "description"            varchar,
   "tags"                   varchar,
   "supported_screen_types" varchar,
   PRIMARY KEY ( "id" )
);

CREATE INDEX IF NOT EXISTS "plugin_index_0" ON
   "plugin" (
      "id"
   );
CREATE TABLE IF NOT EXISTS "dataset" (
   "id"           varchar NOT NULL,
   "plugin_id"    varchar NOT NULL,
   "url"          varchar NOT NULL,
   "url_compact"  varchar,
   "url_medium"   varchar,
   "url_expanded" varchar,
   "js"           text,
   "js_compact"   text,
   "js_medium"    text,
   "js_expanded"  text,
   "dsl_default"  text,
   "dsl_compact"  text,
   "dsl_medium"   text,
   "dsl_expanded" text,
   "next_dataset_id" text,
	PRIMARY KEY ( "id" , "plugin_id" )
);

CREATE INDEX IF NOT EXISTS "dataset_index_0" ON
   "dataset" (
      "id",
      "plugin_id"
   );