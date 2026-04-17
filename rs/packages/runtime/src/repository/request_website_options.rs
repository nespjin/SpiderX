use crate::{
    constants::DEFAULT_REQUEST_TIMEOUT,
    executor::{
        request_dataset_listener::RequestJavaScriptDatasetListenerArc,
        request_javascript_dataset_config::RequestJavaScriptDatasetConfigArc,
    },
};

pub struct RequestWebsiteOptions {
    pub timeout: u16,
    pub pre_execute_javascript: Option<String>,
    pub post_execute_javascript: Option<String>,
    pub listener: Option<RequestJavaScriptDatasetListenerArc>,
    pub config: Option<RequestJavaScriptDatasetConfigArc>,
}
impl RequestWebsiteOptions {
    pub fn new() -> Self {
        Self {
            timeout: DEFAULT_REQUEST_TIMEOUT,
            pre_execute_javascript: None,
            post_execute_javascript: None,
            listener: None,
            config: None,
        }
    }
    pub fn with_timeout(&mut self, timeout: u16) -> &mut Self {
        self.timeout = timeout;
        self
    }
    pub fn with_opt_pre_execute_javascript(
        &mut self,
        pre_execute_javascript: Option<String>,
    ) -> &mut Self {
        self.pre_execute_javascript = pre_execute_javascript;
        self
    }
    pub fn with_pre_execute_javascript(&mut self, pre_execute_javascript: String) -> &mut Self {
        self.pre_execute_javascript = Some(pre_execute_javascript);
        self
    }
    pub fn with_opt_post_execute_javascript(
        &mut self,
        post_execute_javascript: Option<String>,
    ) -> &mut Self {
        self.post_execute_javascript = post_execute_javascript;
        self
    }
    pub fn with_post_execute_javascript(&mut self, post_execute_javascript: String) -> &mut Self {
        self.post_execute_javascript = Some(post_execute_javascript);
        self
    }

    pub fn with_opt_listener(
        &mut self,
        listener: Option<RequestJavaScriptDatasetListenerArc>,
    ) -> &mut Self {
        self.listener = listener;
        self
    }

    pub fn with_listener(&mut self, listener: RequestJavaScriptDatasetListenerArc) -> &mut Self {
        self.listener.replace(listener);
        self
    }

    pub fn listener(&self) -> &Option<RequestJavaScriptDatasetListenerArc> {
        &self.listener
    }

    pub fn with_opt_config(
        &mut self,
        config: Option<RequestJavaScriptDatasetConfigArc>,
    ) -> &mut Self {
        self.config = config;
        self
    }

    pub fn with_config(&mut self, config: RequestJavaScriptDatasetConfigArc) -> &mut Self {
        self.config.replace(config);
        self
    }

    pub fn config(&self) -> &Option<RequestJavaScriptDatasetConfigArc> {
        &self.config
    }
}
