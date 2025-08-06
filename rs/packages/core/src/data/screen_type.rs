use std::hash::Hash;

// Copyright (c) 2025. NESP Technology Corporation.
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
#[derive(Debug, Clone)]
pub enum ScreenType {
    Compact,
    Medium,
    Expanded,
}

impl ScreenType {
    pub fn from_string(string: String) -> Result<ScreenType, String> {
        match string.to_lowercase().as_str() {
            "compact" => Ok(ScreenType::Compact),
            "medium" => Ok(ScreenType::Medium),
            "expanded" => Ok(ScreenType::Expanded),
            _ => Err(format!("Invalid screen type: {}", string)),
        }
    }
}

impl PartialEq for ScreenType {
    fn ne(&self, other: &Self) -> bool {
        !self.eq(other)
    }

    fn eq(&self, other: &Self) -> bool {
        core::mem::discriminant(self) == core::mem::discriminant(other)
    }
}

impl Eq for ScreenType {}

impl Hash for ScreenType {
    fn hash_slice<H: std::hash::Hasher>(data: &[Self], state: &mut H)
    where
        Self: Sized,
    {
        for piece in data {
            piece.hash(state)
        }
    }

    fn hash<H: std::hash::Hasher>(&self, state: &mut H) {
        core::mem::discriminant(self).hash(state);
    }
}
