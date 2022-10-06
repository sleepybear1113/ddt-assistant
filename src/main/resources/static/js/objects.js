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
        this.defaultChoiceAnswer = props.defaultChoiceAnswer;
        this.timeout = props.timeout;
        this.logPrintInterval = props.logPrintInterval;
        this.detectNewWindowInterval = props.detectNewWindowInterval;
        this.extPorts = props.extPorts;
        this.showExtPorts = props.showExtPorts;
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
        let items = props.auctionItemList;
        let list = [];
        if (items != null) {
            for (let i = 0; i < items.length; i++) {
                let item = items[i];
                let auctionItem = new AuctionItem(item);
                if (auctionItem == null) {
                    continue
                }
                list.push(auctionItem);
            }
        }

        this.auctionItemList = list;
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

class MemoryUseList {
    constructor(props) {
        if (props == null) {
            return;
        }
        this.memoryUserList = [];
        this.currentMemoryUse = props[props.length - 1].memory;
        this.maxMemoryUse = 0;
        for (let i = 0; i < props.length; i++) {
            let item = props[i];
            let memoryUse = new MemoryUse(item);
            this.memoryUserList.push(memoryUse);
            this.maxMemoryUse = Math.max(this.maxMemoryUse, memoryUse.memory);
        }
    }

    getData() {
        let list = [];
        for (let i = 0; i < this.memoryUserList.length; i++) {
            let item = this.memoryUserList[i];
            let memory = (item.memory / 1024 / 1024).toFixed(2);
            list.push([item.time, memory]);
        }
        return list;
    }
}

class MemoryUse {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.memory = props.memory;
        this.time = props.time;
    }
}

class SettingConfig {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.keyPadPressWay = props.keyPadPressWay;
        this.email = new Email(props.email);
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
    }
}

class OfflineDetection {
    constructor(props) {
        if (props == null) {
            return;
        }

        this.delay = props.delay;
        this.emailRemind = props.emailRemind;
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