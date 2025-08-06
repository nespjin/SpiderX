use std::hash::Hash;

// Copyright (c) 2025. NESP Technology Corporation. All rights reserved.
// 
// This program is not free software; you can't redistribute it and/or modify it
// without the permit of team manager.
// 
// Unless required by applicable law or agreed to in writing.
// 
// If you have any questions or if you find a bug,
// please contact the author by email or ask for Issues.
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
