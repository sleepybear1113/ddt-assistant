class UserConfig {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.username = props.username;
        this.password = props.password;
        this.softId = props.softId;
        this.captureInterval = props.captureInterval;
        this.keyPressAfterCaptchaShow = props.keyPressAfterCaptchaShow;
        this.keyPressAfterCaptchaDisappear = props.keyPressAfterCaptchaDisappear;
        this.keyPressDelayAfterCaptchaDisappear = props.keyPressDelayAfterCaptchaDisappear;
        this.defaultChoiceAnswer = props.defaultChoiceAnswer;
        this.timeout = props.timeout;
        this.logPrintInterval = props.logPrintInterval;
        this.detectNewWindowInterval = props.detectNewWindowInterval;
        this.extraPorts = props.extraPorts;
    }
}

class BindResultVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.runningCount = props.runningCount;
        this.newAddCount = props.newAddCount;
    }
}

class TjResponse {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.success = props.success;
        this.code = props.code;
        this.message = props.message;
        this.cost = props.cost;
        this.choiceEnum = props.choiceEnum;
    }

    info() {
        return `状态:${this.success}, 耗时:${this.cost}, 答案:${this.choiceEnum}, 附加信息:${this.message}`;
    }
}

class TjPicResult {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.result = props.result;
    }
}

class TestRes {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.msg = props.msg;
        this.success = props.success;

    }

}

class AppInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.appVersion = props.appVersion;
    }
}

class StringRet {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.success = props.success;
        this.msg = props.msg;
    }
}