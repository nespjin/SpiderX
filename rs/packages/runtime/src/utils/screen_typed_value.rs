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

use core::data::screen_type::ScreenType;

pub struct ScreenTypedValue<T> {
    value: T,
    compact: Option<T>,
    medium: Option<T>,
    expanded: Option<T>,
}

impl<T> ScreenTypedValue<T> {
    pub fn new(value: T) -> Self {
        Self {
            value: value,
            compact: None,
            medium: None,
            expanded: None,
        }
    }

    pub fn with_compact(self, compact: T) -> Self {
        Self {
            compact: Some(compact),
            ..self
        }
    }

    pub fn with_medium(self, medium: T) -> Self {
        Self {
            medium: Some(medium),
            ..self
        }
    }

    pub fn with_expanded(self, expanded: T) -> Self {
        Self {
            expanded: Some(expanded),
            ..self
        }
    }

    pub fn value(&self, screen_type: ScreenType) -> &T {
        match screen_type {
            ScreenType::Compact => self.compact.as_ref(),
            ScreenType::Medium => self.medium.as_ref(),
            ScreenType::Expanded => self.expanded.as_ref(),
        }
        .unwrap_or(&self.value)
    }

    pub fn value_mut(&mut self, screen_type: ScreenType) -> &mut T {
        match screen_type {
            ScreenType::Compact => self.compact.as_mut(),
            ScreenType::Medium => self.medium.as_mut(),
            ScreenType::Expanded => self.expanded.as_mut(),
        }
        .unwrap_or(&mut self.value)
    }

    pub fn into_inner(self) -> T {
        self.value
    }
}
