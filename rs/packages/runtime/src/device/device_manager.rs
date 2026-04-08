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

use spiderx_core::data::screen_type::ScreenType;
use std::sync::{Arc, OnceLock, RwLock};

pub struct DeviceManager {
    /// The screen type in current device.
    screen_type: Arc<RwLock<Option<ScreenType>>>,
}

impl DeviceManager {
    pub fn get_instance() -> &'static DeviceManager {
        static INSTANCE: OnceLock<DeviceManager> = OnceLock::new();
        INSTANCE.get_or_init(|| DeviceManager {
            screen_type: Arc::new(RwLock::new(None)),
        })
    }

    pub fn set_screen_type(&self, screen_type: ScreenType) {
        self.screen_type.write().unwrap().replace(screen_type);
    }

    pub fn screen_type(&self) -> Option<ScreenType> {
        self.screen_type.read().unwrap().clone()
    }
}
