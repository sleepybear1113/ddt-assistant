class UserConfig {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.username = props.username;
        this.password = props.password;
        this.softId = props.softId;
        this.typeId = props.typeId;
        this.captureInterval = props.captureInterval;
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

        this.tj = new CaptchaTj(props.tj);
        this.pc = new CaptchaPc(props.pc);
        this.lowBalanceRemind = props.lowBalanceRemind;
        this.lowBalanceNum = props.lowBalanceNum;
        this.captchaWay = props.captchaWay;
        this.captchaChoiceEnumList = props.captchaChoiceEnumList;
    }
}

class CaptchaTj {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.serverAddr = props.serverAddr;
        this.username = props.username;
        this.password = props.password;
        this.softId = props.softId;
        this.typeId = props.typeId;
    }
}

class CaptchaPc {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.serverAddr = props.serverAddr;
        this.serverAddrList = props.serverAddrList;
        this.username = props.username;
        this.cami = props.cami;
        this.author = props.author;
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
    constructor(props) {
        if (props == null) {
            return;
        }

        this.keyPadPressWay = props.keyPadPressWay;
        this.email = new Email(props.email);
        this.loginConfig = new LoginConfig(props.loginConfig);
        this.updateConfig = new UpdateConfig(props.updateConfig);
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