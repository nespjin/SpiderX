// @generated automatically by Diesel CLI.

diesel::table! {
    dataset (id, plugin_id) {
        id -> Text,
        plugin_id -> Text,
        url -> Nullable<Text>,
        url_compact -> Nullable<Text>,
        url_medium -> Nullable<Text>,
        url_expanded -> Nullable<Text>,
        js -> Nullable<Text>,
        js_compact -> Nullable<Text>,
        js_medium -> Nullable<Text>,
        js_expanded -> Nullable<Text>,
        dsl_default -> Nullable<Text>,
        dsl_compact -> Nullable<Text>,
        dsl_medium -> Nullable<Text>,
        dsl_expanded -> Nullable<Text>,
        next_dataset_id -> Nullable<Text>,
    }
}

diesel::table! {
    plugin (id) {
        id -> Text,
        name -> Text,
        author -> Nullable<Text>,
        version -> Text,
        runtime_version -> Text,
        description -> Nullable<Text>,
        tags -> Nullable<Text>,
        supported_screen_types -> Nullable<Text>,
    }
}

diesel::allow_tables_to_appear_in_same_query!(dataset, plugin,);
