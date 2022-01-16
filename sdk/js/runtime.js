/**
 * Base Runtime SDK.
 * @constructor
 */
function Runtime() {

    const RUNTIME_API_LEVEL_A = 1;

    prototype = {
        /**
         *
         * @returns {number} Api版本
         */
        getApiLevel: function () {
            return RUNTIME_API_LEVEL_A;
        },

        /**
         *
         * @returns {number} Runtime 版本号
         */
        getVersionCode: function () {
            return 1;
        },

        /**
         *
         * @returns {string} Runtime 版本名
         */
        getVersionName: function () {
            return "1.0";
        },

        /**
         *
         * @returns {string} Runtime build号
         */
        getBuild: function () {
            return "";
        },

        /**
         *
         * @returns {number} 设备类型: 0: 手机 1: 平板 2: 桌面
         */
        getDeviceType: function () {
            return 0;
        },

        /**
         *
         * @returns {boolean} 是否是手机
         */
        isMobilePhone: function () {
            return false;
        },

        /**
         *
         * @returns {boolean} 是否是平板
         */
        isTable: function () {
            return false;
        },

        /**
         *
         * @returns {boolean} 是否是桌面
         */
        isDesktop: function () {
            return false;
        },

        /**
         * 向应用端发送数据
         * @param type {number} 数据类型
         * @param data {Object} 数据
         */
        sendData: function (type, data) {
        },

        /**
         * 向应用端发送错误消息
         * @param errorMsg {string} 错误消息字符串
         */
        sendError: function (errorMsg) {
        },

        /**
         * 尝试执行method，catch到异常后自动调用sendError发送错误信息到应用端。
         * @param method {function}
         */
        tryRun: function (method) {
            try {
                method();
            } catch (e) {
                this.sendError(e.toString());
            }
        }
    };
}

window.runtime = new Runtime();

export default Runtime;

