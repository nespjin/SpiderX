// Copyright (c) 2026. NESP Technology Corporation.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

use serde_json::{Map, Value};
use std::collections::HashSet;

/// Strategy for merging primitive values (String, Number, Bool, Null) when both exist.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum PrimitiveStrategy {
    /// Replace the first value with the second one.
    Replace,
    /// Keep the first value and ignore the second one.
    Skip,
}

/// Strategy for merging arrays when both exist.
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum ArrayStrategy {
    /// Replace the first array entirely with the second array.
    Replace,
    /// Merge arrays by concatenating and removing duplicate elements (based on equality).
    MergeDeduplicate,
}

/// Error type for merge failures.
#[derive(Debug, PartialEq)]
pub enum JsonMergeError {
    TypeMismatch {
        expected: &'static str,
        found: &'static str,
    },
}

impl std::fmt::Display for JsonMergeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            JsonMergeError::TypeMismatch { expected, found } => {
                write!(f, "Type mismatch: expected {}, found {}", expected, found)
            }
        }
    }
}

impl std::error::Error for JsonMergeError {}

/// Merge two JSON values according to the specified strategies.
/// Returns a new `Value`; original values are not modified.
///
/// Rules:
/// - If both values exist and have the same type:
///   - For `Object`: recursively merge each key.
///   - For `Array`: use `array_strategy` (Replace or MergeDeduplicate).
///   - For primitive types (String, Number, Bool, Null): use `primitive_strategy` (Replace or Skip).
/// - If types differ (e.g., Object vs Array): returns `MergeError::TypeMismatch`.
/// - If only one value exists (handled implicitly via object field presence), that value is used.
pub fn merge_values(
    a: &Value,
    b: &Value,
    primitive_strategy: PrimitiveStrategy,
    array_strategy: ArrayStrategy,
) -> Result<Value, JsonMergeError> {
    match (a, b) {
        // Both are objects: recursively merge fields
        (Value::Object(a_map), Value::Object(b_map)) => {
            let mut result = Map::new();
            // Collect all keys from both objects
            let all_keys: HashSet<&String> = a_map.keys().chain(b_map.keys()).collect();
            for key in all_keys {
                match (a_map.get(key), b_map.get(key)) {
                    (Some(a_val), Some(b_val)) => {
                        // Both exist: recursive merge
                        let merged =
                            merge_values(a_val, b_val, primitive_strategy, array_strategy)?;
                        result.insert(key.clone(), merged);
                    }
                    (Some(a_val), None) => {
                        // Only in a
                        result.insert(key.clone(), a_val.clone());
                    }
                    (None, Some(b_val)) => {
                        // Only in b
                        result.insert(key.clone(), b_val.clone());
                    }
                    (None, None) => unreachable!(),
                }
            }
            Ok(Value::Object(result))
        }

        // Both are arrays
        (Value::Array(a_arr), Value::Array(b_arr)) => match array_strategy {
            ArrayStrategy::Replace => Ok(b.clone()),
            ArrayStrategy::MergeDeduplicate => {
                let mut merged = a_arr.clone();
                for item in b_arr {
                    if !merged.contains(item) {
                        merged.push(item.clone());
                    }
                }
                Ok(Value::Array(merged))
            }
        },

        // Both are strings
        (Value::String(a_str), Value::String(b_str)) => match primitive_strategy {
            PrimitiveStrategy::Replace => Ok(Value::String(b_str.clone())),
            PrimitiveStrategy::Skip => Ok(Value::String(a_str.clone())),
        },

        // Both are numbers
        (Value::Number(a_num), Value::Number(b_num)) => match primitive_strategy {
            PrimitiveStrategy::Replace => Ok(Value::Number(b_num.clone())),
            PrimitiveStrategy::Skip => Ok(Value::Number(a_num.clone())),
        },

        // Both are booleans
        (Value::Bool(a_bool), Value::Bool(b_bool)) => match primitive_strategy {
            PrimitiveStrategy::Replace => Ok(Value::Bool(*b_bool)),
            PrimitiveStrategy::Skip => Ok(Value::Bool(*a_bool)),
        },

        // Both are null
        (Value::Null, Value::Null) => Ok(Value::Null),

        // Type mismatch
        (a_val, b_val) => Err(JsonMergeError::TypeMismatch {
            expected: a_val.type_str(),
            found: b_val.type_str(),
        }),
    }
}

// Helper to assert merge result with Replace strategy
pub fn merge_replace(a: &Value, b: &Value) -> Result<Value, JsonMergeError> {
    merge_values(a, b, PrimitiveStrategy::Replace, ArrayStrategy::Replace)
}

pub fn merge_deduplicate(a: &Value, b: &Value) -> Result<Value, JsonMergeError> {
    merge_values(
        a,
        b,
        PrimitiveStrategy::Replace,
        ArrayStrategy::MergeDeduplicate,
    )
}

pub fn merge_skip_primitive(a: &Value, b: &Value) -> Result<Value, JsonMergeError> {
    merge_values(a, b, PrimitiveStrategy::Skip, ArrayStrategy::Replace)
}

// Helper trait to get type name as &'static str
trait ValueType {
    fn type_str(&self) -> &'static str;
}

impl ValueType for Value {
    fn type_str(&self) -> &'static str {
        match self {
            Value::Null => "null",
            Value::Bool(_) => "boolean",
            Value::Number(_) => "number",
            Value::String(_) => "string",
            Value::Array(_) => "array",
            Value::Object(_) => "object",
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    #[test]
    fn test_merge_objects_basic_replace() {
        let a = json!({"a": 1, "b": 2});
        let b = json!({"b": 3, "c": 4});
        let result = merge_replace(&a, &b).unwrap();
        let expected = json!({"a": 1, "b": 3, "c": 4});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_objects_nested() {
        let a = json!({
            "user": {
                "name": "Alice",
                "age": 30,
                "address": {"city": "NYC"}
            }
        });
        let b = json!({
            "user": {
                "age": 31,
                "address": {"zip": 10001}
            }
        });
        let result = merge_replace(&a, &b).unwrap();
        let expected = json!({
            "user": {
                "name": "Alice",
                "age": 31,
                "address": {"city": "NYC", "zip": 10001}
            }
        });
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_primitive_replace() {
        let a = json!({"str": "hello", "num": 42, "bool": true, "null": null});
        let b = json!({"str": "world", "num": 99, "bool": false, "null": null});
        let result = merge_replace(&a, &b).unwrap();
        let expected = json!({"str": "world", "num": 99, "bool": false, "null": null});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_primitive_skip() {
        let a = json!({"str": "hello", "num": 42, "bool": true});
        let b = json!({"str": "world", "num": 99, "bool": false});
        let result = merge_skip_primitive(&a, &b).unwrap();
        let expected = json!({"str": "hello", "num": 42, "bool": true});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_array_replace() {
        let a = json!({"items": [1, 2, 3]});
        let b = json!({"items": [4, 5]});
        let result = merge_replace(&a, &b).unwrap();
        let expected = json!({"items": [4, 5]});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_array_deduplicate() {
        let a = json!({"tags": ["rust", "serde", "json"]});
        let b = json!({"tags": ["serde", "json", "tokio"]});
        let result = merge_deduplicate(&a, &b).unwrap();
        let expected = json!({"tags": ["rust", "serde", "json", "tokio"]});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_array_deduplicate_order_preserved() {
        let a = json!({"a": [1, 2, 3]});
        let b = json!({"a": [3, 4, 1, 5]});
        let result = merge_deduplicate(&a, &b).unwrap();
        // a's order preserved, then b's unique items in order of appearance
        let expected = json!({"a": [1, 2, 3, 4, 5]});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_empty_objects() {
        let a = json!({});
        let b = json!({"x": 1});
        assert_eq!(merge_replace(&a, &b).unwrap(), json!({"x": 1}));
        assert_eq!(merge_replace(&b, &a).unwrap(), json!({"x": 1}));
    }

    #[test]
    fn test_merge_empty_arrays() {
        let a = json!({"arr": []});
        let b = json!({"arr": [1, 2]});
        let result = merge_deduplicate(&a, &b).unwrap();
        assert_eq!(result, json!({"arr": [1, 2]}));

        let result = merge_replace(&a, &b).unwrap();
        assert_eq!(result, json!({"arr": [1, 2]}));
    }

    #[test]
    fn test_merge_arrays_with_objects() {
        let a = json!({"users": [{"id": 1, "name": "Alice"}]});
        let b = json!({"users": [{"id": 2, "name": "Bob"}, {"id": 1, "name": "Alice"}]});
        let result = merge_deduplicate(&a, &b).unwrap();
        let expected = json!({"users": [
            {"id": 1, "name": "Alice"},
            {"id": 2, "name": "Bob"}
        ]});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_nested_arrays_with_deduplicate() {
        let a = json!({
            "data": {
                "values": [10, 20]
            }
        });
        let b = json!({
            "data": {
                "values": [20, 30, 40]
            }
        });
        let result = merge_values(
            &a,
            &b,
            PrimitiveStrategy::Replace,
            ArrayStrategy::MergeDeduplicate,
        )
        .unwrap();
        let expected = json!({
            "data": {
                "values": [10, 20, 30, 40]
            }
        });
        assert_eq!(result, expected);
    }

    #[test]
    fn test_type_mismatch_object_array() {
        let a = json!({"key": "value"});
        let b = json!({"key": [1, 2, 3]});
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "string",
                found: "array"
            }
        ));
    }

    #[test]
    fn test_type_mismatch_array_object() {
        let a = json!({"list": [1, 2]});
        let b = json!({"list": {"a": 1}});
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "array",
                found: "object"
            }
        ));
    }

    #[test]
    fn test_type_mismatch_string_number() {
        let a = json!({"age": "thirty"});
        let b = json!({"age": 30});
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "string",
                found: "number"
            }
        ));
    }

    #[test]
    fn test_type_mismatch_bool_null() {
        let a = json!({"flag": true});
        let b = json!({"flag": null});
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "boolean",
                found: "null"
            }
        ));
    }

    #[test]
    fn test_type_mismatch_nested() {
        let a = json!({"a": {"b": 123}});
        let b = json!({"a": {"b": "string"}});
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "number",
                found: "string"
            }
        ));
    }

    #[test]
    fn test_merge_deep_nested_with_different_strategies() {
        let a = json!({
            "level1": {
                "level2": {
                    "num": 100,
                    "arr": [1, 2],
                    "flag": true
                }
            }
        });
        let b = json!({
            "level1": {
                "level2": {
                    "num": 200,
                    "arr": [2, 3, 4],
                    "flag": false
                }
            }
        });
        // Replace primitives, deduplicate arrays
        let result = merge_values(
            &a,
            &b,
            PrimitiveStrategy::Replace,
            ArrayStrategy::MergeDeduplicate,
        )
        .unwrap();
        let expected = json!({
            "level1": {
                "level2": {
                    "num": 200,
                    "arr": [1, 2, 3, 4],
                    "flag": false
                }
            }
        });
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_only_one_value_present() {
        let a = json!({"only_a": 1});
        let b = json!({});
        assert_eq!(merge_replace(&a, &b).unwrap(), json!({"only_a": 1}));
        assert_eq!(merge_replace(&b, &a).unwrap(), json!({"only_a": 1}));
    }

    #[test]
    fn test_merge_with_null_values() {
        let a = json!({"field": null});
        let b = json!({"field": "value"});
        // Null vs string -> type mismatch (null is a distinct type)
        let err = merge_replace(&a, &b).unwrap_err();
        assert!(matches!(
            err,
            JsonMergeError::TypeMismatch {
                expected: "null",
                found: "string"
            }
        ));
    }

    #[test]
    fn test_merge_both_null_same_type() {
        let a = json!({"field": null});
        let b = json!({"field": null});
        let result = merge_replace(&a, &b).unwrap();
        assert_eq!(result, json!({"field": null}));
    }

    #[test]
    fn test_merge_primitive_skip_with_array_replace() {
        let a = json!({"count": 5, "list": [1, 2]});
        let b = json!({"count": 10, "list": [3, 4]});
        let result = merge_values(&a, &b, PrimitiveStrategy::Skip, ArrayStrategy::Replace).unwrap();
        let expected = json!({"count": 5, "list": [3, 4]});
        assert_eq!(result, expected);
    }

    #[test]
    fn test_merge_identical_values() {
        let a = json!({"a": 1, "b": "x"});
        let b = json!({"a": 1, "b": "x"});
        let result = merge_replace(&a, &b).unwrap();
        assert_eq!(result, a);
    }

    #[test]
    fn test_merge_very_nested() {
        let a = json!({
            "a": {
                "b": {
                    "c": {
                        "d": 1
                    }
                }
            }
        });
        let b = json!({
            "a": {
                "b": {
                    "c": {
                        "e": 2
                    }
                }
            }
        });
        let result = merge_replace(&a, &b).unwrap();
        let expected = json!({
            "a": {
                "b": {
                    "c": {
                        "d": 1,
                        "e": 2
                    }
                }
            }
        });
        assert_eq!(result, expected);
    }
}
