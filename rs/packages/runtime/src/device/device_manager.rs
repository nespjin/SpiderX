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
use std::sync::{Arc, Mutex, OnceLock};

pub struct DeviceManager {
    /// The screen type in current device.
    screen_type: Option<ScreenType>,
}

impl DeviceManager {
    pub fn get_instance() -> &'static Mutex<DeviceManager> {
        static INSTANCE: OnceLock<Mutex<DeviceManager>> = OnceLock::new();
        INSTANCE.get_or_init(|| Mutex::new(DeviceManager { screen_type: None }))
    }

    pub fn set_screen_type(&mut self, screen_type: ScreenType) {
        self.screen_type = Some(screen_type);
    }

    pub fn screen_type(&self) -> Option<ScreenType> {
        self.screen_type.clone()
    }
}
