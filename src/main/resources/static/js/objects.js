class UserConfig {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.noSaveCaptchaImg = props.noSaveCaptchaImg;
        this.captureInterval = props.captureInterval;
        this.captchaDetectType = props.captchaDetectType ? props.captchaDetectType : 0;
        this.captchaAppearDelay = props.captchaAppearDelay;
        this.keyPressAfterCaptchaShow = props.keyPressAfterCaptchaShow;
        this.keyPressAfterCaptchaDisappear = props.keyPressAfterCaptchaDisappear;
        this.keyPressDelayAfterCaptchaDisappear = props.keyPressDelayAfterCaptchaDisappear;
        this.pveFlopBonusAppearDelay = props.pveFlopBonusAppearDelay;
        this.keyPressAfterPveFlopBonus = props.keyPressAfterPveFlopBonus;
        this.pveFlopBonusDisappearDelay = props.pveFlopBonusDisappearDelay;
        this.keyPressAfterPveFlopBonusDisappear = props.keyPressAfterPveFlopBonusDisappear;
        this.pveFlopBonusCapture = props.pveFlopBonusCapture;
        this.pveFlopBonusCaptureMosaic = props.pveFlopBonusCaptureMosaic;
        this.defaultChoiceAnswer = props.defaultChoiceAnswer;
        this.timeout = props.timeout;
        this.logPrintInterval = props.logPrintInterval;
        this.detectNewWindowInterval = props.detectNewWindowInterval;
        this.lowBalanceRemind = props.lowBalanceRemind;
        this.lowBalanceNum = props.lowBalanceNum;
    }
}

class CaptchaConfig {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.captchaInfoList = props.captchaInfoList ? props.captchaInfoList.map(item => item) : [];
        this.lowBalanceRemind = props.lowBalanceRemind;
        this.lowBalanceNum = props.lowBalanceNum;
        this.captchaWay = props.captchaWay ? props.captchaWay.map(item => item) : [];

        if (this.captchaInfoList) {
            for (let i = 0; i < this.captchaInfoList.length; i++) {
                let captchaInfo = this.captchaInfoList[i];
                if (captchaInfo.captchaType === 1) {
                    this.captchaInfoList[i] = new CaptchaTj(captchaInfo);
                } else if (captchaInfo.captchaType === 2) {
                    this.captchaInfoList[i] = new CaptchaPc(captchaInfo);
                }
            }
        }
    }

    buildParamsAndReturn() {
        if (this.captchaInfoList) {
            for (let i = 0; i < this.captchaInfoList.length; i++) {
                let captchaInfo = this.captchaInfoList[i];
                captchaInfo.buildParams();
            }
        }
        return this;
    }
}

class CaptchaWay {
    constructor(props = {}) {
        this.id = props.id;
        this.captchaName = props.captchaName;
    }

    no() {
        let way = new CaptchaWay();
        way.id = 0;
        way.captchaName = "不打码";
        return way;
    }
}

class CaptchaTj {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.id = props.id;
        this.captchaName = props.captchaName;
        this.captchaType = props.captchaType;
        this.serverAddressList = props.serverAddressList ? props.serverAddressList.map(item => item) : [];
        this.validTimeList = props.validTimeList ? props.validTimeList.map(item => item) : [];
        this.params = props.params ? props.params.map(item => item) : [];

        this.username = ""
        this.password = ""
        this.softId = ""
        this.typeId = ""
        if (this.params) {
            switch (this.params.length) {
                case 4:
                    this.typeId = this.params[3];
                case 3:
                    this.softId = this.params[2];
                case 2:
                    this.password = this.params[1];
                case 1:
                    this.username = this.params[0];
                default:
                    break;
            }
        }
    }

    buildParams() {
        this.params = [];
        this.params.push(this.username);
        this.params.push(this.password);
        this.params.push(this.softId);
        this.params.push(this.typeId);
    }
}

class CaptchaPc {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.id = props.id;
        this.captchaType = props.captchaType;
        this.captchaName = props.captchaName;
        this.validTimeList = props.validTimeList ? props.validTimeList.map(item => item) : [];
        this.serverAddressList = props.serverAddressList ? props.serverAddressList.map(item => item) : [];
        this.params = props.params ? props.params.map(item => item) : [];

        this.cami = "";
        this.author = "";
        if (this.params) {
            switch (this.params.length) {
                case 2:
                    this.author = this.params[1];
                case 1:
                    this.cami = this.params[0];
                default:
                    break;
            }
        }
    }

    buildParams() {
        this.params = [];
        this.params.push(this.cami);
        this.params.push(this.author);
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

class AuctionData {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.auctionItemList = props.auctionItemList.map(e => new AuctionItem(e));
        this.autoAddUnknown = props.autoAddUnknown;
        this.confirm = props.confirm;
        this.sellType = props.sellType == null ? 1 : props.sellType;
    }
}

class AuctionItem {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.shown = true;
        this.name = props.name;
        this.ocrName = props.ocrName;
        this.enabled = props.enabled;
        this.argueUnitPrice = props.argueUnitPrice;
        this.mouthfulUnitPrice = props.mouthfulUnitPrice;
        this.minNum = props.minNum;
        this.auctionTime = props.auctionTime;
        this.drop = props.drop;
    }
}

class SettingConfig {
    constructor(props = {}) {
        this.keyPadPressWay = props.keyPadPressWay;
        this.bindWindowConfig = !props.bindWindowConfig ? new BindWindowConfig() : new BindWindowConfig(props.bindWindowConfig);
        this.email = new Email(props.email);
        this.loginConfig = new LoginConfig(props.loginConfig);
        this.updateConfig = new UpdateConfig(props.updateConfig);
    }
}

class BindWindowConfig {
    constructor(props = {}) {
        this.displayType = props.displayType ? props.displayType : "dx2";
        this.mouseType = props.mouseType ? props.mouseType : "windows";
        this.keypadType = props.keypadType ? props.keypadType : "windows";
    }

    getDisplayTypeList() {
        return ["normal", "gdi", "gdi2", "dx", "dx2", "dx3"];
    }

    getMouseTypeList() {
        return ["normal", "windows", "windows2", "windows3", "dx", "dx2"];
    }

    getKeypadTypeList() {
        return ["normal", "windows", "dx"];
    }
}

class Email {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.emailFrom = props.emailFrom;
        this.emailPassword = props.emailPassword;
        this.emailTo = props.emailTo;
        this.hostName = props.hostName;
        this.allowRemoteConnect = props.allowRemoteConnect;
        this.remoteSenderAddr = props.remoteSenderAddr;
        this.enableRemoteSender = props.enableRemoteSender;
        this.useRemoteLocalConfigFirst = props.useRemoteLocalConfigFirst;
    }
}

class LoginConfig {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.enableLogin = props.enableLogin;
        this.username = props.username;
        this.password = props.password;
    }
}

class OfflineDetection {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.delay = props.delay;
        this.emailRemind = props.emailRemind;
        this.disconnect = props.disconnect;
        this.tokenExpired = props.tokenExpired;
        this.offsite = props.offsite;
        this.leaveGame = props.leaveGame;
        this.whiteScreen = props.whiteScreen;
        this.whiteScreenLimit = props.whiteScreenLimit;
    }
}

class AuctionFilterButton {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.condition = props.condition;
        this.chosen = props.chosen === true;
    }
}

class RecaptureDomain {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.prefix = props.prefix;
        this.msg = props.msg;
        this.url = props.value;
        if (this.url.startsWith("/")) {
            this.url = this.url.slice(1);
        }
    }
}

class UpdateConfig {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.url = props.url;
        this.enableAutoCheckUpdate = props.enableAutoCheckUpdate;
        this.updateVersionType = props.updateVersionType;
    }
}

class UpdateInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.versionInfoList = props.versionInfoList.map(e => new MainVersionInfoVo(e));
        this.message = props.message;
        this.id = props.id;
        this.currentVersion = props.currentVersion;
    }
}

class MainVersionInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.appVersion = props.appVersion;
        this.version = props.version;
        this.versionName = props.versionName;
        this.updateMainFilePath = props.updateMainFilePath;
        this.info = props.info;
        this.updateListVo = new UpdateListVo(props.updateListVo);
    }
}

class UpdateListVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.appVersion = props.appVersion;
        this.version = props.version;
        this.baseUrl = props.baseUrl;
        this.statics = props.statics.map(e => new UpdateFileInfoVo(e));
    }
}

class UpdateFileInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.id = props.id;
        this.filename = props.filename;
        this.path = props.path;
        this.url = props.url;
        this.md5 = props.md5;
        this.remoteMd5 = props.remoteMd5;
        this.size = props.size;
        this.remoteSize = props.remoteSize;
        this.updateStrategy = props.updateStrategy;
        this.info = props.info;
        this.same = props.same;
    }
}

class DownloadFileInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.successCount = props.successCount;
        this.failCount = props.failCount;
    }

    info() {
        return `更新成功:${this.successCount}, 更新失败:${this.failCount}`;
    }
}

class FileInfoVo {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.filename = props.filename;
        this.size = props.size;
        this.absoluteFilename = props.absoluteFilename;
        this.isDir = props.isDir;
        this.lastModified = props.lastModified;
    }
}